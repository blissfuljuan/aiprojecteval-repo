package com.blissfuljuan.aiprojecteval.document.extraction;

import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
class DocumentTextExtractionServiceImpl implements DocumentTextExtractionService {

	private static final String PDF_MIME_TYPE = "application/pdf";
	private static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	private static final String GOOGLE_DOCS_MIME_TYPE = "application/vnd.google-apps.document";
	private static final String PLAIN_TEXT_MIME_TYPE = "text/plain";

	private final GoogleDriveDocumentContentClient googleDriveContentClient;
	private final PdfTextExtractor pdfTextExtractor;
	private final DocxTextExtractor docxTextExtractor;

	DocumentTextExtractionServiceImpl(
			GoogleDriveDocumentContentClient googleDriveContentClient,
			PdfTextExtractor pdfTextExtractor,
			DocxTextExtractor docxTextExtractor) {
		this.googleDriveContentClient = googleDriveContentClient;
		this.pdfTextExtractor = pdfTextExtractor;
		this.docxTextExtractor = docxTextExtractor;
	}

	@Override
	public DocumentTextExtractionResult extract(DocumentVersion version) {
		String extractedText;
		if (GOOGLE_DOCS_MIME_TYPE.equals(version.getMimeType()) || version.getProvider() == DocumentProvider.GOOGLE_DOCS) {
			extractedText = extractGoogleDoc(version);
		} else if (PDF_MIME_TYPE.equals(version.getMimeType())) {
			extractedText = extractBinary(version, ".pdf", pdfTextExtractor::extract);
		} else if (DOCX_MIME_TYPE.equals(version.getMimeType())) {
			extractedText = extractBinary(version, ".docx", docxTextExtractor::extract);
		} else {
			throw new DocumentTextExtractionException("Unsupported document type for text extraction: " + version.getMimeType());
		}

		return new DocumentTextExtractionResult(extractedText, countWords(extractedText));
	}

	private String extractGoogleDoc(DocumentVersion version) {
		ensureExternalFileId(version);
		try (InputStream inputStream = googleDriveContentClient.export(version.getExternalFileId(), PLAIN_TEXT_MIME_TYPE)) {
			return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException exception) {
			throw new DocumentTextExtractionException("Google Docs text extraction failed: " + exception.getMessage(), exception);
		}
	}

	private String extractBinary(DocumentVersion version, String suffix, FileTextExtractor extractor) {
		ensureExternalFileId(version);
		Path tempFile = null;
		try (InputStream inputStream = googleDriveContentClient.download(version.getExternalFileId())) {
			tempFile = Files.createTempFile("document-version-" + version.getId() + "-", suffix);
			Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			return extractor.extract(tempFile);
		} catch (IOException exception) {
			throw new DocumentTextExtractionException("Document content could not be prepared for extraction: " + exception.getMessage(), exception);
		} finally {
			if (tempFile != null) {
				try {
					Files.deleteIfExists(tempFile);
				} catch (IOException ignored) {
				}
			}
		}
	}

	private void ensureExternalFileId(DocumentVersion version) {
		if (!StringUtils.hasText(version.getExternalFileId())) {
			throw new DocumentTextExtractionException("Document version does not have an external file id");
		}
	}

	private int countWords(String text) {
		if (!StringUtils.hasText(text)) {
			return 0;
		}
		return text.trim().split("\\s+").length;
	}

	@FunctionalInterface
	private interface FileTextExtractor {

		String extract(Path file);
	}
}
