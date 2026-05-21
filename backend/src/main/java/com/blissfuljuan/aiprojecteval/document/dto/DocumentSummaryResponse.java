package com.blissfuljuan.aiprojecteval.document.dto;

import com.blissfuljuan.aiprojecteval.document.model.DocumentContextType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentExtractionStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;
import java.time.LocalDateTime;

public record DocumentSummaryResponse(
		Long id,
		DocumentContextType contextType,
		Long contextId,
		DocumentType documentType,
		String title,
		DocumentStatus status,
		Long currentVersionId,
		Integer currentVersionNumber,
		DocumentValidationStatus validationStatus,
		DocumentExtractionStatus extractionStatus,
		LocalDateTime updatedAt
) {
}
