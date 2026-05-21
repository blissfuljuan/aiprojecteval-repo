package com.blissfuljuan.aiprojecteval.document.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.document.model.Document;
import com.blissfuljuan.aiprojecteval.document.model.DocumentExtractionStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import com.blissfuljuan.aiprojecteval.document.model.DocumentSourceType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentLinkValidationServiceImplTest {

	@Mock
	private GoogleDriveMetadataClient metadataClient;

	private DocumentLinkValidationServiceImpl validationService;
	private GoogleDriveProperties properties;

	@BeforeEach
	void setUp() {
		properties = new GoogleDriveProperties();
		validationService = new DocumentLinkValidationServiceImpl(
				new GoogleDriveLinkParser(),
				metadataClient,
				properties);
	}

	@Test
	void shouldReturnInvalidUrlForUnsupportedLink() {
		DocumentValidationResult result = validationService.validate(version("https://example.com/document.pdf"));

		assertThat(result.status()).isEqualTo(DocumentValidationStatus.INVALID_URL);
		assertThat(result.provider()).isEqualTo(DocumentProvider.UNKNOWN);
		assertThat(result.externalFileId()).isNull();
	}

	@Test
	void shouldMapInaccessibleMetadataResult() {
		when(metadataClient.fetchMetadata("abc123")).thenReturn(GoogleDriveMetadataResult.failure(
				DocumentValidationStatus.INACCESSIBLE,
				"Google Drive file is not accessible"));

		DocumentValidationResult result = validationService.validate(version("https://drive.google.com/file/d/abc123/view"));

		assertThat(result.status()).isEqualTo(DocumentValidationStatus.INACCESSIBLE);
		assertThat(result.provider()).isEqualTo(DocumentProvider.GOOGLE_DRIVE);
		assertThat(result.externalFileId()).isEqualTo("abc123");
	}

	@Test
	void shouldRejectUnsupportedMimeType() {
		when(metadataClient.fetchMetadata("abc123")).thenReturn(GoogleDriveMetadataResult.success(new GoogleDriveFileMetadata(
				"abc123",
				"image.png",
				"image/png",
				100L,
				"https://drive.google.com/file/d/abc123/view",
				true)));

		DocumentValidationResult result = validationService.validate(version("https://drive.google.com/file/d/abc123/view"));

		assertThat(result.status()).isEqualTo(DocumentValidationStatus.UNSUPPORTED_FILE_TYPE);
	}

	@Test
	void shouldRejectOversizedBlobFile() {
		when(metadataClient.fetchMetadata("abc123")).thenReturn(GoogleDriveMetadataResult.success(new GoogleDriveFileMetadata(
				"abc123",
				"proposal.pdf",
				"application/pdf",
				11L * 1024L * 1024L,
				"https://drive.google.com/file/d/abc123/view",
				true)));

		DocumentValidationResult result = validationService.validate(version("https://drive.google.com/file/d/abc123/view"));

		assertThat(result.status()).isEqualTo(DocumentValidationStatus.TOO_LARGE);
	}

	@Test
	void shouldAllowGoogleDocsWithoutSize() {
		when(metadataClient.fetchMetadata("doc123")).thenReturn(GoogleDriveMetadataResult.success(new GoogleDriveFileMetadata(
				"doc123",
				"Proposal",
				"application/vnd.google-apps.document",
				null,
				"https://docs.google.com/document/d/doc123/edit",
				true)));

		DocumentValidationResult result = validationService.validate(version("https://docs.google.com/document/d/doc123/edit"));

		assertThat(result.status()).isEqualTo(DocumentValidationStatus.VALID);
		assertThat(result.provider()).isEqualTo(DocumentProvider.GOOGLE_DOCS);
		assertThat(result.externalFileId()).isEqualTo("doc123");
		assertThat(result.fileName()).isEqualTo("Proposal");
		assertThat(result.fileSizeBytes()).isNull();
	}

	private DocumentVersion version(String originalUrl) {
		Document document = new Document();
		document.setId(100L);

		DocumentVersion version = new DocumentVersion();
		version.setId(200L);
		version.setDocument(document);
		version.setSourceType(DocumentSourceType.EXTERNAL_LINK);
		version.setProvider(DocumentProvider.UNKNOWN);
		version.setOriginalUrl(originalUrl);
		version.setValidationStatus(DocumentValidationStatus.PENDING);
		version.setExtractionStatus(DocumentExtractionStatus.NOT_STARTED);
		version.setSubmittedAt(LocalDateTime.now());
		return version;
	}
}
