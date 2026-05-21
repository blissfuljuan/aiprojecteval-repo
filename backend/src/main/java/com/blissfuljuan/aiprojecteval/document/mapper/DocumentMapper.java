package com.blissfuljuan.aiprojecteval.document.mapper;

import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentSummaryResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentVersionResponse;
import com.blissfuljuan.aiprojecteval.document.model.Document;
import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

	public DocumentResponse toResponse(Document document, DocumentVersion currentVersion) {
		return new DocumentResponse(
				document.getId(),
				document.getContextType(),
				document.getContextId(),
				document.getDocumentType(),
				document.getTitle(),
				document.getDescription(),
				document.getStatus(),
				document.getCurrentVersionId(),
				document.getCreatedByUserId(),
				toVersionResponse(currentVersion),
				document.getCreatedAt(),
				document.getUpdatedAt()
		);
	}

	public DocumentSummaryResponse toSummaryResponse(Document document, DocumentVersion currentVersion) {
		return new DocumentSummaryResponse(
				document.getId(),
				document.getContextType(),
				document.getContextId(),
				document.getDocumentType(),
				document.getTitle(),
				document.getStatus(),
				document.getCurrentVersionId(),
				currentVersion == null ? null : currentVersion.getVersionNumber(),
				currentVersion == null ? null : currentVersion.getValidationStatus(),
				currentVersion == null ? null : currentVersion.getExtractionStatus(),
				document.getUpdatedAt()
		);
	}

	public DocumentVersionResponse toVersionResponse(DocumentVersion version) {
		if (version == null) {
			return null;
		}

		return new DocumentVersionResponse(
				version.getId(),
				version.getDocument().getId(),
				version.getVersionNumber(),
				version.getSourceType(),
				version.getProvider(),
				version.getOriginalUrl(),
				version.getExternalFileId(),
				version.getStorageProvider(),
				version.getStorageKey(),
				version.getFileName(),
				version.getMimeType(),
				version.getFileSizeBytes(),
				version.getChecksum(),
				version.getValidationStatus(),
				version.getExtractionStatus(),
				version.getWordCount(),
				version.getSubmittedByUserId(),
				version.getSubmittedAt(),
				version.getValidatedAt(),
				version.getExtractedAt(),
				version.getValidationMessage(),
				version.getExtractionErrorMessage(),
				version.getCreatedAt(),
				version.getUpdatedAt()
		);
	}
}
