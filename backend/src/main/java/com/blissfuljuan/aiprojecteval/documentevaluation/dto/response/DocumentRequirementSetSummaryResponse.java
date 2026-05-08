package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import java.time.LocalDateTime;

public record DocumentRequirementSetSummaryResponse(
		Long id,
		String name,
		String description,
		Long sourcePresetId,
		Long ownerInstructorId,
		String ownerInstructorName,
		String ownerInstructorEmail,
		ConfigurationStatus status,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		int documentRequirementCount
) {
}
