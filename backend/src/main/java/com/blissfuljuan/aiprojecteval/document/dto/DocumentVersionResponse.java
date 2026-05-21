package com.blissfuljuan.aiprojecteval.document.dto;

import com.blissfuljuan.aiprojecteval.document.model.DocumentExtractionStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import com.blissfuljuan.aiprojecteval.document.model.DocumentSourceType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;
import java.time.LocalDateTime;

public record DocumentVersionResponse(
		Long id,
		Long documentId,
		Integer versionNumber,
		DocumentSourceType sourceType,
		DocumentProvider provider,
		String originalUrl,
		String externalFileId,
		String storageProvider,
		String storageKey,
		String fileName,
		String mimeType,
		Long fileSizeBytes,
		String checksum,
		DocumentValidationStatus validationStatus,
		DocumentExtractionStatus extractionStatus,
		Integer wordCount,
		Long submittedByUserId,
		LocalDateTime submittedAt,
		LocalDateTime validatedAt,
		LocalDateTime extractedAt,
		String validationMessage,
		String extractionErrorMessage,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
