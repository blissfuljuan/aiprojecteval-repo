package com.blissfuljuan.aiprojecteval.document.extraction;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

@Component
class DocxTextExtractor {

	String extract(Path file) {
		try (InputStream inputStream = Files.newInputStream(file);
				XWPFDocument document = new XWPFDocument(inputStream);
				XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
			return extractor.getText();
		} catch (IOException exception) {
			throw new DocumentTextExtractionException("DOCX text extraction failed: " + exception.getMessage(), exception);
		}
	}
}
