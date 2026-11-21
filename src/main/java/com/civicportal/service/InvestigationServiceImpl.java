package com.civicportal.service;

import com.civicportal.dto.investigation.InvestigationCreateRequest;
import com.civicportal.dto.investigation.InvestigationResponse;
import com.civicportal.dto.investigation.InvestigationUpdateRequest;
import com.civicportal.entity.*;
import com.civicportal.enums.ComplaintStatus;
import com.civicportal.exception.BadRequestException;
import com.civicportal.exception.ResourceNotFoundException;
import com.civicportal.mapper.InvestigationMapper;
import com.civicportal.repository.*;
import com.civicportal.security.SecurityUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class InvestigationServiceImpl implements InvestigationService {
    private final InvestigationRepository investigationRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintAssignmentRepository assignmentRepository;
    private final ComplaintStatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final InvestigationMapper investigationMapper;

    public InvestigationServiceImpl(
            InvestigationRepository investigationRepository,
            ComplaintRepository complaintRepository,
            ComplaintAssignmentRepository assignmentRepository,
            ComplaintStatusHistoryRepository statusHistoryRepository,
            UserRepository userRepository,
           InvestigationMapper investigationMapper
    ) {
        this.investigationRepository = investigationRepository;
        this.complaintRepository = complaintRepository;
        this.assignmentRepository = assignmentRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.userRepository = userRepository;
        this.investigationMapper = investigationMapper;
    }

    @Override
    @Transactional
    public InvestigationResponse createInvestigation(
            Long complaintId,
            InvestigationCreateRequest request
    ) {
        if (request == null) {
            throw new BadRequestException("Investigation request is required");
        }

        User investigator = getCurrentUser();
        Complaint complaint = findComplaint(complaintId);
        requireCanInvestigate(investigator, complaint);

        if (complaint.getStatus() == ComplaintStatus.RESOLVED
                || complaint.getStatus() == ComplaintStatus.CLOSED
                || complaint.getStatus() == ComplaintStatus.REJECTED) {
            throw new BadRequestException(
                    "A resolved, closed, or rejected complaint cannot be investigated");
        }

        Investigation investigation = new Investigation();
        investigation.setComplaint(complaint);
        investigation.setInvestigator(investigator);
        investigation.setFindings(orEmpty(request.getFindings()));
        investigation.setRemarks(orEmpty(request.getRemarks()));
        investigation.setOffenderIdentified("UNKNOWN");

        ComplaintStatus oldStatus = complaint.getStatus();
        if (oldStatus != ComplaintStatus.INVESTIGATING) {
            complaint.setStatus(ComplaintStatus.INVESTIGATING);
            complaintRepository.save(complaint);
            saveStatusHistory(
                    complaint,
                    oldStatus,
                    ComplaintStatus.INVESTIGATING,
                    investigator,
                    "Investigation started"
            );
        }

        return investigationMapper.toResponse(
                investigationRepository.save(investigation)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestigationResponse> getInvestigationsForComplaint(
            Long complaintId
    ) {
        User currentUser = getCurrentUser();
        Complaint complaint = findComplaint(complaintId);
        requireCanView(currentUser, complaint);

        return investigationRepository.findByComplaintId(complaintId)
                .stream()
                .map(investigationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public InvestigationResponse updateInvestigation(
            Long investigationId,
            InvestigationUpdateRequest request
    ) {
        if (request == null) {
            throw new BadRequestException("Investigation update is required");
        }

        User currentUser = getCurrentUser();
        Investigation investigation = findInvestigation(investigationId);
        requireCanEdit(currentUser, investigation);

        if (investigation.getCompletedAt() != null) {
            throw new BadRequestException(
                    "A completed investigation cannot be edited");
        }

        if (request.getFindings() != null) {
            investigation.setFindings(request.getFindings());
        }
        if (request.getRemarks() != null) {
            investigation.setRemarks(request.getRemarks());
        }

        return investigationMapper.toResponse(
                investigationRepository.save(investigation)
        );
    }

    @Override
    @Transactional
    public InvestigationResponse completeInvestigation(Long investigationId) {
        User currentUser = getCurrentUser();
        Investigation investigation = findInvestigation(investigationId);
        requireCanEdit(currentUser, investigation);

        if (investigation.getCompletedAt() != null) {
            throw new BadRequestException("Investigation is already completed");
        }

        investigation.setCompletedAt(LocalDateTime.now());

        return investigationMapper.toResponse(
                investigationRepository.save(investigation)
        );
    }

    private void requireCanInvestigate(User user, Complaint complaint) {
        if (isSupervisor(user)) {
            return;
        }

        boolean isAssignedOfficer = isOfficer(user)
                && assignmentRepository
                .findByComplaintIdAndActiveTrue(complaint.getId())
                .stream()
                .map(ComplaintAssignment::getOfficer)
                .anyMatch(officer -> officer.getId().equals(user.getId()));

        if (!isAssignedOfficer) {
            throw new AccessDeniedException(
                    "Only the assigned officer or a supervisor can investigate this complaint");
        }
    }

    private void requireCanView(User user, Complaint complaint) {
        boolean isCitizenOwner = isCitizen(user)
                && complaint.getCitizen() != null
                && complaint.getCitizen().getId().equals(user.getId());

        if (!isCitizenOwner && !isStaff(user)) {
            throw new AccessDeniedException(
                    "You are not allowed to view this investigation");
        }
    }

    private void requireCanEdit(User user, Investigation investigation) {
        boolean isInvestigator = investigation.getInvestigator() != null
                && investigation.getInvestigator().getId().equals(user.getId());

        if (!isInvestigator && !isSupervisor(user)) {
            throw new AccessDeniedException(
                    "Only the investigator or a supervisor can edit this investigation");
        }
    }

    private boolean isStaff(User user) {
        return isOfficer(user) || isSupervisor(user);
    }

    private boolean isOfficer(User user) {
        return hasRole(user, "OFFICER");
    }

    private boolean isSupervisor(User user) {
        return hasRole(user, "ADMIN") || hasRole(user, "SUPERVISOR");
    }

    private boolean isCitizen(User user) {
        return hasRole(user, "CITIZEN");
    }

    private boolean hasRole(User user, String expectedRole) {
        return user.getRole() != null
                && user.getRole().isActive()
                && user.getRole().getName() != null
                && expectedRole.equals(
                user.getRole().getName()
                        .replaceFirst("^ROLE_", "")
                        .toUpperCase(Locale.ROOT)
        );
    }

    private String orEmpty(String value) {
        return value == null ? "" : value;
    }

    private Complaint findComplaint(Long complaintId) {
        return complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Complaint not found with ID: " + complaintId));
    }

    private Investigation findInvestigation(Long investigationId) {
        return investigationRepository.findById(investigationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Investigation not found with ID: " + investigationId));
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new AccessDeniedException("Authentication is required");
        }

        Long userId = securityUser.getUser().getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user no longer exists"));
    }

    private void saveStatusHistory(
            Complaint complaint,
            ComplaintStatus oldStatus,
            ComplaintStatus newStatus,
            User updatedBy,
            String remarks
    ) {
        ComplaintStatusHistory history = new ComplaintStatusHistory();
        history.setComplaint(complaint);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(updatedBy);
        history.setRemarks(remarks);
        statusHistoryRepository.save(history);
    }

}