package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.courseclass.repository.CourseClassRepository;
import com.blissfuljuan.aiprojecteval.submission.dto.request.CreateSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.CreateSubmissionRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.SubmissionFileMetadataRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.UpdateSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionFileStatus;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import com.blissfuljuan.aiprojecteval.submission.model.SubmissionFile;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionFileRepository;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.service.IdentityQueryService;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {

	@Mock
	private SubmissionRepository submissionRepository;

	@Mock
	private SubmissionFileRepository fileRepository;

	@Mock
	private DocumentRequirementSetAssignmentRepository assignmentRepository;

	@Mock
	private DocumentRequirementRepository requirementRepository;

	@Mock
	private IdentityQueryService identityQueryService;

	@Mock
	private CourseClassRepository courseClassRepository;

	private SubmissionServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new SubmissionServiceImpl(
				submissionRepository,
				fileRepository,
				assignmentRepository,
				requirementRepository,
				identityQueryService,
				courseClassRepository);
	}

	@Test
	void shouldCreateDraftForActiveAssignmentAndValidRequirement() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		stubValidSubmissionTarget(student, assignment, requirement, 0);
		when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
			Submission submission = invocation.getArgument(0);
			submission.setId(40L);
			return submission;
		});

		SubmissionResponse response = service.createDraftSubmission(
				"student@example.com",
				new CreateSubmissionDraftRequest(30L, 20L, "SRS Draft", "Initial metadata"));

		assertThat(response.id()).isEqualTo(40L);
		assertThat(response.status()).isEqualTo(SubmissionStatus.DRAFT);
		assertThat(response.assignmentId()).isEqualTo(30L);
		assertThat(response.requirementId()).isEqualTo(20L);
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
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(assignmentRepository.findById(30L)).thenReturn(Optional.of(assignment));

		assertThatThrownBy(() -> service.createSubmission(
				"student@example.com",
				new CreateSubmissionRequest(30L, 20L, "SRS", null, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only active requirement set assignments can receive submissions");

		verify(submissionRepository, never()).save(any(Submission.class));
	}

	@Test
	void shouldRejectRequirementOutsideAssignedRequirementSet() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet assignedSet = requirementSet(10L);
		DocumentRequirement otherRequirement = requirement(20L, requirementSet(11L));
		DocumentRequirementSetAssignment assignment = classAssignment(30L, assignedSet, RequirementSetAssignmentStatus.ACTIVE);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(assignmentRepository.findById(30L)).thenReturn(Optional.of(assignment));
		when(requirementRepository.findById(20L)).thenReturn(Optional.of(otherRequirement));

		assertThatThrownBy(() -> service.createDraftSubmission(
				"student@example.com",
				new CreateSubmissionDraftRequest(30L, 20L, "SRS Draft", null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Document requirement does not belong to the assigned requirement set");
	}

	@Test
	void shouldRejectDuplicateDraftForSameUserAssignmentAndRequirement() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(assignmentRepository.findById(30L)).thenReturn(Optional.of(assignment));
		when(requirementRepository.findById(20L)).thenReturn(Optional.of(requirement));
		Submission existingDraft = submission(40L, student, assignment, requirement, SubmissionStatus.DRAFT);
		when(submissionRepository.findFirstByTypeAndSubmittedByIdAndAssignmentIdAndRequirementIdAndStatus(SubmissionType.DOCUMENT, 
				1L, 30L, 20L, SubmissionStatus.DRAFT))
				.thenReturn(Optional.of(existingDraft));

		assertThatThrownBy(() -> service.createDraftSubmission(
				"student@example.com",
				new CreateSubmissionDraftRequest(30L, 20L, "SRS Draft", null)))
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
		when(submissionRepository.save(any(Submission.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		SubmissionResponse response = service.createDraftSubmission(
				"student@example.com",
				new CreateSubmissionDraftRequest(30L, 20L, "SRS v2", null));

		assertThat(response.attemptNumber()).isEqualTo(2);
		assertThat(response.status()).isEqualTo(SubmissionStatus.DRAFT);
		assertThat(response.submittedAt()).isNull();
	}

	@Test
	void shouldFinalizeDraftToSubmitted() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		Submission submission = submission(40L, student, assignment, requirement, SubmissionStatus.DRAFT);
		SubmissionFile uploadedFile = submissionFile(70L, submission);
		uploadedFile.setFileStatus(SubmissionFileStatus.UPLOADED);
		uploadedFile.setStoragePath("uploads/srs.pdf");
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));
		when(fileRepository.findBySubmissionIdAndFileStatusInOrderByCreatedAtAsc(
				40L,
				Set.of(SubmissionFileStatus.UPLOADED))).thenReturn(List.of(uploadedFile));
		when(submissionRepository.save(any(Submission.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		SubmissionResponse response = service.submitDraftSubmission("student@example.com", 40L);

		assertThat(response.status()).isEqualTo(SubmissionStatus.SUBMITTED);
		assertThat(response.submittedAt()).isNotNull();
	}

	@Test
	void shouldRejectSubmittingDraftWithoutUploadedFile() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		Submission submission = submission(40L, student, assignment, requirement, SubmissionStatus.DRAFT);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));
		when(fileRepository.findBySubmissionIdAndFileStatusInOrderByCreatedAtAsc(
				40L,
				Set.of(SubmissionFileStatus.UPLOADED))).thenReturn(List.of());

		assertThatThrownBy(() -> service.submitDraftSubmission("student@example.com", 40L))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Submission must have at least one uploaded file before it can be submitted");

		verify(submissionRepository, never()).save(any(Submission.class));
	}

	@Test
	void shouldRejectUpdatingArchivedSubmission() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		Submission submission = submission(40L, student, assignment, requirement, SubmissionStatus.ARCHIVED);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		assertThatThrownBy(() -> service.updateDraftSubmission(
				"student@example.com",
				40L,
				new UpdateSubmissionDraftRequest("Updated", null, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Archived submissions are read-only");
	}

	@Test
	void shouldRejectUpdatingSubmittedSubmissionAsDraft() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		Submission submission = submission(40L, student, assignment, requirement, SubmissionStatus.SUBMITTED);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		assertThatThrownBy(() -> service.updateDraftSubmission(
				"student@example.com",
				40L,
				new UpdateSubmissionDraftRequest("Updated", null, null)))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only draft submissions can be updated");
	}

	@Test
	void shouldRejectStudentUpdatingAnotherStudentsDraftSubmission() {
		User owner = user(1L, "owner@example.com", Role.STUDENT);
		User otherStudent = user(2L, "other@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		Submission submission = submission(40L, owner, assignment, requirement, SubmissionStatus.DRAFT);
		when(identityQueryService.getUserByEmail("other@example.com")).thenReturn(otherStudent);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		assertThatThrownBy(() -> service.updateDraftSubmission(
				"other@example.com",
				40L,
				new UpdateSubmissionDraftRequest("Updated", null, null)))
				.isInstanceOf(AccessDeniedException.class);

		verify(submissionRepository, never()).save(any(Submission.class));
	}

	@Test
	void shouldRejectStudentViewingAnotherStudentsSubmission() {
		User owner = user(1L, "owner@example.com", Role.STUDENT);
		User otherStudent = user(2L, "other@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		DocumentRequirementSetAssignment assignment = projectAssignment(30L, requirementSet, project(60L, 99L), RequirementSetAssignmentStatus.ACTIVE);
		Submission submission = submission(40L, owner, assignment, requirement, SubmissionStatus.SUBMITTED);
		when(identityQueryService.getUserByEmail("other@example.com")).thenReturn(otherStudent);
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
		Submission submission = submission(40L, owner, assignment, requirement, SubmissionStatus.SUBMITTED);
		when(identityQueryService.getUserByEmail("admin@example.com")).thenReturn(admin);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		SubmissionResponse response = service.getSubmissionById("admin@example.com", 40L);

		assertThat(response.id()).isEqualTo(40L);
		assertThat(response.submittedById()).isEqualTo(1L);
	}

	@Test
	void shouldRejectDirectSubmittedSubmissionCreation() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L);
		DocumentRequirement requirement = requirement(20L, requirementSet);
		requirement.getAllowedFileTypes().add(AllowedFileType.PDF);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(assignmentRepository.findById(30L)).thenReturn(Optional.of(assignment));
		when(requirementRepository.findById(20L)).thenReturn(Optional.of(requirement));
		when(submissionRepository.findFirstByTypeAndSubmittedByIdAndAssignmentIdAndRequirementIdAndStatus(
				SubmissionType.DOCUMENT,
				1L,
				30L,
				20L,
				SubmissionStatus.DRAFT)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.createSubmission(
				"student@example.com",
				new CreateSubmissionRequest(30L, 20L, "SRS", null, List.of(new SubmissionFileMetadataRequest(
						"srs.pdf",
						"application/pdf",
						1024L,
						"pdf",
						null,
						null,
						"checksum")))))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Create a draft submission, upload at least one valid file, then submit the draft");

		verify(submissionRepository, never()).save(any(Submission.class));
	}

	private void stubValidSubmissionTarget(
			User user,
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement,
			int maxAttemptNumber) {
		when(identityQueryService.getUserByEmail(user.getEmail())).thenReturn(user);
		when(assignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));
		when(requirementRepository.findById(requirement.getId())).thenReturn(Optional.of(requirement));
		when(submissionRepository.findFirstByTypeAndSubmittedByIdAndAssignmentIdAndRequirementIdAndStatus(SubmissionType.DOCUMENT, 
				user.getId(),
				assignment.getId(),
				requirement.getId(),
				SubmissionStatus.DRAFT)).thenReturn(Optional.empty());
		when(submissionRepository.findMaxAttemptNumber(SubmissionType.DOCUMENT, user.getId(), assignment.getId(), requirement.getId()))
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

	private Submission submission(
			Long id,
			User submittedBy,
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement,
			SubmissionStatus status) {
		Submission submission = new Submission();
		submission.setType(SubmissionType.DOCUMENT);
		submission.setId(id);
		submission.setSubmittedBy(submittedBy);
		submission.setAssignmentId(assignment.getId());
		submission.setRequirementId(requirement.getId());
		submission.setCourseClassId(assignment.getCourseClass() == null ? null : assignment.getCourseClass().getId());
		submission.setProjectId(assignment.getProject() == null ? null : assignment.getProject().getId());
		submission.setStatus(status);
		submission.setSubmissionTitle("SRS");
		submission.setAttemptNumber(1);
		submission.setLastUpdatedAt(LocalDateTime.now());
		if (status == SubmissionStatus.SUBMITTED) {
			submission.setSubmittedAt(LocalDateTime.now());
		}
		org.mockito.Mockito.lenient().when(assignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));
		return submission;
	}

	@SuppressWarnings("unused")
	private SubmissionFile submissionFile(Long id, Submission submission) {
		SubmissionFile file = new SubmissionFile();
		file.setId(id);
		file.setSubmission(submission);
		file.setOriginalFileName("srs.pdf");
		file.setFileExtension("PDF");
		file.setFileStatus(SubmissionFileStatus.PENDING_UPLOAD);
		return file;
	}
}
