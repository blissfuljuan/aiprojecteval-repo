package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AssignRequirementSetToClassRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AssignRequirementSetToProjectRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.DeactivateRequirementSetAssignmentRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetAssignmentResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class DocumentRequirementSetAssignmentServiceImplTest {

	@Mock
	private DocumentRequirementSetAssignmentRepository assignmentRepository;

	@Mock
	private DocumentRequirementSetRepository requirementSetRepository;

	@Mock
	private CourseClassRepository courseClassRepository;

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private SubmissionRepository submissionRepository;

	private DocumentRequirementSetAssignmentServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new DocumentRequirementSetAssignmentServiceImpl(
				assignmentRepository,
				requirementSetRepository,
				courseClassRepository,
				projectRepository,
				userRepository,
				submissionRepository);
	}

	@Test
	void shouldAssignActiveRequirementSetToClass() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.ACTIVE);
		CourseClass courseClass = courseClass(200L);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(courseClassRepository.findById(200L)).thenReturn(Optional.of(courseClass));
		when(assignmentRepository.existsByRequirementSetIdAndCourseClassIdAndStatus(
				100L, 200L, RequirementSetAssignmentStatus.ACTIVE)).thenReturn(false);
		when(assignmentRepository.save(any(DocumentRequirementSetAssignment.class)))
				.thenAnswer(invocation -> {
					DocumentRequirementSetAssignment assignment = invocation.getArgument(0);
					assignment.setId(300L);
					return assignment;
				});

		DocumentRequirementSetAssignmentResponse response = service.assignToClass(
				"instructor@example.com",
				new AssignRequirementSetToClassRequest(100L, 200L, "Required for class"));

		assertThat(response.id()).isEqualTo(300L);
		assertThat(response.assignmentType()).isEqualTo(RequirementSetAssignmentType.COURSE_CLASS);
		assertThat(response.status()).isEqualTo(RequirementSetAssignmentStatus.ACTIVE);
		assertThat(response.requirementSet().id()).isEqualTo(100L);
		assertThat(response.courseClass().id()).isEqualTo(200L);
		assertThat(response.project()).isNull();
		assertThat(response.assignedBy().id()).isEqualTo(10L);
		assertThat(response.notes()).isEqualTo("Required for class");
	}

	@Test
	void shouldAssignActiveRequirementSetToProject() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.ACTIVE);
		Project project = project(200L);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(projectRepository.findById(200L)).thenReturn(Optional.of(project));
		when(assignmentRepository.existsByRequirementSetIdAndProjectIdAndStatus(
				100L, 200L, RequirementSetAssignmentStatus.ACTIVE)).thenReturn(false);
		when(assignmentRepository.save(any(DocumentRequirementSetAssignment.class)))
				.thenAnswer(invocation -> {
					DocumentRequirementSetAssignment assignment = invocation.getArgument(0);
					assignment.setId(300L);
					return assignment;
				});

		DocumentRequirementSetAssignmentResponse response = service.assignToProject(
				"instructor@example.com",
				new AssignRequirementSetToProjectRequest(100L, 200L, "Required for project"));

		assertThat(response.assignmentType()).isEqualTo(RequirementSetAssignmentType.PROJECT);
		assertThat(response.status()).isEqualTo(RequirementSetAssignmentStatus.ACTIVE);
		assertThat(response.project().id()).isEqualTo(200L);
		assertThat(response.courseClass()).isNull();
	}

	@Test
	void shouldRejectAssigningArchivedRequirementSet() {
		User admin = user(1L, "admin@example.com", Role.ADMIN);
		DocumentRequirementSet requirementSet = requirementSet(100L, admin, ConfigurationStatus.ARCHIVED);
		when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(courseClassRepository.findById(200L)).thenReturn(Optional.of(courseClass(200L)));

		assertThatThrownBy(() -> service.assignToClass(
				"admin@example.com",
				new AssignRequirementSetToClassRequest(100L, 200L, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only active requirement sets can be assigned");

		verify(assignmentRepository, never()).save(any(DocumentRequirementSetAssignment.class));
	}

	@Test
	void shouldRejectAssigningDraftRequirementSet() {
		User admin = user(1L, "admin@example.com", Role.ADMIN);
		DocumentRequirementSet requirementSet = requirementSet(100L, admin, ConfigurationStatus.DRAFT);
		when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(projectRepository.findById(200L)).thenReturn(Optional.of(project(200L)));

		assertThatThrownBy(() -> service.assignToProject(
				"admin@example.com",
				new AssignRequirementSetToProjectRequest(100L, 200L, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only active requirement sets can be assigned");
	}

	@Test
	void shouldRejectDuplicateActiveClassAssignment() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.ACTIVE);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(courseClassRepository.findById(200L)).thenReturn(Optional.of(courseClass(200L)));
		when(assignmentRepository.existsByRequirementSetIdAndCourseClassIdAndStatus(
				100L, 200L, RequirementSetAssignmentStatus.ACTIVE)).thenReturn(true);

		assertThatThrownBy(() -> service.assignToClass(
				"instructor@example.com",
				new AssignRequirementSetToClassRequest(100L, 200L, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Requirement set is already actively assigned to this class");
	}

	@Test
	void shouldRejectDuplicateActiveProjectAssignment() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.ACTIVE);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(projectRepository.findById(200L)).thenReturn(Optional.of(project(200L)));
		when(assignmentRepository.existsByRequirementSetIdAndProjectIdAndStatus(
				100L, 200L, RequirementSetAssignmentStatus.ACTIVE)).thenReturn(true);

		assertThatThrownBy(() -> service.assignToProject(
				"instructor@example.com",
				new AssignRequirementSetToProjectRequest(100L, 200L, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Requirement set is already actively assigned to this project");
	}

	@Test
	void shouldReturnAssignmentsByClass() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		CourseClass courseClass = courseClass(200L);
		DocumentRequirementSetAssignment assignment = classAssignment(
				300L,
				requirementSet(100L, instructor, ConfigurationStatus.ACTIVE),
				courseClass,
				instructor,
				RequirementSetAssignmentStatus.ACTIVE);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(courseClassRepository.findById(200L)).thenReturn(Optional.of(courseClass));
		when(assignmentRepository.findByCourseClassIdAndStatus(200L, RequirementSetAssignmentStatus.ACTIVE))
				.thenReturn(List.of(assignment));

		var responses = service.getAssignmentsByClass(
				"instructor@example.com",
				200L,
				RequirementSetAssignmentStatus.ACTIVE);

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).id()).isEqualTo(300L);
		assertThat(responses.get(0).courseClassId()).isEqualTo(200L);
	}

	@Test
	void shouldReturnAssignmentsByProject() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		Project project = project(200L);
		DocumentRequirementSetAssignment assignment = projectAssignment(
				300L,
				requirementSet(100L, instructor, ConfigurationStatus.ACTIVE),
				project,
				instructor,
				RequirementSetAssignmentStatus.ACTIVE);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(projectRepository.findById(200L)).thenReturn(Optional.of(project));
		when(assignmentRepository.findByProjectId(200L)).thenReturn(List.of(assignment));

		var responses = service.getAssignmentsByProject("instructor@example.com", 200L, null);

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).id()).isEqualTo(300L);
		assertThat(responses.get(0).projectId()).isEqualTo(200L);
	}

	@Test
	void shouldReturnStudentAssignedProjectRequirementsWithExistingDraft() {
		User student = user(99L, "student@example.com", Role.STUDENT);
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, instructor, ConfigurationStatus.ACTIVE);
		com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement requirement =
				new com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement("Requirements Document", 1);
		requirement.setId(101L);
		requirement.setRequirementSet(requirementSet);
		requirementSet.getDocumentRequirements().add(requirement);
		Project project = project(200L);
		DocumentRequirementSetAssignment assignment = projectAssignment(
				300L,
				requirementSet,
				project,
				instructor,
				RequirementSetAssignmentStatus.ACTIVE);
		Submission draft = submission(400L, student, assignment, requirement, SubmissionStatus.DRAFT);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(projectRepository.findByOwnerUserIdOrderByCreatedAtDesc(99L)).thenReturn(List.of(project));
		when(assignmentRepository.findByProjectIdAndStatus(200L, RequirementSetAssignmentStatus.ACTIVE))
				.thenReturn(List.of(assignment));
		when(submissionRepository.findByTypeAndSubmittedByIdOrderByCreatedAtDesc(SubmissionType.DOCUMENT, 99L))
				.thenReturn(List.of(draft));

		var responses = service.getMyAssignedDocumentRequirements("student@example.com");

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).assignmentId()).isEqualTo(300L);
		assertThat(responses.get(0).documentRequirementId()).isEqualTo(101L);
		assertThat(responses.get(0).requirementName()).isEqualTo("Requirements Document");
		assertThat(responses.get(0).existingDraftSubmissionId()).isEqualTo(400L);
		assertThat(responses.get(0).latestSubmissionStatus()).isEqualTo(SubmissionStatus.DRAFT);
	}

	@Test
	void shouldReturnActiveRequirementSetsByClass() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		CourseClass courseClass = courseClass(200L);
		DocumentRequirementSetAssignment assignment = classAssignment(
				300L,
				requirementSet(100L, instructor, ConfigurationStatus.ACTIVE),
				courseClass,
				instructor,
				RequirementSetAssignmentStatus.ACTIVE);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(courseClassRepository.findById(200L)).thenReturn(Optional.of(courseClass));
		when(assignmentRepository.findByCourseClassIdAndAssignmentTypeAndStatus(
				200L,
				RequirementSetAssignmentType.COURSE_CLASS,
				RequirementSetAssignmentStatus.ACTIVE)).thenReturn(List.of(assignment));

		var responses = service.getActiveRequirementSetsByClass("instructor@example.com", 200L);

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).id()).isEqualTo(100L);
	}

	@Test
	void shouldReturnActiveRequirementSetsByProject() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		Project project = project(200L);
		DocumentRequirementSetAssignment assignment = projectAssignment(
				300L,
				requirementSet(100L, instructor, ConfigurationStatus.ACTIVE),
				project,
				instructor,
				RequirementSetAssignmentStatus.ACTIVE);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(projectRepository.findById(200L)).thenReturn(Optional.of(project));
		when(assignmentRepository.findByProjectIdAndAssignmentTypeAndStatus(
				200L,
				RequirementSetAssignmentType.PROJECT,
				RequirementSetAssignmentStatus.ACTIVE)).thenReturn(List.of(assignment));

		var responses = service.getActiveRequirementSetsByProject("instructor@example.com", 200L);

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).id()).isEqualTo(100L);
	}

	@Test
	void shouldDeactivateAssignment() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSetAssignment assignment = classAssignment(
				300L,
				requirementSet(100L, instructor, ConfigurationStatus.ACTIVE),
				courseClass(200L),
				instructor,
				RequirementSetAssignmentStatus.ACTIVE);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(assignmentRepository.findById(300L)).thenReturn(Optional.of(assignment));
		when(assignmentRepository.save(any(DocumentRequirementSetAssignment.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementSetAssignmentResponse response = service.deactivateAssignment(
				"instructor@example.com",
				300L,
				new DeactivateRequirementSetAssignmentRequest("Replaced"));

		assertThat(response.status()).isEqualTo(RequirementSetAssignmentStatus.INACTIVE);
		assertThat(response.deactivatedAt()).isNotNull();
		assertThat(response.notes()).isEqualTo("Replaced");
	}

	@Test
	void shouldArchiveAssignment() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSetAssignment assignment = classAssignment(
				300L,
				requirementSet(100L, instructor, ConfigurationStatus.ACTIVE),
				courseClass(200L),
				instructor,
				RequirementSetAssignmentStatus.ACTIVE);
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(assignmentRepository.findById(300L)).thenReturn(Optional.of(assignment));
		when(assignmentRepository.save(any(DocumentRequirementSetAssignment.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementSetAssignmentResponse response = service.archiveAssignment("instructor@example.com", 300L);

		assertThat(response.status()).isEqualTo(RequirementSetAssignmentStatus.ARCHIVED);
		assertThat(response.deactivatedAt()).isNotNull();
	}

	@Test
	void shouldReactivateAssignment() {
		User instructor = user(10L, "instructor@example.com", Role.INSTRUCTOR);
		DocumentRequirementSetAssignment assignment = classAssignment(
				300L,
				requirementSet(100L, instructor, ConfigurationStatus.ACTIVE),
				courseClass(200L),
				instructor,
				RequirementSetAssignmentStatus.INACTIVE);
		assignment.setDeactivatedAt(LocalDateTime.now());
		when(userRepository.findByEmail("instructor@example.com")).thenReturn(Optional.of(instructor));
		when(assignmentRepository.findById(300L)).thenReturn(Optional.of(assignment));
		when(assignmentRepository.existsByRequirementSetIdAndCourseClassIdAndStatus(
				100L, 200L, RequirementSetAssignmentStatus.ACTIVE)).thenReturn(false);
		when(assignmentRepository.save(any(DocumentRequirementSetAssignment.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentRequirementSetAssignmentResponse response = service.reactivateAssignment("instructor@example.com", 300L);

		assertThat(response.status()).isEqualTo(RequirementSetAssignmentStatus.ACTIVE);
		assertThat(response.deactivatedAt()).isNull();
	}

	@Test
	void shouldRejectUnauthorizedInstructorAssignment() {
		User owner = user(10L, "owner@example.com", Role.INSTRUCTOR);
		User otherInstructor = user(11L, "other@example.com", Role.INSTRUCTOR);
		DocumentRequirementSet requirementSet = requirementSet(100L, owner, ConfigurationStatus.ACTIVE);
		when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherInstructor));
		when(requirementSetRepository.findById(100L)).thenReturn(Optional.of(requirementSet));
		when(courseClassRepository.findById(200L)).thenReturn(Optional.of(courseClass(200L)));

		assertThatThrownBy(() -> service.assignToClass(
				"other@example.com",
				new AssignRequirementSetToClassRequest(100L, 200L, null)))
				.isInstanceOf(AccessDeniedException.class);
	}

	private User user(Long id, String email, Role role) {
		User user = new User("Test", null, "User", email, "encoded-password", role);
		user.setId(id);
		return user;
	}

	private DocumentRequirementSet requirementSet(Long id, User owner, ConfigurationStatus status) {
		DocumentRequirementSet requirementSet = new DocumentRequirementSet("Requirement Set", status);
		requirementSet.setId(id);
		requirementSet.setOwnerInstructor(owner);
		return requirementSet;
	}

	private CourseClass courseClass(Long id) {
		CourseClass courseClass = new CourseClass("Software Engineering", "SE201");
		courseClass.setId(id);
		return courseClass;
	}

	private Project project(Long id) {
		Project project = new Project(99L, "student@example.com", "Capstone Portal", "Description", null);
		project.setId(id);
		return project;
	}

	private DocumentRequirementSetAssignment classAssignment(
			Long id,
			DocumentRequirementSet requirementSet,
			CourseClass courseClass,
			User assignedBy,
			RequirementSetAssignmentStatus status) {
		DocumentRequirementSetAssignment assignment = new DocumentRequirementSetAssignment(
				requirementSet,
				RequirementSetAssignmentType.COURSE_CLASS,
				status,
				LocalDateTime.now());
		assignment.setId(id);
		assignment.setCourseClass(courseClass);
		assignment.setAssignedBy(assignedBy);
		return assignment;
	}

	private DocumentRequirementSetAssignment projectAssignment(
			Long id,
			DocumentRequirementSet requirementSet,
			Project project,
			User assignedBy,
			RequirementSetAssignmentStatus status) {
		DocumentRequirementSetAssignment assignment = new DocumentRequirementSetAssignment(
				requirementSet,
				RequirementSetAssignmentType.PROJECT,
				status,
				LocalDateTime.now());
		assignment.setId(id);
		assignment.setProject(project);
		assignment.setAssignedBy(assignedBy);
		return assignment;
	}

	private Submission submission(
			Long id,
			User submittedBy,
			DocumentRequirementSetAssignment assignment,
			com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement requirement,
			SubmissionStatus status) {
		Submission submission = new Submission();
		submission.setId(id);
		submission.setType(SubmissionType.DOCUMENT);
		submission.setSubmittedBy(submittedBy);
		submission.setAssignmentId(assignment.getId());
		submission.setRequirementId(requirement.getId());
		submission.setProjectId(assignment.getProject() == null ? null : assignment.getProject().getId());
		submission.setCourseClassId(assignment.getCourseClass() == null ? null : assignment.getCourseClass().getId());
		submission.setStatus(status);
		submission.setSubmissionTitle(requirement.getName());
		submission.setAttemptNumber(1);
		return submission;
	}
}
