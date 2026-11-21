package com.civicportal.service;

import com.civicportal.dto.assignment.AssignmentRequest;
import com.civicportal.dto.assignment.AssignmentResponse;
import com.civicportal.entity.Complaint;
import com.civicportal.entity.ComplaintAssignment;
import com.civicportal.entity.ComplaintStatusHistory;
import com.civicportal.entity.User;
import com.civicportal.enums.ComplaintStatus;
import com.civicportal.exception.BadRequestException;
import com.civicportal.exception.ResourceNotFoundException;
import com.civicportal.mapper.AssignmentMapper;
import com.civicportal.repository.ComplaintAssignmentRepository;
import com.civicportal.repository.ComplaintRepository;
import com.civicportal.repository.ComplaintStatusHistoryRepository;
import com.civicportal.repository.UserRepository;
import com.civicportal.security.SecurityUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final ComplaintAssignmentRepository assignmentRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final ComplaintStatusHistoryRepository statusHistoryRepository;
    private final AssignmentMapper assignmentMapper;

    public AssignmentServiceImpl(
            ComplaintAssignmentRepository assignmentRepository,
            ComplaintRepository complaintRepository,
            UserRepository userRepository,
            ComplaintStatusHistoryRepository statusHistoryRepository,
            AssignmentMapper assignmentMapper
    ) {
        this.assignmentRepository = assignmentRepository;
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.assignmentMapper = assignmentMapper;
    }

    @Override
    @Transactional
    public AssignmentResponse assignComplaint(
            Long complaintId,
            AssignmentRequest request
    ) {
        if (request == null || request.getOfficerId() == null) {
            throw new BadRequestException("An officer ID is required");
        }
        if (request.getDueDate() == null) {
            throw new BadRequestException("A due date is required");
        }
        if (request.getDueDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Due date cannot be in the past");
        }

        User assigningUser = getCurrentUser();
        requireAdminOrSupervisor(assigningUser);

        Complaint complaint = findComplaint(complaintId);
        if (complaint.getStatus() == ComplaintStatus.RESOLVED
                || complaint.getStatus() == ComplaintStatus.CLOSED
                || complaint.getStatus() == ComplaintStatus.REJECTED) {
            throw new BadRequestException(
                    "A resolved, closed, or rejected complaint cannot be assigned");
        }

        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Officer not found with ID: " + request.getOfficerId()));
        if (officer.getStatus() == null
                || !"ACTIVE".equalsIgnoreCase(officer.getStatus().name())) {
            throw new BadRequestException("The selected officer is not active");
        }
        if (officer.getRole() == null
                || !"OFFICER".equalsIgnoreCase(officer.getRole().getName())
                || !officer.getRole().isActive()) {
            throw new BadRequestException(
                    "The selected user does not have an active OFFICER role");
        }

        for (ComplaintAssignment existing :
                assignmentRepository.findByComplaintIdAndActiveTrue(complaintId)) {
            existing.setActive(false);
        }

        ComplaintAssignment assignment = new ComplaintAssignment();
        assignment.setComplaint(complaint);
        assignment.setOfficer(officer);
        assignment.setAssignedBy(assigningUser);
        assignment.setDueDate(request.getDueDate());
        assignment.setRemarks(request.getRemarks());
        assignment.setActive(true);

        ComplaintStatus oldStatus = complaint.getStatus();
        if (oldStatus != ComplaintStatus.ASSIGNED) {
            complaint.setStatus(ComplaintStatus.ASSIGNED);
            complaintRepository.save(complaint);

            ComplaintStatusHistory history = new ComplaintStatusHistory();
            history.setComplaint(complaint);
            history.setOldStatus(oldStatus);
            history.setNewStatus(ComplaintStatus.ASSIGNED);
            history.setChangedBy(assigningUser);
            history.setRemarks("Complaint assigned to " + officer.getFullName());
            statusHistoryRepository.save(history);
        }

        return assignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsForComplaint(Long complaintId) {
        User currentUser = getCurrentUser();
        Complaint complaint = findComplaint(complaintId);
        boolean isCitizenOwner = isRole(currentUser, "CITIZEN")
                && complaint.getCitizen() != null
                && complaint.getCitizen().getId().equals(currentUser.getId());
        boolean isAssignedOfficer = assignmentRepository
                .findByComplaintIdAndActiveTrue(complaintId)
                .stream()
                .anyMatch(assignment -> assignment.getOfficer().getId()
                        .equals(currentUser.getId()));

        if (!isCitizenOwner && !isAssignedOfficer
                && !hasStaffAccess(currentUser)) {
            throw new AccessDeniedException(
                    "You are not allowed to view these assignments");
        }

        return assignmentRepository.findByComplaintId(complaintId)
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsForCurrentOfficer() {
        User currentUser = getCurrentUser();
        if (!isRole(currentUser, "OFFICER")) {
            throw new AccessDeniedException("This list is available to officers only");
        }

        return assignmentRepository.findByOfficerIdAndActiveTrue(currentUser.getId())
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsForOfficer(Long officerId) {
        User currentUser = getCurrentUser();

        boolean viewingOwnAssignments = currentUser.getId().equals(officerId);

        if (!viewingOwnAssignments && !hasAdminOrSupervisorAccess(currentUser)) {
            throw new AccessDeniedException(
                    "You are not allowed to view this officer's assignments"
            );
        }

        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Officer not found with ID: " + officerId
                ));

        if (!isRole(officer, "OFFICER")) {
            throw new BadRequestException("The selected user is not an officer");
        }

        return assignmentRepository.findByOfficerId(officerId)
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AssignmentResponse completeAssignment(Long assignmentId) {
        User currentUser = getCurrentUser();
        ComplaintAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assignment not found with ID: " + assignmentId));

        boolean assignedOfficer = assignment.getOfficer() != null
                && assignment.getOfficer().getId().equals(currentUser.getId());
        if (!assignedOfficer && !hasAdminOrSupervisorAccess(currentUser)) {
            throw new AccessDeniedException(
                    "Only the assigned officer or a supervisor can complete this assignment");
        }
        if (!assignment.isActive()) {
            throw new BadRequestException("This assignment is already inactive");
        }

        assignment.setCompletedAt(LocalDateTime.now());
        assignment.setActive(false);
        return assignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    private Complaint findComplaint(Long complaintId) {
        if (complaintId == null) {
            throw new BadRequestException("Complaint ID is required");
        }
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
            throw new AccessDeniedException("Authentication is required");
        }

        Long userId = securityUser.getUser().getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user no longer exists"));
    }

    private void requireAdminOrSupervisor(User user) {
        if (!hasAdminOrSupervisorAccess(user)) {
            throw new AccessDeniedException(
                    "Only administrators and supervisors can assign complaints");
        }
    }

    private boolean hasStaffAccess(User user) {
        return hasAdminOrSupervisorAccess(user) || isRole(user, "OFFICER");
    }

    private boolean hasAdminOrSupervisorAccess(User user) {
        return isRole(user, "ADMIN") || isRole(user, "SUPERVISOR");
    }

    private boolean isRole(User user, String roleName) {
        return user.getRole() != null
                && user.getRole().isActive()
                && user.getRole().getName() != null
                && roleName.equals(user.getRole().getName()
                        .replaceFirst("^ROLE_", "")
                        .toUpperCase(Locale.ROOT));
    }
}
