package com.blissfuljuan.aiprojecteval.document.extraction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.document.model.Document;
import com.blissfuljuan.aiprojecteval.document.model.DocumentExtractionStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import com.blissfuljuan.aiprojecteval.document.model.DocumentSourceType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentTextExtractionServiceImplTest {

	private static final String PDF_MIME_TYPE = "application/pdf";
	private static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	private static final String GOOGLE_DOCS_MIME_TYPE = "application/vnd.google-apps.document";

	@Mock
	private GoogleDriveDocumentContentClient googleDriveContentClient;

	private DocumentTextExtractionServiceImpl extractionService;

	@BeforeAll
	static void setUpPdfBoxFontCache() throws IOException {
		Path fontCacheDirectory = Path.of("target", "pdfbox-cache");
		Files.createDirectories(fontCacheDirectory);
		System.setProperty("pdfbox.fontcache", fontCacheDirectory.toAbsolutePath().toString());
	}

	@BeforeEach
	void setUp() {
		extractionService = new DocumentTextExtractionServiceImpl(
				googleDriveContentClient,
				new PdfTextExtractor(),
				new DocxTextExtractor());
	}

	@Test
	void shouldExtractPdfText() throws IOException {
		DocumentVersion version = version(PDF_MIME_TYPE, DocumentProvider.GOOGLE_DRIVE);
		when(googleDriveContentClient.download("file-123")).thenReturn(new ByteArrayInputStream(pdfBytes("PDF proposal text")));

		DocumentTextExtractionResult result = extractionService.extract(version);

		assertThat(result.extractedText()).contains("PDF proposal text");
		assertThat(result.wordCount()).isEqualTo(3);
	}

	@Test
	void shouldExtractDocxText() throws IOException {
		DocumentVersion version = version(DOCX_MIME_TYPE, DocumentProvider.GOOGLE_DRIVE);
		when(googleDriveContentClient.download("file-123")).thenReturn(new ByteArrayInputStream(docxBytes("DOCX proposal text")));

		DocumentTextExtractionResult result = extractionService.extract(version);

		assertThat(result.extractedText()).contains("DOCX proposal text");
		assertThat(result.wordCount()).isEqualTo(3);
	}

	@Test
	void shouldExtractGoogleDocsExportedText() {
		DocumentVersion version = version(GOOGLE_DOCS_MIME_TYPE, DocumentProvider.GOOGLE_DOCS);
		when(googleDriveContentClient.export("file-123", "text/plain"))
				.thenReturn(new ByteArrayInputStream("Google Docs proposal text".getBytes(StandardCharsets.UTF_8)));

		DocumentTextExtractionResult result = extractionService.extract(version);

		assertThat(result.extractedText()).isEqualTo("Google Docs proposal text");
		assertThat(result.wordCount()).isEqualTo(4);
	}

	@Test
	void shouldFailForUnsupportedMimeType() {
		DocumentVersion version = version("image/png", DocumentProvider.GOOGLE_DRIVE);

		assertThatThrownBy(() -> extractionService.extract(version))
				.isInstanceOf(DocumentTextExtractionException.class)
				.hasMessageContaining("Unsupported document type");
	}

	private DocumentVersion version(String mimeType, DocumentProvider provider) {
		Document document = new Document();
		document.setId(100L);

		DocumentVersion version = new DocumentVersion();
		version.setId(202L);
		version.setDocument(document);
		version.setVersionNumber(1);
		version.setSourceType(DocumentSourceType.EXTERNAL_LINK);
		version.setProvider(provider);
		version.setExternalFileId("file-123");
		version.setMimeType(mimeType);
		version.setValidationStatus(DocumentValidationStatus.VALID);
		version.setExtractionStatus(DocumentExtractionStatus.NOT_STARTED);
		version.setSubmittedAt(LocalDateTime.now());
		return version;
	}

	private byte[] pdfBytes(String text) throws IOException {
		try (PDDocument document = new PDDocument();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			PDPage page = new PDPage();
			document.addPage(page);
			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				contentStream.beginText();
				contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
				contentStream.newLineAtOffset(100, 700);
				contentStream.showText(text);
				contentStream.endText();
			}
			document.save(outputStream);
			return outputStream.toByteArray();
		}
	}

	private byte[] docxBytes(String text) throws IOException {
		try (XWPFDocument document = new XWPFDocument();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			XWPFParagraph paragraph = document.createParagraph();
			XWPFRun run = paragraph.createRun();
			run.setText(text);
			document.write(outputStream);
			return outputStream.toByteArray();
		}
	}
}
