package com.civicportal.service;

import com.civicportal.dto.complaint.ComplaintCreateRequest;
import com.civicportal.dto.complaint.ComplaintFilterRequest;
import com.civicportal.dto.complaint.ComplaintResponse;
import com.civicportal.dto.complaint.ComplaintStatusUpdateRequest;
import com.civicportal.dto.complaint.ComplaintSummaryResponse;
import com.civicportal.dto.complaint.ComplaintUpdateRequest;
import com.civicportal.entity.Complaint;
import com.civicportal.entity.ComplaintCategory;
import com.civicportal.entity.ComplaintStatusHistory;
import com.civicportal.entity.User;
import com.civicportal.entity.Ward;
import com.civicportal.enums.ComplaintPriority;
import com.civicportal.enums.ComplaintStatus;
import com.civicportal.exception.BadRequestException;
import com.civicportal.exception.ResourceNotFoundException;
import com.civicportal.exception.UnauthorizedException;
import com.civicportal.mapper.ComplaintMapper;
import com.civicportal.repository.ComplaintCategoryRepository;
import com.civicportal.repository.ComplaintRepository;
import com.civicportal.repository.ComplaintStatusHistoryRepository;
import com.civicportal.repository.UserRepository;
import com.civicportal.repository.WardRepository;
import com.civicportal.security.SecurityUser;
import com.civicportal.util.ComplaintNumberGenerator;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintCategoryRepository categoryRepository;
    private final WardRepository wardRepository;
    private final UserRepository userRepository;
    private final ComplaintStatusHistoryRepository statusHistoryRepository;
    private final ComplaintMapper complaintMapper;

    public ComplaintServiceImpl(
            ComplaintRepository complaintRepository,
            ComplaintCategoryRepository categoryRepository,
            WardRepository wardRepository,
            UserRepository userRepository,
            ComplaintStatusHistoryRepository statusHistoryRepository,
            ComplaintMapper complaintMapper
    ) {
        this.complaintRepository = complaintRepository;
        this.categoryRepository = categoryRepository;
        this.wardRepository = wardRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.complaintMapper = complaintMapper;
    }

    @Override
    @Transactional
    public ComplaintResponse createComplaint(ComplaintCreateRequest request) {
        User citizen = getCurrentUser();

        Complaint complaint = new Complaint();
        complaint.setComplaintNumber(generateUniqueComplaintNumber());
        complaint.setCitizen(citizen);
        complaint.setCategory(findActiveCategory(request.getCategoryId()));
        complaint.setWard(findActiveWard(request.getWardId()));
        complaint.setDescription(requireText(request.getDescription(), "Description"));
        complaint.setAddress(requireText(request.getAddress(), "Address"));
        complaint.setLatitude(request.getLatitude());
        complaint.setLongitude(request.getLongitude());
        complaint.setPriority(ComplaintPriority.MEDIUM);
        complaint.setStatus(ComplaintStatus.SUBMITTED);

        return complaintMapper.toResponse(complaintRepository.save(complaint));
    }

    @Override
    @Transactional
    public ComplaintResponse updateComplaint(
            Long complaintId,
            ComplaintUpdateRequest request
    ) {
        User actor = getCurrentUser();
        Complaint complaint = findComplaint(complaintId);
        ensureCitizenOwnsComplaint(actor, complaint);

        if (complaint.getStatus() != ComplaintStatus.SUBMITTED) {
            throw new BadRequestException(
                    "Only submitted complaints can be edited by the citizen");
        }

        if (request.getDescription() != null) {
            complaint.setDescription(
                    requireText(request.getDescription(), "Description"));
        }
        if (request.getCategoryId() != null) {
            complaint.setCategory(findActiveCategory(request.getCategoryId()));
        }
        if (request.getWardId() != null) {
            complaint.setWard(findActiveWard(request.getWardId()));
        }
        if (request.getAddress() != null) {
            complaint.setAddress(requireText(request.getAddress(), "Address"));
        }
        if (request.getLatitude() != null) {
            complaint.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            complaint.setLongitude(request.getLongitude());
        }

        return complaintMapper.toResponse(complaintRepository.save(complaint));
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(Long complaintId) {
        User actor = getCurrentUser();
        Complaint complaint = findComplaint(complaintId);
        ensureCanView(actor, complaint);
        return complaintMapper.toResponse(complaint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintSummaryResponse> filterComplaints(
            ComplaintFilterRequest request
    ) {
        User actor = getCurrentUser();
        validateDateRange(request);

        Specification<Complaint> specification = buildSpecification(request);
        if (isCitizen(actor)) {
            Long citizenId = actor.getId();
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("citizen").get("id"), citizenId));
        }

        return complaintRepository.findAll(specification)
                .stream()
                .map(complaintMapper::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional
    public ComplaintResponse updateComplaintStatus(
            Long complaintId,
            ComplaintStatusUpdateRequest request
    ) {
        User actor = getCurrentUser();
        ensureStaff(actor);

        Complaint complaint = findComplaint(complaintId);
        ComplaintStatus oldStatus = complaint.getStatus();
        ComplaintStatus newStatus = request.getStatus();

        if (newStatus == null) {
            throw new BadRequestException("New complaint status is required");
        }
        if (!isAllowedTransition(oldStatus, newStatus)) {
            throw new BadRequestException(
                    "Invalid complaint status transition from "
                            + oldStatus + " to " + newStatus);
        }

        complaint.setStatus(newStatus);
        complaintRepository.save(complaint);

        ComplaintStatusHistory history = new ComplaintStatusHistory();
        history.setComplaint(complaint);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(actor);
        history.setRemarks(request.getRemarks());
        statusHistoryRepository.save(history);

        return complaintMapper.toResponse(complaint);
    }

    private Specification<Complaint> buildSpecification(
            ComplaintFilterRequest request
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"), request.getStatus()));
            }
            if (request.getPriority() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("priority"), request.getPriority()));
            }
            if (request.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("category").get("id"), request.getCategoryId()));
            }
            if (request.getWardId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("ward").get("id"), request.getWardId()));
            }
            if (request.getComplaintNumber() != null
                    && !request.getComplaintNumber().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("complaintNumber"),
                        request.getComplaintNumber().trim()));
            }
            if (request.getCreatedFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("submittedAt"),
                        request.getCreatedFrom().atStartOfDay()));
            }
            if (request.getCreatedTo() != null) {
                predicates.add(criteriaBuilder.lessThan(
                        root.get("submittedAt"),
                        request.getCreatedTo().plusDays(1).atStartOfDay()));
            }

            query.orderBy(criteriaBuilder.desc(root.get("submittedAt")));
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void validateDateRange(ComplaintFilterRequest request) {
        if (request.getCreatedFrom() != null
                && request.getCreatedTo() != null
                && request.getCreatedFrom().isAfter(request.getCreatedTo())) {
            throw new BadRequestException(
                    "createdFrom must be on or before createdTo");
        }
    }

    private boolean isAllowedTransition(
            ComplaintStatus current,
            ComplaintStatus next
    ) {
        if (current == null || next == null || current == next) {
            return false;
        }

        Set<ComplaintStatus> allowed = switch (current) {
            case SUBMITTED -> Set.of(
                    ComplaintStatus.UNDER_REVIEW,
                    ComplaintStatus.REJECTED
            );
            case UNDER_REVIEW -> Set.of(
                    ComplaintStatus.VERIFIED,
                    ComplaintStatus.INSUFFICIENT_EVIDENCE,
                    ComplaintStatus.REJECTED
            );
            case VERIFIED -> Set.of(
                    ComplaintStatus.ASSIGNED,
                    ComplaintStatus.REJECTED
            );
            case ASSIGNED -> Set.of(
                    ComplaintStatus.INVESTIGATING,
                    ComplaintStatus.REJECTED
            );
            case INVESTIGATING -> Set.of(
                    ComplaintStatus.RESOLVED,
                    ComplaintStatus.INSUFFICIENT_EVIDENCE,
                    ComplaintStatus.REJECTED
            );
            case INSUFFICIENT_EVIDENCE -> Set.of(
                    ComplaintStatus.UNDER_REVIEW,
                    ComplaintStatus.REJECTED,
                    ComplaintStatus.CLOSED
            );
            case RESOLVED, REJECTED -> Set.of(ComplaintStatus.CLOSED);
            case CLOSED -> Set.of();
        };

        return allowed.contains(next);
    }

    private String generateUniqueComplaintNumber() {
        for (int attempt = 0; attempt < 5; attempt++) {
            String number = ComplaintNumberGenerator.generate();
            if (!complaintRepository.existsByComplaintNumber(number)) {
                return number;
            }
        }
        throw new IllegalStateException(
                "Could not generate a unique complaint number");
    }

    private Complaint findComplaint(Long complaintId) {
        return complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Complaint not found with ID: " + complaintId));
    }

    private ComplaintCategory findActiveCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .filter(ComplaintCategory::isActive)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active complaint category not found with ID: " + categoryId));
    }

    private Ward findActiveWard(Long wardId) {
        return wardRepository.findById(wardId)
                .filter(Ward::isActive)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active ward not found with ID: " + wardId));
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new UnauthorizedException("Authentication is required");
        }

        Long userId = securityUser.getUser().getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(
                        "Authenticated user no longer exists"));
    }

    private void ensureCanView(User actor, Complaint complaint) {
        if (isCitizen(actor)
                && !complaint.getCitizen().getId().equals(actor.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to access this complaint");
        }
    }

    private void ensureCitizenOwnsComplaint(User actor, Complaint complaint) {
        if (!isCitizen(actor)
                || !complaint.getCitizen().getId().equals(actor.getId())) {
            throw new AccessDeniedException(
                    "Only the complaint's citizen can edit it");
        }
    }

    private void ensureStaff(User actor) {
        if (isCitizen(actor)) {
            throw new AccessDeniedException(
                    "Only staff can change complaint status");
        }
    }

    private boolean isCitizen(User user) {
        return user.getRole() != null
                && "CITIZEN".equalsIgnoreCase(user.getRole().getName());
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
