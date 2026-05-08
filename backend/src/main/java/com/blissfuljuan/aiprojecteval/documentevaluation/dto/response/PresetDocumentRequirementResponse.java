package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import java.time.LocalDateTime;
import java.util.List;

public record PresetDocumentRequirementResponse(
		Long id,
		Long presetId,
		String name,
		String description,
		boolean required,
		List<AllowedFileType> allowedFileTypes,
		Integer sortOrder,
		Long templateId,
		String templateName,
		Long rubricId,
		String rubricName,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
