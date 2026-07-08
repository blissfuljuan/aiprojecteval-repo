package com.blissfuljuan.aiprojecteval.document.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentLinkSubmitRequest;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentVersionResponse;
import com.blissfuljuan.aiprojecteval.document.extraction.DocumentTextExtractionResult;
import com.blissfuljuan.aiprojecteval.document.extraction.DocumentTextExtractionService;
import com.blissfuljuan.aiprojecteval.document.mapper.DocumentMapper;
import com.blissfuljuan.aiprojecteval.document.model.Document;
import com.blissfuljuan.aiprojecteval.document.model.DocumentContextType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentExtractionStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import com.blissfuljuan.aiprojecteval.document.model.DocumentSourceType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import com.blissfuljuan.aiprojecteval.document.repository.DocumentRepository;
import com.blissfuljuan.aiprojecteval.document.repository.DocumentVersionRepository;
import com.blissfuljuan.aiprojecteval.document.validation.DocumentLinkValidationService;
import com.blissfuljuan.aiprojecteval.document.validation.GoogleDriveProperties;
import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.service.AuthService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

	@Mock
	private DocumentRepository documentRepository;

	@Mock
	private DocumentVersionRepository documentVersionRepository;

	@Mock
	private DocumentLinkValidationService documentLinkValidationService;

	@Mock
	private DocumentTextExtractionService documentTextExtractionService;

	@Mock
	private AuthService authService;

	private DocumentServiceImpl documentService;
	private GoogleDriveProperties googleDriveProperties;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.clearContext();
		googleDriveProperties = new GoogleDriveProperties();
		documentService = new DocumentServiceImpl(
				documentRepository,
				documentVersionRepository,
				new DocumentMapper(),
				documentLinkValidationService,
				googleDriveProperties,
				documentTextExtractionService,
				authService);
	}

	@Test
	void shouldCreateDocumentAndFirstExternalLinkVersion() {
		when(documentRepository.findByContextTypeAndContextIdAndDocumentType(
				DocumentContextType.PROJECT_PROPOSAL,
				10L,
				DocumentType.PROJECT_PROPOSAL_DOCUMENT)).thenReturn(Optional.empty());
		when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
			Document document = invocation.getArgument(0);
			document.setId(100L);
			document.setCreatedAt(LocalDateTime.now());
			document.setUpdatedAt(LocalDateTime.now());
			return document;
		});
		when(documentVersionRepository.findTopByDocumentIdOrderByVersionNumberDesc(100L)).thenReturn(Optional.empty());
		when(documentVersionRepository.save(any(DocumentVersion.class))).thenAnswer(invocation -> {
			DocumentVersion version = invocation.getArgument(0);
			version.setId(200L);
			version.setCreatedAt(LocalDateTime.now());
			version.setUpdatedAt(LocalDateTime.now());
			return version;
		});

		DocumentResponse response = documentService.submitExternalLink(request("https://drive.google.com/file/abc"));

		assertThat(response.id()).isEqualTo(100L);
		assertThat(response.status()).isEqualTo(DocumentStatus.SUBMITTED);
		assertThat(response.currentVersionId()).isEqualTo(200L);
		assertThat(response.currentVersion().versionNumber()).isEqualTo(1);
		assertThat(response.currentVersion().sourceType()).isEqualTo(DocumentSourceType.EXTERNAL_LINK);
		assertThat(response.currentVersion().provider()).isEqualTo(DocumentProvider.UNKNOWN);
		assertThat(response.currentVersion().validationStatus()).isEqualTo(DocumentValidationStatus.PENDING);
		assertThat(response.currentVersion().extractionStatus()).isEqualTo(DocumentExtractionStatus.NOT_STARTED);
	}

	@Test
	void shouldAddNextVersionToExistingDocument() {
		Document existingDocument = document();
		DocumentVersion latestVersion = version(existingDocument, 2);
		when(documentRepository.findByContextTypeAndContextIdAndDocumentType(
				DocumentContextType.PROJECT_PROPOSAL,
				10L,
				DocumentType.PROJECT_PROPOSAL_DOCUMENT)).thenReturn(Optional.of(existingDocument));
		when(documentRepository.save(existingDocument)).thenReturn(existingDocument);
		when(documentVersionRepository.findTopByDocumentIdOrderByVersionNumberDesc(100L))
				.thenReturn(Optional.of(latestVersion));
		when(documentVersionRepository.save(any(DocumentVersion.class))).thenAnswer(invocation -> {
			DocumentVersion version = invocation.getArgument(0);
			version.setId(203L);
			version.setCreatedAt(LocalDateTime.now());
			version.setUpdatedAt(LocalDateTime.now());
			return version;
		});

		DocumentResponse response = documentService.submitExternalLink(request("https://docs.google.com/document/d/next"));

		assertThat(response.id()).isEqualTo(100L);
		assertThat(response.currentVersionId()).isEqualTo(203L);
		assertThat(response.currentVersion().versionNumber()).isEqualTo(3);
		assertThat(response.currentVersion().originalUrl()).isEqualTo("https://docs.google.com/document/d/next");
		assertThat(existingDocument.getCurrentVersionId()).isEqualTo(203L);
	}

	@Test
	void shouldUseNumericAuthenticatedPrincipalAsUserIdWhenAvailable() {
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("7", null));
		when(documentRepository.findByContextTypeAndContextIdAndDocumentType(
				DocumentContextType.PROJECT_PROPOSAL,
				10L,
				DocumentType.PROJECT_PROPOSAL_DOCUMENT)).thenReturn(Optional.empty());
		when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
			Document document = invocation.getArgument(0);
			document.setId(100L);
			document.setCreatedAt(LocalDateTime.now());
			document.setUpdatedAt(LocalDateTime.now());
			return document;
		});
		when(documentVersionRepository.findTopByDocumentIdOrderByVersionNumberDesc(100L)).thenReturn(Optional.empty());
		when(documentVersionRepository.save(any(DocumentVersion.class))).thenAnswer(invocation -> {
			DocumentVersion version = invocation.getArgument(0);
			version.setId(200L);
			version.setCreatedAt(LocalDateTime.now());
			version.setUpdatedAt(LocalDateTime.now());
			return version;
		});

		documentService.submitExternalLink(request("https://example.com/document.pdf"));

		ArgumentCaptor<DocumentVersion> versionCaptor = ArgumentCaptor.forClass(DocumentVersion.class);
		verify(documentVersionRepository).save(versionCaptor.capture());
		assertThat(versionCaptor.getValue().getSubmittedByUserId()).isEqualTo(7L);
	}

	@Test
	void shouldResolveEmailAuthenticatedPrincipalAsUserId() {
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("student@example.com", null));
		when(authService.getCurrentUser("student@example.com"))
				.thenReturn(new UserResponse(7L, "Test", null, "Student", "student@example.com", Role.STUDENT, true));
		when(documentRepository.findByContextTypeAndContextIdAndDocumentType(
				DocumentContextType.PROJECT_PROPOSAL,
				10L,
				DocumentType.PROJECT_PROPOSAL_DOCUMENT)).thenReturn(Optional.empty());
		when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
			Document document = invocation.getArgument(0);
			document.setId(100L);
			document.setCreatedAt(LocalDateTime.now());
			document.setUpdatedAt(LocalDateTime.now());
			return document;
		});
		when(documentVersionRepository.findTopByDocumentIdOrderByVersionNumberDesc(100L)).thenReturn(Optional.empty());
		when(documentVersionRepository.save(any(DocumentVersion.class))).thenAnswer(invocation -> {
			DocumentVersion version = invocation.getArgument(0);
			version.setId(200L);
			version.setCreatedAt(LocalDateTime.now());
			version.setUpdatedAt(LocalDateTime.now());
			return version;
		});

		documentService.submitExternalLink(request("https://example.com/document.pdf"));

		ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
		verify(documentRepository, times(2)).save(documentCaptor.capture());
		assertThat(documentCaptor.getAllValues().get(0).getCreatedByUserId()).isEqualTo(7L);

		ArgumentCaptor<DocumentVersion> versionCaptor = ArgumentCaptor.forClass(DocumentVersion.class);
		verify(documentVersionRepository).save(versionCaptor.capture());
		assertThat(versionCaptor.getValue().getSubmittedByUserId()).isEqualTo(7L);
	}

	@Test
	void shouldBlockExtractionWhenValidationStatusIsNotValid() {
		Document existingDocument = document();
		DocumentVersion version = version(existingDocument, 1);
		when(documentRepository.findById(100L)).thenReturn(Optional.of(existingDocument));
		when(documentVersionRepository.findById(202L)).thenReturn(Optional.of(version));

		assertThatThrownBy(() -> documentService.extractVersionText(100L, 202L))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("Only VALID document versions can be extracted");
	}

	@Test
	void shouldExtractValidatedVersionText() {
		Document existingDocument = document();
		DocumentVersion version = validVersion(existingDocument);
		when(documentRepository.findById(100L)).thenReturn(Optional.of(existingDocument));
		when(documentVersionRepository.findById(202L)).thenReturn(Optional.of(version));
		when(documentVersionRepository.saveAndFlush(version)).thenReturn(version);
		when(documentTextExtractionService.extract(version)).thenReturn(new DocumentTextExtractionResult(
				"Proposal text for evaluation",
				4));
		when(documentVersionRepository.save(version)).thenReturn(version);

		DocumentVersionResponse response = documentService.extractVersionText(100L, 202L);

		assertThat(response.extractionStatus()).isEqualTo(DocumentExtractionStatus.EXTRACTED);
		assertThat(response.wordCount()).isEqualTo(4);
		assertThat(response.extractionErrorMessage()).isNull();
		assertThat(response.extractedAt()).isNotNull();
		assertThat(version.getExtractedText()).isEqualTo("Proposal text for evaluation");
		verify(documentVersionRepository).saveAndFlush(version);
	}

	@Test
	void shouldMarkExtractionFailedWhenExtractorFails() {
		Document existingDocument = document();
		DocumentVersion version = validVersion(existingDocument);
		when(documentRepository.findById(100L)).thenReturn(Optional.of(existingDocument));
		when(documentVersionRepository.findById(202L)).thenReturn(Optional.of(version));
		when(documentVersionRepository.saveAndFlush(version)).thenReturn(version);
		when(documentTextExtractionService.extract(version)).thenThrow(new RuntimeException("Unreadable PDF"));
		when(documentVersionRepository.save(version)).thenReturn(version);

		DocumentVersionResponse response = documentService.extractVersionText(100L, 202L);

		assertThat(response.extractionStatus()).isEqualTo(DocumentExtractionStatus.FAILED);
		assertThat(response.wordCount()).isNull();
		assertThat(response.extractionErrorMessage()).isEqualTo("Unreadable PDF");
		assertThat(version.getExtractedText()).isNull();
	}

	@Test
	void shouldAllowSafeReExtraction() {
		Document existingDocument = document();
		DocumentVersion version = validVersion(existingDocument);
		version.setExtractionStatus(DocumentExtractionStatus.EXTRACTED);
		version.setExtractedText("Old extracted text");
		version.setWordCount(3);
		version.setExtractedAt(LocalDateTime.now().minusDays(1));
		when(documentRepository.findById(100L)).thenReturn(Optional.of(existingDocument));
		when(documentVersionRepository.findById(202L)).thenReturn(Optional.of(version));
		when(documentVersionRepository.saveAndFlush(version)).thenReturn(version);
		when(documentTextExtractionService.extract(version)).thenReturn(new DocumentTextExtractionResult(
				"Fresh extracted text",
				3));
		when(documentVersionRepository.save(version)).thenReturn(version);

		DocumentVersionResponse response = documentService.extractVersionText(100L, 202L);

		assertThat(response.extractionStatus()).isEqualTo(DocumentExtractionStatus.EXTRACTED);
		assertThat(version.getExtractedText()).isEqualTo("Fresh extracted text");
		assertThat(version.getExtractionErrorMessage()).isNull();
		verify(documentVersionRepository, times(1)).saveAndFlush(version);
	}

	private DocumentLinkSubmitRequest request(String documentUrl) {
		return new DocumentLinkSubmitRequest(
				DocumentContextType.PROJECT_PROPOSAL,
				10L,
				DocumentType.PROJECT_PROPOSAL_DOCUMENT,
				"Proposal Document",
				"Initial proposal",
				documentUrl);
	}

	private Document document() {
		Document document = new Document();
		document.setId(100L);
		document.setContextType(DocumentContextType.PROJECT_PROPOSAL);
		document.setContextId(10L);
		document.setDocumentType(DocumentType.PROJECT_PROPOSAL_DOCUMENT);
		document.setTitle("Proposal Document");
		document.setDescription("Initial proposal");
		document.setStatus(DocumentStatus.SUBMITTED);
		document.setCurrentVersionId(202L);
		document.setCreatedAt(LocalDateTime.now());
		document.setUpdatedAt(LocalDateTime.now());
		return document;
	}

	private DocumentVersion version(Document document, int versionNumber) {
		DocumentVersion version = new DocumentVersion();
		version.setId(202L);
		version.setDocument(document);
		version.setVersionNumber(versionNumber);
		version.setSourceType(DocumentSourceType.EXTERNAL_LINK);
		version.setProvider(DocumentProvider.UNKNOWN);
		version.setOriginalUrl("https://docs.google.com/document/d/current");
		version.setValidationStatus(DocumentValidationStatus.PENDING);
		version.setExtractionStatus(DocumentExtractionStatus.NOT_STARTED);
		version.setSubmittedAt(LocalDateTime.now());
		version.setCreatedAt(LocalDateTime.now());
		version.setUpdatedAt(LocalDateTime.now());
		return version;
	}

	private DocumentVersion validVersion(Document document) {
		DocumentVersion version = version(document, 1);
		version.setProvider(DocumentProvider.GOOGLE_DRIVE);
		version.setExternalFileId("file-123");
		version.setMimeType("application/pdf");
		version.setValidationStatus(DocumentValidationStatus.VALID);
		return version;
	}
}
