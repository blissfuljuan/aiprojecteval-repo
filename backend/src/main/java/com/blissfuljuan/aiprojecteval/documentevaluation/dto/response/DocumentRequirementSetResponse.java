package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import java.time.LocalDateTime;
import java.util.List;

public record DocumentRequirementSetResponse(
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
		List<DocumentRequirementResponse> documentRequirements
) {
}
