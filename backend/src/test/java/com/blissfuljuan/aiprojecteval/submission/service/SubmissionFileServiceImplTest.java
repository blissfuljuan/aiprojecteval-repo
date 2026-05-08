package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.submission.config.SubmissionStorageProperties;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResource;
import com.blissfuljuan.aiprojecteval.submission.dto.response.FileUploadResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionFileStatus;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import com.blissfuljuan.aiprojecteval.submission.model.SubmissionFile;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionFileRepository;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionRepository;
import com.blissfuljuan.aiprojecteval.submission.storage.FileStorageService;
import com.blissfuljuan.aiprojecteval.submission.storage.StoredFile;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.service.IdentityQueryService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class SubmissionFileServiceImplTest {

	@Mock
	private SubmissionRepository submissionRepository;

	@Mock
	private SubmissionFileRepository fileRepository;

	@Mock
	private IdentityQueryService identityQueryService;

	@Mock
	private DocumentRequirementSetAssignmentRepository assignmentRepository;

	@Mock
	private DocumentRequirementRepository requirementRepository;

	@Mock
	private FileStorageService storageService;

	private SubmissionFileServiceImpl service;

	private SubmissionStorageProperties storageProperties;

	@BeforeEach
	void setUp() {
		storageProperties = new SubmissionStorageProperties();
		storageProperties.setMaxFileSize(10L);
		SubmissionFileValidator validator =
				new SubmissionFileValidator(storageProperties, fileRepository, requirementRepository);
		service = new SubmissionFileServiceImpl(
				submissionRepository,
				fileRepository,
				identityQueryService,
				assignmentRepository,
				storageService,
				validator);
	}

	@Test
	void shouldUploadValidFileToOwnDraftSubmission() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		Submission submission = draftSubmission(student);
		MockMultipartFile upload = pdfFile("srs.pdf", "content");
		stubUpload(student, submission, upload);

		FileUploadResponse response = service.uploadFileToSubmission(
				"student@example.com",
				submission.getId(),
				upload,
				null);

		assertThat(response.file().id()).isEqualTo(70L);
		assertThat(response.file().submissionId()).isEqualTo(40L);
		assertThat(response.file().fileStatus()).isEqualTo(SubmissionFileStatus.UPLOADED);
		assertThat(response.file().storedFileName()).isEqualTo("file-uuid-srs.pdf");
		assertThat(response.file().storedFileName()).isNotEqualTo(response.file().originalFileName());
		assertThat(response.file().downloadUrl()).endsWith("/api/submission-files/70/download");
	}

	@Test
	void shouldRejectUploadToAnotherStudentsDraftSubmission() {
		User owner = user(1L, "owner@example.com", Role.STUDENT);
		User otherStudent = user(2L, "other@example.com", Role.STUDENT);
		Submission submission = draftSubmission(owner);
		when(identityQueryService.getUserByEmail("other@example.com")).thenReturn(otherStudent);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		assertThatThrownBy(() -> service.uploadFileToSubmission(
				"other@example.com",
				40L,
				pdfFile("srs.pdf", "content"),
				null))
				.isInstanceOf(AccessDeniedException.class);

		verify(storageService, never()).store(any(), any());
	}

	@Test
	void shouldRejectUploadToSubmittedSubmission() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		Submission submission = draftSubmission(student);
		submission.setStatus(SubmissionStatus.SUBMITTED);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		assertThatThrownBy(() -> service.uploadFileToSubmission(
				"student@example.com",
				40L,
				pdfFile("srs.pdf", "content"),
				null))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Only draft submissions can accept file changes");
	}

	@Test
	void shouldRejectUploadToArchivedSubmission() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		Submission submission = draftSubmission(student);
		submission.setStatus(SubmissionStatus.ARCHIVED);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		assertThatThrownBy(() -> service.uploadFileToSubmission(
				"student@example.com",
				40L,
				pdfFile("srs.pdf", "content"),
				null))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Archived submissions are read-only");
	}

	@Test
	void shouldRejectFileExceedingMaxSize() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		Submission submission = draftSubmission(student);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		assertThatThrownBy(() -> service.uploadFileToSubmission(
				"student@example.com",
				40L,
				pdfFile("srs.pdf", "content-too-large"),
				null))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Uploaded file exceeds the maximum allowed size");
	}

	@Test
	void shouldRejectInvalidExtensionWhenRequirementDefinesAllowedFileTypes() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		Submission submission = draftSubmission(student);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(submissionRepository.findById(40L)).thenReturn(Optional.of(submission));

		assertThatThrownBy(() -> service.uploadFileToSubmission(
				"student@example.com",
				40L,
				new MockMultipartFile(
						"file",
						"srs.docx",
						"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
						"content".getBytes()),
				null))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("File type is not allowed for this document requirement");
	}

	@Test
	void shouldDownloadFileForAuthorizedOwner() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		Submission submission = draftSubmission(student);
		SubmissionFile file = uploadedFile(70L, submission);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(fileRepository.findById(70L)).thenReturn(Optional.of(file));
		when(storageService.loadAsResource("requirement-set-10/assignment-30/submission-40/file-uuid-srs.pdf"))
				.thenReturn(new ByteArrayResource("content".getBytes()));

		SubmissionFileResource resource = service.downloadFile(70L, "student@example.com");

		assertThat(resource.originalFileName()).isEqualTo("srs.pdf");
		assertThat(resource.contentType()).isEqualTo("application/pdf");
		assertThat(resource.resource().exists()).isTrue();
	}

	@Test
	void shouldRejectDownloadForUnauthorizedStudent() {
		User owner = user(1L, "owner@example.com", Role.STUDENT);
		User otherStudent = user(2L, "other@example.com", Role.STUDENT);
		Submission submission = draftSubmission(owner);
		SubmissionFile file = uploadedFile(70L, submission);
		when(identityQueryService.getUserByEmail("other@example.com")).thenReturn(otherStudent);
		when(fileRepository.findById(70L)).thenReturn(Optional.of(file));

		assertThatThrownBy(() -> service.downloadFile(70L, "other@example.com"))
				.isInstanceOf(AccessDeniedException.class);

		verify(storageService, never()).loadAsResource(any());
	}

	@Test
	void shouldMarkRemovedFileAsRemoved() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		Submission submission = draftSubmission(student);
		SubmissionFile file = uploadedFile(70L, submission);
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(fileRepository.findById(70L)).thenReturn(Optional.of(file));
		when(fileRepository.save(any(SubmissionFile.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = service.removeFile(70L, "student@example.com");

		assertThat(response.fileStatus()).isEqualTo(SubmissionFileStatus.REMOVED);
		verify(storageService, never()).delete(any());
	}

	@Test
	void shouldMarkOldFileAsReplacedAndCreateNewUploadedRecord() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		Submission submission = draftSubmission(student);
		SubmissionFile oldFile = uploadedFile(70L, submission);
		MockMultipartFile upload = pdfFile("srs-v2.pdf", "v2");
		when(identityQueryService.getUserByEmail("student@example.com")).thenReturn(student);
		when(fileRepository.findById(70L)).thenReturn(Optional.of(oldFile));
		when(fileRepository.save(any(SubmissionFile.class))).thenAnswer(invocation -> {
			SubmissionFile savedFile = invocation.getArgument(0);
			if (savedFile.getId() == null) {
				savedFile.setId(71L);
			}
			return savedFile;
		});
		when(storageService.store(upload, submission)).thenReturn(new StoredFile(
				"srs-v2.pdf",
				"file-uuid-srs-v2.pdf",
				"requirement-set-10/assignment-30/submission-40/file-uuid-srs-v2.pdf",
				"application/pdf",
				2L,
				"PDF",
				"checksum-v2"));

		FileUploadResponse response = service.replaceFile(70L, upload, "student@example.com");

		assertThat(oldFile.getFileStatus()).isEqualTo(SubmissionFileStatus.REPLACED);
		assertThat(response.file().id()).isEqualTo(71L);
		assertThat(response.file().fileStatus()).isEqualTo(SubmissionFileStatus.UPLOADED);
	}

	private void stubUpload(User student, Submission submission, MockMultipartFile upload) {
		when(identityQueryService.getUserByEmail(student.getEmail())).thenReturn(student);
		when(submissionRepository.findById(submission.getId())).thenReturn(Optional.of(submission));
		when(fileRepository.save(any(SubmissionFile.class))).thenAnswer(invocation -> {
			SubmissionFile savedFile = invocation.getArgument(0);
			if (savedFile.getId() == null) {
				savedFile.setId(70L);
			}
			return savedFile;
		});
		when(storageService.store(upload, submission)).thenReturn(new StoredFile(
				"srs.pdf",
				"file-uuid-srs.pdf",
				"requirement-set-10/assignment-30/submission-40/file-uuid-srs.pdf",
				"application/pdf",
				7L,
				"PDF",
				"checksum"));
	}

	private MockMultipartFile pdfFile(String originalFileName, String content) {
		return new MockMultipartFile(
				"file",
				originalFileName,
				"application/pdf",
				content.getBytes());
	}

	private User user(Long id, String email, Role role) {
		User user = new User("Test", null, "User", email, "encoded-password", role);
		user.setId(id);
		return user;
	}

	private Submission draftSubmission(User student) {
		DocumentRequirementSet requirementSet = new DocumentRequirementSet("Requirement Set", ConfigurationStatus.ACTIVE);
		requirementSet.setId(10L);
		requirementSet.setOwnerInstructor(user(90L, "instructor@example.com", Role.INSTRUCTOR));

		DocumentRequirement requirement = new DocumentRequirement("SRS", 1);
		requirement.setId(20L);
		requirement.setRequirementSet(requirementSet);
		requirement.getAllowedFileTypes().add(AllowedFileType.PDF);

		DocumentRequirementSetAssignment assignment = new DocumentRequirementSetAssignment(
				requirementSet,
				RequirementSetAssignmentType.COURSE_CLASS,
				RequirementSetAssignmentStatus.ACTIVE,
				LocalDateTime.now());
		assignment.setId(30L);

		Submission submission = new Submission();
		submission.setType(SubmissionType.DOCUMENT);
		submission.setId(40L);
		submission.setSubmittedBy(student);
		submission.setAssignmentId(assignment.getId());
		submission.setRequirementId(requirement.getId());
		submission.setStatus(SubmissionStatus.DRAFT);
		submission.setSubmissionTitle("SRS");
		submission.setAttemptNumber(1);
		submission.setLastUpdatedAt(LocalDateTime.now());
		org.mockito.Mockito.lenient().when(assignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));
		org.mockito.Mockito.lenient().when(requirementRepository.findById(requirement.getId())).thenReturn(Optional.of(requirement));
		return submission;
	}

	private SubmissionFile uploadedFile(Long id, Submission submission) {
		SubmissionFile file = new SubmissionFile();
		file.setId(id);
		file.setSubmission(submission);
		file.setOriginalFileName("srs.pdf");
		file.setStoredFileName("file-uuid-srs.pdf");
		file.setStoragePath("requirement-set-10/assignment-30/submission-40/file-uuid-srs.pdf");
		file.setContentType("application/pdf");
		file.setFileSize(7L);
		file.setFileExtension("PDF");
		file.setChecksum("checksum");
		file.setFileStatus(SubmissionFileStatus.UPLOADED);
		file.setUploadedAt(LocalDateTime.now());
		return file;
	}
}
