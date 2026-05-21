package com.blissfuljuan.aiprojecteval.document.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;
import org.junit.jupiter.api.Test;

class GoogleDriveLinkParserTest {

	private final GoogleDriveLinkParser parser = new GoogleDriveLinkParser();

	@Test
	void shouldParseDriveFileUrl() {
		ParsedDocumentLink result = parser.parse("https://drive.google.com/file/d/abc123/view");

		assertThat(result.validationStatus()).isEqualTo(DocumentValidationStatus.VALID);
		assertThat(result.provider()).isEqualTo(DocumentProvider.GOOGLE_DRIVE);
		assertThat(result.fileId()).isEqualTo("abc123");
	}

	@Test
	void shouldParseDriveOpenUrl() {
		ParsedDocumentLink result = parser.parse("https://drive.google.com/open?id=abc123");

		assertThat(result.validationStatus()).isEqualTo(DocumentValidationStatus.VALID);
		assertThat(result.provider()).isEqualTo(DocumentProvider.GOOGLE_DRIVE);
		assertThat(result.fileId()).isEqualTo("abc123");
	}

	@Test
	void shouldParseDriveUcUrl() {
		ParsedDocumentLink result = parser.parse("https://drive.google.com/uc?id=abc123&export=download");

		assertThat(result.validationStatus()).isEqualTo(DocumentValidationStatus.VALID);
		assertThat(result.provider()).isEqualTo(DocumentProvider.GOOGLE_DRIVE);
		assertThat(result.fileId()).isEqualTo("abc123");
	}

	@Test
	void shouldParseGoogleDocsDocumentUrl() {
		ParsedDocumentLink result = parser.parse("https://docs.google.com/document/d/doc123/edit");

		assertThat(result.validationStatus()).isEqualTo(DocumentValidationStatus.VALID);
		assertThat(result.provider()).isEqualTo(DocumentProvider.GOOGLE_DOCS);
		assertThat(result.fileId()).isEqualTo("doc123");
	}

	@Test
	void shouldRejectUnsupportedGoogleUrls() {
		assertInvalid("https://drive.google.com/drive/folders/folder123");
		assertInvalid("https://docs.google.com/spreadsheets/d/sheet123/edit");
		assertInvalid("https://docs.google.com/presentation/d/slides123/edit");
		assertInvalid("https://docs.google.com/forms/d/form123/edit");
	}

	@Test
	void shouldRejectBlankMalformedAndNonGoogleUrls() {
		assertInvalid("");
		assertInvalid("not a url");
		assertInvalid("https://example.com/document.pdf");
	}

	private void assertInvalid(String url) {
		ParsedDocumentLink result = parser.parse(url);

		assertThat(result.validationStatus()).isEqualTo(DocumentValidationStatus.INVALID_URL);
		assertThat(result.provider()).isEqualTo(DocumentProvider.UNKNOWN);
		assertThat(result.fileId()).isNull();
	}
}
