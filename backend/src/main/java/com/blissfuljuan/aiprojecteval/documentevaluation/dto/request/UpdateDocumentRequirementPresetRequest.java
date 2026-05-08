package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.PresetVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateDocumentRequirementPresetRequest(
		@NotBlank(message = "Name is required")
		@Size(max = 150, message = "Name must not exceed 150 characters")
		String name,

		@Size(max = 1000, message = "Description must not exceed 1000 characters")
		String description,

		@Size(max = 100, message = "Category must not exceed 100 characters")
		String category,

		@NotNull(message = "Visibility is required")
		PresetVisibility visibility,

		ConfigurationStatus status
) {
}
