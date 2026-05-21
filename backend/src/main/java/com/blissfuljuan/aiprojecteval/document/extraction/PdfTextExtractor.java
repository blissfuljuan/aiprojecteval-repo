package com.blissfuljuan.aiprojecteval.document.extraction;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
class PdfTextExtractor {

	String extract(Path file) {
		try (PDDocument document = Loader.loadPDF(file.toFile())) {
			return new PDFTextStripper().getText(document);
		} catch (IOException exception) {
			throw new DocumentTextExtractionException("PDF text extraction failed: " + exception.getMessage(), exception);
		}
	}
}
