package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import java.time.LocalDateTime;

public record DocumentRequirementSetAssignmentSummaryResponse(
		Long id,
		Long requirementSetId,
		String requirementSetName,
		RequirementSetAssignmentType assignmentType,
		RequirementSetAssignmentStatus status,
		Long courseClassId,
		String courseClassName,
		Long projectId,
		String projectTitle,
		Long assignedById,
		String assignedByEmail,
		LocalDateTime assignedAt,
		LocalDateTime deactivatedAt,
		String notes
) {
}
