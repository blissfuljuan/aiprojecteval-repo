package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PresetDocumentRequirementRequest(
		@NotBlank(message = "Name is required")
		@Size(max = 150, message = "Name must not exceed 150 characters")
		String name,

		@Size(max = 1000, message = "Description must not exceed 1000 characters")
		String description,

		Boolean required,

		List<AllowedFileType> allowedFileTypes,

		@NotNull(message = "Sort order is required")
		@Min(value = 0, message = "Sort order must be greater than or equal to 0")
		Integer sortOrder,

		Long templateId,

		Long rubricId
) {
}
