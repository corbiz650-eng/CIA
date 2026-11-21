package com.civicportal.service;

import com.civicportal.dto.evidence.EvidenceResponse;
import com.civicportal.dto.evidence.EvidenceUploadResponse;
import com.civicportal.entity.Complaint;
import com.civicportal.entity.ComplaintEvidence;
import com.civicportal.entity.User;
import com.civicportal.exception.BadRequestException;
import com.civicportal.exception.FileStorageException;
import com.civicportal.exception.ResourceNotFoundException;
import com.civicportal.mapper.EvidenceMapper;
import com.civicportal.repository.ComplaintEvidenceRepository;
import com.civicportal.repository.ComplaintRepository;
import com.civicportal.repository.UserRepository;
import com.civicportal.security.SecurityUser;
import com.civicportal.util.FileHashUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class EvidenceServiceImpl implements EvidenceService {

    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "application/pdf", ".pdf",
            "video/mp4", ".mp4"
    );

    private final ComplaintEvidenceRepository evidenceRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final EvidenceMapper evidenceMapper;
    private final Path storageDirectory;
    private final int maxFilesPerComplaint;
    private final long maxFileSizeBytes;

    public EvidenceServiceImpl(
            ComplaintEvidenceRepository evidenceRepository,
            ComplaintRepository complaintRepository,
            UserRepository userRepository,
            EvidenceMapper evidenceMapper,
            @Value("${app.evidence.storage-dir:uploads/evidence}") String storageDirectory,
            @Value("${app.evidence.max-files-per-complaint:5}") int maxFilesPerComplaint,
            @Value("${app.evidence.max-file-size-bytes:10485760}") long maxFileSizeBytes
    ) {
        this.evidenceRepository = evidenceRepository;
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.evidenceMapper = evidenceMapper;
        this.storageDirectory = Path.of(storageDirectory)
                .toAbsolutePath()
                .normalize();
        this.maxFilesPerComplaint = maxFilesPerComplaint;
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    @PostConstruct
    void validateConfiguration() {
        if (maxFilesPerComplaint < 1 || maxFileSizeBytes < 1) {
            throw new IllegalArgumentException(
                    "Evidence file limits must be positive numbers");
        }
    }

    @Override
    @Transactional
    public EvidenceUploadResponse uploadEvidence(
            Long complaintId,
            MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("An evidence file is required");
        }

        Complaint complaint = findComplaint(complaintId);
        User uploader = getCurrentUser();
        ensureCanAccessComplaint(uploader, complaint);

        if (evidenceRepository.findByComplaintId(complaintId).size()
                >= maxFilesPerComplaint) {
            throw new BadRequestException(
                    "A complaint cannot have more than "
                            + maxFilesPerComplaint + " evidence files");
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw new BadRequestException(
                    "Evidence file exceeds the maximum size of "
                            + maxFileSizeBytes + " bytes");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.containsKey(contentType)) {
            throw new BadRequestException("This evidence file type is not allowed");
        }

        verifyFileSignature(file, contentType);
        String fileHash = calculateHash(file);
        String storageName = UUID.randomUUID() + ALLOWED_TYPES.get(contentType);
        Path target = storageDirectory.resolve(storageName).normalize();

        if (!target.startsWith(storageDirectory)) {
            throw new FileStorageException("Invalid evidence storage path");
        }

        try {
            Files.createDirectories(storageDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }

            ComplaintEvidence evidence = new ComplaintEvidence();
            evidence.setComplaint(complaint);
            evidence.setFileName(safeOriginalFileName(file.getOriginalFilename()));
            evidence.setFileType(contentType);
            evidence.setFileSize(file.getSize());
            evidence.setStoragePath(storageName);
            evidence.setFileHash(fileHash);
            evidence.setUploadedBy(uploader);

            ComplaintEvidence saved = evidenceRepository.save(evidence);
            return evidenceMapper.toUploadResponse(
                    saved,
                    "Evidence uploaded successfully"
            );
        } catch (IOException exception) {
            deleteStoredFile(target);
            throw new FileStorageException("Could not store evidence file", exception);
        } catch (RuntimeException exception) {
            deleteStoredFile(target);
            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceResponse> getEvidenceForComplaint(Long complaintId) {
        Complaint complaint = findComplaint(complaintId);
        ensureCanAccessComplaint(getCurrentUser(), complaint);

        return evidenceRepository.findByComplaintId(complaintId)
                .stream()
                .map(evidenceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EvidenceResponse getEvidenceById(Long evidenceId) {
        ComplaintEvidence evidence = findEvidence(evidenceId);
        ensureCanAccessComplaint(getCurrentUser(), evidence.getComplaint());

        return evidenceMapper.toResponse(evidence);
    }

    @Override
    @Transactional
    public void deleteEvidence(Long evidenceId) {
        ComplaintEvidence evidence = findEvidence(evidenceId);
        ensureCanAccessComplaint(getCurrentUser(), evidence.getComplaint());

        Path storedFile = storageDirectory
                .resolve(evidence.getStoragePath())
                .normalize();

        if (!storedFile.startsWith(storageDirectory)) {
            throw new FileStorageException("Invalid evidence storage path");
        }

        try {
            Files.deleteIfExists(storedFile);
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Could not delete evidence file",
                    exception
            );
        }

        evidenceRepository.delete(evidence);
    }

    private ComplaintEvidence findEvidence(Long evidenceId) {
        return evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evidence not found with ID: " + evidenceId
                ));
    }

    private void verifyFileSignature(MultipartFile file, String contentType) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = inputStream.readNBytes(12);
            boolean valid = switch (contentType) {
                case "image/jpeg" -> header.length >= 3
                        && (header[0] & 0xFF) == 0xFF
                        && (header[1] & 0xFF) == 0xD8
                        && (header[2] & 0xFF) == 0xFF;
                case "image/png" -> header.length >= 8
                        && (header[0] & 0xFF) == 0x89
                        && header[1] == 0x50
                        && header[2] == 0x4E
                        && header[3] == 0x47
                        && header[4] == 0x0D
                        && header[5] == 0x0A
                        && header[6] == 0x1A
                        && header[7] == 0x0A;
                case "application/pdf" -> header.length >= 5
                        && header[0] == 0x25
                        && header[1] == 0x50
                        && header[2] == 0x44
                        && header[3] == 0x46
                        && header[4] == 0x2D;
                case "video/mp4" -> header.length >= 8
                        && header[4] == 0x66
                        && header[5] == 0x74
                        && header[6] == 0x79
                        && header[7] == 0x70;
                default -> false;
            };

            if (!valid) {
                throw new BadRequestException(
                        "File contents do not match the declared content type");
            }
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Could not read evidence file signature",
                    exception
            );
        }
    }

    private String calculateHash(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return FileHashUtil.sha256(inputStream);
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Could not calculate evidence file hash",
                    exception
            );
        }
    }

    private String safeOriginalFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "evidence";
        }

        String baseName = originalFileName.replace('\\', '/');
        baseName = baseName.substring(baseName.lastIndexOf('/') + 1);
        baseName = baseName.replaceAll("[\\p{Cntrl}]", "_").trim();

        if (baseName.isBlank()) {
            return "evidence";
        }
        return baseName.length() <= 255 ? baseName : baseName.substring(0, 255);
    }

    private Complaint findComplaint(Long complaintId) {
        return complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Complaint not found with ID: " + complaintId));
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Authentication is required");
        }

        Long userId = securityUser.getUser().getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user no longer exists"));
    }

    private void ensureCanAccessComplaint(User user, Complaint complaint) {
        if (user.getRole() != null
                && "CITIZEN".equalsIgnoreCase(user.getRole().getName())
                && !complaint.getCitizen().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to access this complaint's evidence");
        }
    }

    private void deleteStoredFile(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // Preserve the original storage or database failure.
        }
    }
}
