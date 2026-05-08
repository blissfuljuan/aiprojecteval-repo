package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import java.time.LocalDateTime;
import java.util.List;

public record DocumentRequirementResponse(
		Long id,
		Long requirementSetId,
		String name,
		String description,
		boolean required,
		List<AllowedFileType> allowedFileTypes,
		Integer sortOrder,
		DocumentTemplateResponse template,
		EvaluationRubricResponse rubric,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
