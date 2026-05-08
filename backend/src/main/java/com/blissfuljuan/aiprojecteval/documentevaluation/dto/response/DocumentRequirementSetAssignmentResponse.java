package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import java.time.LocalDateTime;

public record DocumentRequirementSetAssignmentResponse(
		Long id,
		DocumentRequirementSetSummaryResponse requirementSet,
		RequirementSetAssignmentType assignmentType,
		RequirementSetAssignmentStatus status,
		CourseClassResponse courseClass,
		ProjectResponse project,
		AssignedByResponse assignedBy,
		LocalDateTime assignedAt,
		LocalDateTime deactivatedAt,
		String notes,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
