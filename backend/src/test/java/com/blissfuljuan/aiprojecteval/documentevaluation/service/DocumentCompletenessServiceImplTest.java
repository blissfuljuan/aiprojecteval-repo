package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentCompletenessReportResponse;
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
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentSubmissionRepository;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.project.repository.ProjectRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentCompletenessServiceImplTest {

	@Mock
	private DocumentRequirementSetAssignmentRepository assignmentRepository;

	@Mock
	private DocumentRequirementRepository requirementRepository;

	@Mock
	private DocumentSubmissionRepository submissionRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ProjectRepository projectRepository;

	private DocumentCompletenessServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new DocumentCompletenessServiceImpl(
				assignmentRepository,
				requirementRepository,
				submissionRepository,
				userRepository,
				projectRepository);
	}

	@Test
	void shouldReportMissingRequiredDocumentAsNotReady() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, ConfigurationStatus.ACTIVE);
		DocumentRequirement required = requirement(20L, requirementSet, "Project Proposal", true, 1);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		stubMyReport(student, assignment, List.of(required), List.of());

		DocumentCompletenessReportResponse report = service.getMyCompletenessReport("student@example.com", 30L);

		assertThat(report.complete()).isFalse();
		assertThat(report.readyForEvaluation()).isFalse();
		assertThat(report.missingRequiredRequirements()).isEqualTo(1);
		assertThat(report.blockingIssues()).contains("Missing required document: Project Proposal");
	}

	@Test
	void shouldNotBlockReadinessWhenOptionalDocumentIsMissing() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, ConfigurationStatus.ACTIVE);
		DocumentRequirement required = requirement(20L, requirementSet, "Project Proposal", true, 1);
		DocumentRequirement optional = requirement(21L, requirementSet, "Appendix", false, 2);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		DocumentSubmission submitted = submission(40L, student, assignment, required, DocumentSubmissionStatus.SUBMITTED, 1);
		submitted.addFile(uploadedFile(70L, submitted));
		stubMyReport(student, assignment, List.of(required, optional), List.of(submitted));

		DocumentCompletenessReportResponse report = service.getMyCompletenessReport("student@example.com", 30L);

		assertThat(report.complete()).isTrue();
		assertThat(report.readyForEvaluation()).isTrue();
		assertThat(report.optionalRequirements()).isEqualTo(1);
		assertThat(report.blockingIssues()).isEmpty();
		assertThat(report.requirements().get(1).blocking()).isFalse();
	}

	@Test
	void shouldTreatDraftSubmissionAsIncomplete() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, ConfigurationStatus.ACTIVE);
		DocumentRequirement required = requirement(20L, requirementSet, "Project Proposal", true, 1);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		DocumentSubmission draft = submission(40L, student, assignment, required, DocumentSubmissionStatus.DRAFT, 1);
		draft.addFile(uploadedFile(70L, draft));
		stubMyReport(student, assignment, List.of(required), List.of(draft));

		DocumentCompletenessReportResponse report = service.getMyCompletenessReport("student@example.com", 30L);

		assertThat(report.complete()).isFalse();
		assertThat(report.incompleteRequiredRequirements()).isEqualTo(1);
		assertThat(report.blockingIssues()).contains("Latest submission is still in draft: Project Proposal");
	}

	@Test
	void shouldRequireAtLeastOneValidUploadedFile() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, ConfigurationStatus.ACTIVE);
		DocumentRequirement required = requirement(20L, requirementSet, "Project Proposal", true, 1);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ACTIVE);
		DocumentSubmission submitted = submission(40L, student, assignment, required, DocumentSubmissionStatus.SUBMITTED, 1);
		submitted.addFile(inactiveFile(70L, submitted, DocumentSubmissionFileStatus.REMOVED));
		submitted.addFile(inactiveFile(71L, submitted, DocumentSubmissionFileStatus.INVALID));
		stubMyReport(student, assignment, List.of(required), List.of(submitted));

		DocumentCompletenessReportResponse report = service.getMyCompletenessReport("student@example.com", 30L);

		assertThat(report.complete()).isFalse();
		assertThat(report.requirements().get(0).uploadedFileCount()).isZero();
		assertThat(report.requirements().get(0).validUploadedFileCount()).isZero();
		assertThat(report.blockingIssues()).contains("All uploaded files are invalid or removed: Project Proposal");
	}

	@Test
	void shouldBlockReadyForArchivedAssignment() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentRequirementSet requirementSet = requirementSet(10L, ConfigurationStatus.ACTIVE);
		DocumentRequirement required = requirement(20L, requirementSet, "Project Proposal", true, 1);
		DocumentRequirementSetAssignment assignment = classAssignment(30L, requirementSet, RequirementSetAssignmentStatus.ARCHIVED);
		DocumentSubmission submitted = submission(40L, student, assignment, required, DocumentSubmissionStatus.SUBMITTED, 1);
		submitted.addFile(uploadedFile(70L, submitted));
		stubMyReport(student, assignment, List.of(required), List.of(submitted));

		DocumentCompletenessReportResponse report = service.getMyCompletenessReport("student@example.com", 30L);

		assertThat(report.complete()).isFalse();
		assertThat(report.readyForEvaluation()).isFalse();
		assertThat(report.blockingIssues()).contains("Requirement set assignment is not active.");
	}

	private void stubMyReport(
			User student,
			DocumentRequirementSetAssignment assignment,
			List<DocumentRequirement> requirements,
			List<DocumentSubmission> submissions) {
		when(userRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
		when(assignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));
		when(requirementRepository.findByRequirementSetIdOrderBySortOrderAsc(assignment.getRequirementSet().getId()))
				.thenReturn(requirements);
		when(submissionRepository.findByAssignmentIdAndSubmittedByIdOrderByCreatedAtDesc(
				assignment.getId(),
				student.getId())).thenReturn(submissions);
	}

	private User user(Long id, String email, Role role) {
		User user = new User("Test", null, "User", email, "encoded-password", role);
		user.setId(id);
		return user;
	}

	private DocumentRequirementSet requirementSet(Long id, ConfigurationStatus status) {
		DocumentRequirementSet requirementSet = new DocumentRequirementSet("Requirement Set", status);
		requirementSet.setId(id);
		requirementSet.setOwnerInstructor(user(90L, "instructor@example.com", Role.INSTRUCTOR));
		return requirementSet;
	}

	private DocumentRequirement requirement(
			Long id,
			DocumentRequirementSet requirementSet,
			String name,
			boolean required,
			Integer sortOrder) {
		DocumentRequirement requirement = new DocumentRequirement(name, sortOrder);
		requirement.setId(id);
		requirement.setRequirementSet(requirementSet);
		requirement.setRequired(required);
		return requirement;
	}

	private DocumentRequirementSetAssignment classAssignment(
			Long id,
			DocumentRequirementSet requirementSet,
			RequirementSetAssignmentStatus status) {
		CourseClass courseClass = new CourseClass("Software Engineering", "SE201");
		courseClass.setId(50L);
		DocumentRequirementSetAssignment assignment = new DocumentRequirementSetAssignment(
				requirementSet,
				RequirementSetAssignmentType.COURSE_CLASS,
				status,
				LocalDateTime.now());
		assignment.setId(id);
		assignment.setCourseClass(courseClass);
		return assignment;
	}

	private DocumentSubmission submission(
			Long id,
			User submittedBy,
			DocumentRequirementSetAssignment assignment,
			DocumentRequirement requirement,
			DocumentSubmissionStatus status,
			Integer attemptNumber) {
		DocumentSubmission submission = new DocumentSubmission();
		submission.setId(id);
		submission.setSubmittedBy(submittedBy);
		submission.setAssignment(assignment);
		submission.setDocumentRequirement(requirement);
		submission.setCourseClass(assignment.getCourseClass());
		submission.setStatus(status);
		submission.setSubmissionTitle(requirement.getName());
		submission.setAttemptNumber(attemptNumber);
		submission.setLastUpdatedAt(LocalDateTime.now());
		return submission;
	}

	private DocumentSubmissionFile uploadedFile(Long id, DocumentSubmission submission) {
		DocumentSubmissionFile file = inactiveFile(id, submission, DocumentSubmissionFileStatus.UPLOADED);
		file.setStoredFileName("stored-" + id + ".pdf");
		file.setStoragePath("uploads/stored-" + id + ".pdf");
		file.setUploadedAt(LocalDateTime.now());
		return file;
	}

	private DocumentSubmissionFile inactiveFile(
			Long id,
			DocumentSubmission submission,
			DocumentSubmissionFileStatus status) {
		DocumentSubmissionFile file = new DocumentSubmissionFile();
		file.setId(id);
		file.setSubmission(submission);
		file.setOriginalFileName("project-proposal.pdf");
		file.setContentType("application/pdf");
		file.setFileSize(1024L);
		file.setFileStatus(status);
		return file;
	}
}
