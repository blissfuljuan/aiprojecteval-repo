package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AssignRequirementSetToClassRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AssignRequirementSetToProjectRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.DeactivateRequirementSetAssignmentRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetAssignmentResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetAssignmentSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import com.blissfuljuan.aiprojecteval.documentevaluation.mapper.DocumentRequirementSetAssignmentMapper;
import com.blissfuljuan.aiprojecteval.documentevaluation.mapper.DocumentRequirementSetMapper;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DocumentRequirementSetAssignmentServiceImpl implements DocumentRequirementSetAssignmentService {

	private final DocumentRequirementSetAssignmentRepository assignmentRepository;
	private final DocumentRequirementSetRepository requirementSetRepository;
	private final CourseClassRepository courseClassRepository;
	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;

	DocumentRequirementSetAssignmentServiceImpl(
			DocumentRequirementSetAssignmentRepository assignmentRepository,
			DocumentRequirementSetRepository requirementSetRepository,
			CourseClassRepository courseClassRepository,
			ProjectRepository projectRepository,
			UserRepository userRepository) {
		this.assignmentRepository = assignmentRepository;
		this.requirementSetRepository = requirementSetRepository;
		this.courseClassRepository = courseClassRepository;
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public DocumentRequirementSetAssignmentResponse assignToClass(
			String currentUserEmail,
			AssignRequirementSetToClassRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSet requirementSet = findRequirementSet(request.requirementSetId());
		CourseClass courseClass = findCourseClass(request.courseClassId());
		checkCanManageRequirementSet(currentUser, requirementSet);
		checkRequirementSetAssignable(requirementSet);
		if (assignmentRepository.existsByRequirementSetIdAndCourseClassIdAndStatus(
				requirementSet.getId(),
				courseClass.getId(),
				RequirementSetAssignmentStatus.ACTIVE)) {
			throw new BadRequestException("Requirement set is already actively assigned to this class");
		}

		DocumentRequirementSetAssignment assignment = new DocumentRequirementSetAssignment(
				requirementSet,
				RequirementSetAssignmentType.COURSE_CLASS,
				RequirementSetAssignmentStatus.ACTIVE,
				LocalDateTime.now());
		assignment.setCourseClass(courseClass);
		assignment.setAssignedBy(currentUser);
		assignment.setNotes(request.notes());

		return DocumentRequirementSetAssignmentMapper.toResponse(assignmentRepository.save(assignment));
	}

	@Override
	@Transactional
	public DocumentRequirementSetAssignmentResponse assignToProject(
			String currentUserEmail,
			AssignRequirementSetToProjectRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSet requirementSet = findRequirementSet(request.requirementSetId());
		Project project = findProject(request.projectId());
		checkCanManageRequirementSet(currentUser, requirementSet);
		checkRequirementSetAssignable(requirementSet);
		if (assignmentRepository.existsByRequirementSetIdAndProjectIdAndStatus(
				requirementSet.getId(),
				project.getId(),
				RequirementSetAssignmentStatus.ACTIVE)) {
			throw new BadRequestException("Requirement set is already actively assigned to this project");
		}

		DocumentRequirementSetAssignment assignment = new DocumentRequirementSetAssignment(
				requirementSet,
				RequirementSetAssignmentType.PROJECT,
				RequirementSetAssignmentStatus.ACTIVE,
				LocalDateTime.now());
		assignment.setProject(project);
		assignment.setAssignedBy(currentUser);
		assignment.setNotes(request.notes());

		return DocumentRequirementSetAssignmentMapper.toResponse(assignmentRepository.save(assignment));
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentRequirementSetAssignmentResponse getAssignmentById(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = findAssignment(id);
		checkCanViewAssignment(currentUser, assignment);

		return DocumentRequirementSetAssignmentMapper.toResponse(assignment);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentRequirementSetAssignmentSummaryResponse> getAssignmentsByClass(
			String currentUserEmail,
			Long courseClassId,
			RequirementSetAssignmentStatus status) {
		User currentUser = findUserByEmail(currentUserEmail);
		findCourseClass(courseClassId);
		return findClassAssignments(courseClassId, status)
				.stream()
				.filter(assignment -> canViewAssignment(currentUser, assignment))
				.map(DocumentRequirementSetAssignmentMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentRequirementSetAssignmentSummaryResponse> getAssignmentsByProject(
			String currentUserEmail,
			Long projectId,
			RequirementSetAssignmentStatus status) {
		User currentUser = findUserByEmail(currentUserEmail);
		findProject(projectId);
		return findProjectAssignments(projectId, status)
				.stream()
				.filter(assignment -> canViewAssignment(currentUser, assignment))
				.map(DocumentRequirementSetAssignmentMapper::toSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentRequirementSetResponse> getActiveRequirementSetsByClass(
			String currentUserEmail,
			Long courseClassId) {
		User currentUser = findUserByEmail(currentUserEmail);
		findCourseClass(courseClassId);
		return assignmentRepository.findByCourseClassIdAndAssignmentTypeAndStatus(
						courseClassId,
						RequirementSetAssignmentType.COURSE_CLASS,
						RequirementSetAssignmentStatus.ACTIVE)
				.stream()
				.filter(assignment -> canViewAssignment(currentUser, assignment))
				.map(DocumentRequirementSetAssignment::getRequirementSet)
				.map(DocumentRequirementSetMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentRequirementSetResponse> getActiveRequirementSetsByProject(
			String currentUserEmail,
			Long projectId) {
		User currentUser = findUserByEmail(currentUserEmail);
		findProject(projectId);
		return assignmentRepository.findByProjectIdAndAssignmentTypeAndStatus(
						projectId,
						RequirementSetAssignmentType.PROJECT,
						RequirementSetAssignmentStatus.ACTIVE)
				.stream()
				.filter(assignment -> canViewAssignment(currentUser, assignment))
				.map(DocumentRequirementSetAssignment::getRequirementSet)
				.map(DocumentRequirementSetMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public DocumentRequirementSetAssignmentResponse deactivateAssignment(
			String currentUserEmail,
			Long id,
			DeactivateRequirementSetAssignmentRequest request) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = findAssignment(id);
		checkCanManageAssignment(currentUser, assignment);

		assignment.setStatus(RequirementSetAssignmentStatus.INACTIVE);
		assignment.setDeactivatedAt(LocalDateTime.now());
		if (request != null && request.notes() != null) {
			assignment.setNotes(request.notes());
		}

		return DocumentRequirementSetAssignmentMapper.toResponse(assignmentRepository.save(assignment));
	}

	@Override
	@Transactional
	public DocumentRequirementSetAssignmentResponse archiveAssignment(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = findAssignment(id);
		checkCanManageAssignment(currentUser, assignment);

		assignment.setStatus(RequirementSetAssignmentStatus.ARCHIVED);
		if (assignment.getDeactivatedAt() == null) {
			assignment.setDeactivatedAt(LocalDateTime.now());
		}

		return DocumentRequirementSetAssignmentMapper.toResponse(assignmentRepository.save(assignment));
	}

	@Override
	@Transactional
	public DocumentRequirementSetAssignmentResponse reactivateAssignment(String currentUserEmail, Long id) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentRequirementSetAssignment assignment = findAssignment(id);
		checkCanManageAssignment(currentUser, assignment);
		checkRequirementSetAssignable(assignment.getRequirementSet());
		if (assignment.getStatus() == RequirementSetAssignmentStatus.ACTIVE) {
			return DocumentRequirementSetAssignmentMapper.toResponse(assignment);
		}
		checkNoDuplicateActiveAssignment(assignment);

		assignment.setStatus(RequirementSetAssignmentStatus.ACTIVE);
		assignment.setDeactivatedAt(null);

		return DocumentRequirementSetAssignmentMapper.toResponse(assignmentRepository.save(assignment));
	}

	private List<DocumentRequirementSetAssignment> findClassAssignments(
			Long courseClassId,
			RequirementSetAssignmentStatus status) {
		if (status == null) {
			return assignmentRepository.findByCourseClassId(courseClassId);
		}
		return assignmentRepository.findByCourseClassIdAndStatus(courseClassId, status);
	}

	private List<DocumentRequirementSetAssignment> findProjectAssignments(
			Long projectId,
			RequirementSetAssignmentStatus status) {
		if (status == null) {
			return assignmentRepository.findByProjectId(projectId);
		}
		return assignmentRepository.findByProjectIdAndStatus(projectId, status);
	}

	private DocumentRequirementSetAssignment findAssignment(Long id) {
		return assignmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Requirement set assignment not found"));
	}

	private DocumentRequirementSet findRequirementSet(Long id) {
		return requirementSetRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document requirement set not found"));
	}

	private CourseClass findCourseClass(Long id) {
		return courseClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Course class not found"));
	}

	private Project findProject(Long id) {
		return projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private void checkRequirementSetAssignable(DocumentRequirementSet requirementSet) {
		if (requirementSet.getStatus() != ConfigurationStatus.ACTIVE) {
			throw new BadRequestException("Only active requirement sets can be assigned");
		}
	}

	private void checkNoDuplicateActiveAssignment(DocumentRequirementSetAssignment assignment) {
		if (assignment.getAssignmentType() == RequirementSetAssignmentType.COURSE_CLASS
				&& assignmentRepository.existsByRequirementSetIdAndCourseClassIdAndStatus(
						assignment.getRequirementSet().getId(),
						assignment.getCourseClass().getId(),
						RequirementSetAssignmentStatus.ACTIVE)) {
			throw new BadRequestException("Requirement set is already actively assigned to this class");
		}
		if (assignment.getAssignmentType() == RequirementSetAssignmentType.PROJECT
				&& assignmentRepository.existsByRequirementSetIdAndProjectIdAndStatus(
						assignment.getRequirementSet().getId(),
						assignment.getProject().getId(),
						RequirementSetAssignmentStatus.ACTIVE)) {
			throw new BadRequestException("Requirement set is already actively assigned to this project");
		}
	}

	private void checkCanViewAssignment(User user, DocumentRequirementSetAssignment assignment) {
		if (!canViewAssignment(user, assignment)) {
			throw new AccessDeniedException("Access denied");
		}
	}

	private void checkCanManageAssignment(User user, DocumentRequirementSetAssignment assignment) {
		checkCanManageRequirementSet(user, assignment.getRequirementSet());
	}

	private void checkCanManageRequirementSet(User user, DocumentRequirementSet requirementSet) {
		if (user.getRole() == Role.ADMIN) {
			return;
		}
		if (user.getRole() == Role.INSTRUCTOR
				&& requirementSet.getOwnerInstructor() != null
				&& user.getId().equals(requirementSet.getOwnerInstructor().getId())) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private boolean canViewAssignment(User user, DocumentRequirementSetAssignment assignment) {
		if (user.getRole() == Role.ADMIN) {
			return true;
		}
		DocumentRequirementSet requirementSet = assignment.getRequirementSet();
		return user.getRole() == Role.INSTRUCTOR
				&& requirementSet.getOwnerInstructor() != null
				&& user.getId().equals(requirementSet.getOwnerInstructor().getId());
	}
}
