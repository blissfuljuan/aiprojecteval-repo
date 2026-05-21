package com.blissfuljuan.aiprojecteval.document.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.document.dto.DocumentLinkSubmitRequest;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
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
				googleDriveProperties);
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
}
