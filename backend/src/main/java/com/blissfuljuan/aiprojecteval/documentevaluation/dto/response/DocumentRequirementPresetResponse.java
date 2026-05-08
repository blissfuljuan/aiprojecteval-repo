package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.PresetVisibility;
import java.time.LocalDateTime;
import java.util.List;

public record DocumentRequirementPresetResponse(
		Long id,
		String name,
		String description,
		String category,
		PresetVisibility visibility,
		ConfigurationStatus status,
		Long createdById,
		String createdByName,
		String createdByEmail,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		List<PresetDocumentRequirementResponse> documentRequirements
) {
}
