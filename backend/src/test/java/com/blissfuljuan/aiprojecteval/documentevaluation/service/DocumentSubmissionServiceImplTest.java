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
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentSubmissionRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.DocumentSubmissionFileMetadataRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionFileStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmission;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmissionFile;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentSubmissionFileRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentSubmissionRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
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
class DocumentSubmissionServiceImplTest {

	@Mock
	private DocumentSubmissionRepository submissionRepository;

	@Mock
	private DocumentSubmissionFileRepository fileRepository;

	@Mock
	private DocumentRequirementSetAssignmentRepository assignmentRepository;

	@Mock
	private DocumentRequirementRepository requirementRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private CourseClassRepository courseClassRepository;

	private DocumentSubmissionServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new DocumentSubmissionServiceImpl(
				submissionRepository,
				fileRepository,
				assignmentRepository,
				requirementRepository,
				userRepository,
				projectRepository,
				courseClassRepository);
	}

	@Test
	void shouldCreateDraftForActiveAssignmentAndValidRequirement() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		stubValidSubmissionTarget(student, assignment, requirement, 0);
		when(submissionRepository.save(any(DocumentSubmission.class))).thenAnswer(invocation -> {
			DocumentSubmission submission = invocation.getArgument(0);
			submission.setId(40L);
			return submission;
		});

		DocumentSubmissionResponse response = service.createDraftSubmission(
				"student@example.com",
				new CreateDocumentSubmissionDraftRequest(30L, 20L, "SRS Draft", "Initial metadata"));

		assertThat(response.id()).isEqualTo(40L);
		assertThat(response.status()).isEqualTo(DocumentSubmissionStatus.DRAFT);
		assertThat(response.assignmentId()).isEqualTo(30L);
		assertThat(response.documentRequirementId()).isEqualTo(20L);
		assertThat(response.courseClassId()).isEqualTo(50L);
		assertThat(response.projectId()).isNull();
		assertThat(response.submittedById()).isEqualTo(1L);
		assertThat(response.attemptNumber()).isEqualTo(1);
	}

	@Test
	void shouldRejectSubmissionForInactiveAssignment() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.INACTIVE);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(assignmentRepository.findById(30L)).thenReturn(Optional.of(assignment));

		assertThatThrownBy(() -> service.createSubmission(
				"student@example.com",
				new CreateDocumentSubmissionRequest(30L, 20L, "SRS", null, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only active requirement set assignments can receive submissions");

		verify(submissionRepository, never()).save(any(DocumentSubmission.class));
	}

	@Test
	void shouldRejectRequirementOutsideAssignedRequirementSet() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet assignedSet = requirementSet(10L);
		DocumentRequirement otherRequirement = requirement(20L, requirementSet(11L));
		DocumentRequirementSetAssignment assignment = classAssignment(30L, assignedSet, RequirementSetAssignmentStatus.ACTIVE);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(assignmentRepository.findById(30L)).thenReturn(Optional.of(assignment));
		when(requirementRepository.findById(20L)).thenReturn(Optional.of(otherRequirement));

		assertThatThrownBy(() -> service.createDraftSubmission(
				"student@example.com",
				new CreateDocumentSubmissionDraftRequest(30L, 20L, "SRS Draft", null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Document requirement does not belong to the assigned requirement set");
	}

	@Test
	void shouldRejectDuplicateDraftForSameUserAssignmentAndRequirement() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(assignmentRepository.findById(30L)).thenReturn(Optional.of(assignment));
		when(requirementRepository.findById(20L)).thenReturn(Optional.of(requirement));
		when(submissionRepository.findFirstBySubmittedByIdAndAssignmentIdAndDocumentRequirementIdAndStatus(
				1L, 30L, 20L, DocumentSubmissionStatus.DRAFT))
				.thenReturn(Optional.of(submission(40L, student, assignment, requirement, DocumentSubmissionStatus.DRAFT)));

		assertThatThrownBy(() -> service.createDraftSubmission(
				"student@example.com",
				new CreateDocumentSubmissionDraftRequest(30L, 20L, "SRS Draft", null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("A draft submission already exists for this assignment and document requirement");
	}

	@Test
	void shouldIncrementAttemptAfterPreviousSubmittedAttempt() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		stubValidSubmissionTarget(student, assignment, requirement, 1);
		when(submissionRepository.save(any(DocumentSubmission.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentSubmissionResponse response = service.createSubmission(
				"student@example.com",
				new CreateDocumentSubmissionRequest(30L, 20L, "SRS v2", null, null));

		assertThat(response.attemptNumber()).isEqualTo(2);
		assertThat(response.status()).isEqualTo(DocumentSubmissionStatus.SUBMITTED);
		assertThat(response.submittedAt()).isNotNull();
	}

	@Test
	void shouldFinalizeDraftToSubmitted() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		DocumentSubmission submission = submission(40L, student, assignment, requirement, DocumentSubmissionStatus.DRAFT);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));
		when(submissionRepository.save(any(DocumentSubmission.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DocumentSubmissionResponse response = service.submitDraftSubmission("student@example.com", 40L);

		assertThat(response.status()).isEqualTo(DocumentSubmissionStatus.SUBMITTED);
		assertThat(response.submittedAt()).isNotNull();
	}

	@Test
	void shouldRejectUpdatingArchivedSubmission() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		DocumentSubmission submission = submission(40L, student, assignment, requirement, DocumentSubmissionStatus.ARCHIVED);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		assertThatThrownBy(() -> service.updateDraftSubmission(
				"student@example.com",
				40L,
				new UpdateDocumentSubmissionDraftRequest("Updated", null, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Archived submissions are read-only");
	}

	@Test
	void shouldRejectStudentViewingAnotherStudentsSubmission() {
		User owner = user(1L, "owner@example.com", Role.STUDENT);
		User otherStudent = user(2L, "other@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = projectAssignment(30L, requirementSet, project(60L, 99L), RequirementSetAssignmentStatus.ACTIVE);
		DocumentSubmission submission = submission(40L, owner, assignment, requirement, DocumentSubmissionStatus.SUBMITTED);
		when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherStudent));
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		assertThatThrownBy(() -> service.getSubmissionById("other@example.com", 40L))
				.isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void shouldAllowAdminToViewSubmission() {
		User admin = user(3L, "admin@example.com", Role.ADMIN);
		User owner = user(1L, "owner@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		DocumentSubmission submission = submission(40L, owner, assignment, requirement, DocumentSubmissionStatus.SUBMITTED);
		when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		DocumentSubmissionResponse response = service.getSubmissionById("admin@example.com", 40L);

		assertThat(response.id()).isEqualTo(40L);
		assertThat(response.submittedById()).isEqualTo(1L);
	}

	@Test
	void shouldSaveFileMetadataWithSubmittedSubmission() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		requirement.getAllowedFileTypes().add(AllowedFileType.PDF);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		stubValidSubmissionTarget(student, assignment, requirement, 0);
		when(submissionRepository.save(any(DocumentSubmission.class))).thenAnswer(invocation -> {
			DocumentSubmission submission = invocation.getArgument(0);
			submission.setId(40L);
			submission.getFiles().get(0).setId(70L);
			return submission;
		});

		DocumentSubmissionResponse response = service.createSubmission(
				"student@example.com",
				new CreateDocumentSubmissionRequest(
						30L,
						20L,
						"SRS",
						null,
						List.of(new DocumentSubmissionFileMetadataRequest(
								"srs.pdf",
								"application/pdf",
								1024L,
								"pdf",
								null,
								null,
								"checksum"))));

		assertThat(response.files()).hasSize(1);
		assertThat(response.files().get(0).id()).isEqualTo(70L);
		assertThat(response.files().get(0).fileExtension()).isEqualTo("PDF");
		assertThat(response.files().get(0).fileStatus()).isEqualTo(DocumentSubmissionFileStatus.PENDING_UPLOAD);
		assertThat(response.files().get(0).uploadedAt()).isNull();
	}

	private void stubValidSubmissionTarget(
			User user,
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement,
			int maxAttemptNumber) {
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
		when(assignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));
		when(requirementRepository.findById(requirement.getId())).thenReturn(Optional.of(requirement));
		when(submissionRepository.findFirstBySubmittedByIdAndAssignmentIdAndDocumentRequirementIdAndStatus(
				user.getId(),
				assignment.getId(),
				requirement.getId(),
				DocumentSubmissionStatus.DRAFT)).thenReturn(Optional.empty());
		when(submissionRepository.findMaxAttemptNumber(user.getId(), assignment.getId(), requirement.getId()))
				.thenReturn(maxAttemptNumber);
	}

	private User user(Long id, String email, Role role) {
		User user = new User("Test", null, "User", email, "encoded-password", role);
		user.setId(id);
		return user;
	}

	private DocumentRequirementSet requirementSet(Long id) {
		DocumentRequirementSet requirementSet = new DocumentRequirementSet("Requirement Set", ConfigurationStatus.ACTIVE);
		requirementSet.setId(id);
		requirementSet.setOwnerInstructor(user(90L, "instructor@example.com", Role.INSTRUCTOR));
		return requirementSet;
	}

	private DocumentRequirement requirement(Long id, DocumentRequirementSet requirementSet) {
		DocumentRequirement requirement = new DocumentRequirement("SRS", 1);
		requirement.setId(id);
		requirement.setRequirementSet(requirementSet);
		return requirement;
	}

	private CourseClass courseClass(Long id) {
		CourseClass courseClass = new CourseClass("Software Engineering", "SE201");
		courseClass.setId(id);
		return courseClass;
	}

	private Project project(Long id, Long ownerUserId) {
		Project project = new Project(ownerUserId, "owner@example.com", "Capstone Portal", "Description", null);
		project.setId(id);
		return project;
	}

	private DocumentRequirementSetAssignment classAssignment(
			Long id,
			DocumentRequirementSet requirementSet,
			RequirementSetAssignmentStatus status) {
		DocumentRequirementSetAssignment assignment = new DocumentRequirementSetAssignment(
				requirementSet,
				RequirementSetAssignmentType.COURSE_CLASS,
				status,
				LocalDateTime.now());
		assignment.setId(id);
		assignment.setCourseClass(courseClass(50L));
		return assignment;
	}

	private DocumentRequirementSetAssignment projectAssignment(
			Long id,
			DocumentRequirementSet requirementSet,
			Project project,
			RequirementSetAssignmentStatus status) {
		DocumentRequirementSetAssignment assignment = new DocumentRequirementSetAssignment(
				requirementSet,
				RequirementSetAssignmentType.PROJECT,
				status,
				LocalDateTime.now());
		assignment.setId(id);
		assignment.setProject(project);
		return assignment;
	}

	private DocumentSubmission submission(
			Long id,
			User submittedBy,
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement,
			DocumentSubmissionStatus status) {
		DocumentSubmission submission = new DocumentSubmission();
		submission.setId(id);
		submission.setSubmittedBy(submittedBy);
		submission.setAssignment(assignment);
		submission.setDocumentRequirement(requirement);
		submission.setCourseClass(assignment.getCourseClass());
		submission.setProject(assignment.getProject());
		submission.setStatus(status);
		submission.setSubmissionTitle("SRS");
		submission.setAttemptNumber(1);
		submission.setLastUpdatedAt(LocalDateTime.now());
		if (status == DocumentSubmissionStatus.SUBMITTED) {
			submission.setSubmittedAt(LocalDateTime.now());
		}
		return submission;
	}

	@SuppressWarnings("unused")
	private DocumentSubmissionFile submissionFile(Long id, DocumentSubmission submission) {
		DocumentSubmissionFile file = new DocumentSubmissionFile();
		file.setId(id);
		file.setSubmission(submission);
		file.setOriginalFileName("srs.pdf");
		file.setFileExtension("PDF");
		file.setFileStatus(DocumentSubmissionFileStatus.PENDING_UPLOAD);
		return file;
	}
}
