package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.documentevaluation.config.DocumentEvaluationStorageProperties;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionFileResource;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.FileUploadResponse;
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
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentSubmissionFileRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentSubmissionRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.storage.DocumentFileStorageService;
import com.blissfuljuan.aiprojecteval.documentevaluation.storage.StoredDocumentFile;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
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
class DocumentSubmissionFileServiceImplTest {

	@Mock
	private DocumentSubmissionRepository submissionRepository;

	@Mock
	private DocumentSubmissionFileRepository fileRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private DocumentFileStorageService storageService;

	private DocumentSubmissionFileServiceImpl service;

	private DocumentEvaluationStorageProperties storageProperties;

	@BeforeEach
	void setUp() {
		storageProperties = new DocumentEvaluationStorageProperties();
		storageProperties.setMaxFileSize(10L);
		DocumentSubmissionFileValidator validator =
				new DocumentSubmissionFileValidator(storageProperties, fileRepository);
		service = new DocumentSubmissionFileServiceImpl(
				submissionRepository,
				fileRepository,
				userRepository,
				storageService,
				validator);
	}

	@Test
	void shouldUploadValidFileToOwnDraftSubmission() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentSubmission submission = draftSubmission(student);
		MockMultipartFile upload = pdfFile("srs.pdf", "content");
		stubUpload(student, submission, upload);

		FileUploadResponse response = service.uploadFileToSubmission(
				"student@example.com",
				submission.getId(),
				upload,
				null);

		assertThat(response.file().id()).isEqualTo(70L);
		assertThat(response.file().submissionId()).isEqualTo(40L);
		assertThat(response.file().fileStatus()).isEqualTo(DocumentSubmissionFileStatus.UPLOADED);
		assertThat(response.file().storedFileName()).isEqualTo("file-uuid-srs.pdf");
		assertThat(response.file().storedFileName()).isNotEqualTo(response.file().originalFileName());
		assertThat(response.file().downloadUrl()).endsWith("/api/document-evaluation/submissions/files/70/download");
	}

	@Test
	void shouldRejectUploadToAnotherStudentsDraftSubmission() {
		User owner = user(1L, "owner@example.com", Role.STUDENT);
		User otherStudent = user(2L, "other@example.com", Role.STUDENT);
		DocumentSubmission submission = draftSubmission(owner);
		when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherStudent));
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
		DocumentSubmission submission = draftSubmission(student);
		submission.setStatus(DocumentSubmissionStatus.SUBMITTED);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
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
		DocumentSubmission submission = draftSubmission(student);
		submission.setStatus(DocumentSubmissionStatus.ARCHIVED);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
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
		DocumentSubmission submission = draftSubmission(student);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
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
		DocumentSubmission submission = draftSubmission(student);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
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
		DocumentSubmission submission = draftSubmission(student);
		DocumentSubmissionFile file = uploadedFile(70L, submission);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(fileRepository.findById(70L)).thenReturn(Optional.of(file));
		when(storageService.loadAsResource("requirement-set-10/assignment-30/submission-40/file-uuid-srs.pdf"))
				.thenReturn(new ByteArrayResource("content".getBytes()));

		DocumentSubmissionFileResource resource = service.downloadFile(70L, "student@example.com");

		assertThat(resource.originalFileName()).isEqualTo("srs.pdf");
		assertThat(resource.contentType()).isEqualTo("application/pdf");
		assertThat(resource.resource().exists()).isTrue();
	}

	@Test
	void shouldRejectDownloadForUnauthorizedStudent() {
		User owner = user(1L, "owner@example.com", Role.STUDENT);
		User otherStudent = user(2L, "other@example.com", Role.STUDENT);
		DocumentSubmission submission = draftSubmission(owner);
		DocumentSubmissionFile file = uploadedFile(70L, submission);
		when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherStudent));
		when(fileRepository.findById(70L)).thenReturn(Optional.of(file));

		assertThatThrownBy(() -> service.downloadFile(70L, "other@example.com"))
				.isInstanceOf(AccessDeniedException.class);

		verify(storageService, never()).loadAsResource(any());
	}

	@Test
	void shouldMarkRemovedFileAsRemoved() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentSubmission submission = draftSubmission(student);
		DocumentSubmissionFile file = uploadedFile(70L, submission);
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(fileRepository.findById(70L)).thenReturn(Optional.of(file));
		when(fileRepository.save(any(DocumentSubmissionFile.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = service.removeFile(70L, "student@example.com");

		assertThat(response.fileStatus()).isEqualTo(DocumentSubmissionFileStatus.REMOVED);
		verify(storageService, never()).delete(any());
	}

	@Test
	void shouldMarkOldFileAsReplacedAndCreateNewUploadedRecord() {
		User student = user(1L, "student@example.com", Role.STUDENT);
		DocumentSubmission submission = draftSubmission(student);
		DocumentSubmissionFile oldFile = uploadedFile(70L, submission);
		MockMultipartFile upload = pdfFile("srs-v2.pdf", "v2");
		when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
		when(fileRepository.findById(70L)).thenReturn(Optional.of(oldFile));
		when(fileRepository.save(any(DocumentSubmissionFile.class))).thenAnswer(invocation -> {
			DocumentSubmissionFile savedFile = invocation.getArgument(0);
			if (savedFile.getId() == null) {
				savedFile.setId(71L);
			}
			return savedFile;
		});
		when(storageService.store(upload, submission)).thenReturn(new StoredDocumentFile(
				"srs-v2.pdf",
				"file-uuid-srs-v2.pdf",
				"requirement-set-10/assignment-30/submission-40/file-uuid-srs-v2.pdf",
				"application/pdf",
				2L,
				"PDF",
				"checksum-v2"));

		FileUploadResponse response = service.replaceFile(70L, upload, "student@example.com");

		assertThat(oldFile.getFileStatus()).isEqualTo(DocumentSubmissionFileStatus.REPLACED);
		assertThat(response.file().id()).isEqualTo(71L);
		assertThat(response.file().fileStatus()).isEqualTo(DocumentSubmissionFileStatus.UPLOADED);
	}

	private void stubUpload(User student, DocumentSubmission submission, MockMultipartFile upload) {
		when(userRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
		when(submissionRepository.findById(submission.getId())).thenReturn(Optional.of(submission));
		when(fileRepository.save(any(DocumentSubmissionFile.class))).thenAnswer(invocation -> {
			DocumentSubmissionFile savedFile = invocation.getArgument(0);
			if (savedFile.getId() == null) {
				savedFile.setId(70L);
			}
			return savedFile;
		});
		when(storageService.store(upload, submission)).thenReturn(new StoredDocumentFile(
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

	private DocumentSubmission draftSubmission(User student) {
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

		DocumentSubmission submission = new DocumentSubmission();
		submission.setId(40L);
		submission.setSubmittedBy(student);
		submission.setAssignment(assignment);
		submission.setDocumentRequirement(requirement);
		submission.setStatus(DocumentSubmissionStatus.DRAFT);
		submission.setSubmissionTitle("SRS");
		submission.setAttemptNumber(1);
		submission.setLastUpdatedAt(LocalDateTime.now());
		return submission;
	}

	private DocumentSubmissionFile uploadedFile(Long id, DocumentSubmission submission) {
		DocumentSubmissionFile file = new DocumentSubmissionFile();
		file.setId(id);
		file.setSubmission(submission);
		file.setOriginalFileName("srs.pdf");
		file.setStoredFileName("file-uuid-srs.pdf");
		file.setStoragePath("requirement-set-10/assignment-30/submission-40/file-uuid-srs.pdf");
		file.setContentType("application/pdf");
		file.setFileSize(7L);
		file.setFileExtension("PDF");
		file.setChecksum("checksum");
		file.setFileStatus(DocumentSubmissionFileStatus.UPLOADED);
		file.setUploadedAt(LocalDateTime.now());
		return file;
	}
}
