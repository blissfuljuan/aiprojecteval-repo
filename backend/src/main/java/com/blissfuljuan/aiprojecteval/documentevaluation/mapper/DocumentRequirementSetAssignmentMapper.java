package com.blissfuljuan.aiprojecteval.documentevaluation.mapper;

import com.blissfuljuan.aiprojecteval.courseclass.dto.CourseClassResponse;
import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.AssignedByResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetAssignmentResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetAssignmentSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import com.blissfuljuan.aiprojecteval.project.model.Project;

public final class DocumentRequirementSetAssignmentMapper {

	private DocumentRequirementSetAssignmentMapper() {
	}

	public static DocumentRequirementSetAssignmentResponse toResponse(
			DocumentRequirementSetAssignment assignment) {
		return new DocumentRequirementSetAssignmentResponse(
				assignment.getId(),
				DocumentRequirementSetMapper.toSummaryResponse(assignment.getRequirementSet()),
				assignment.getAssignmentType(),
				assignment.getStatus(),
				toCourseClassResponse(assignment.getCourseClass()),
				toProjectResponse(assignment.getProject()),
				toAssignedByResponse(assignment.getAssignedBy()),
				assignment.getAssignedAt(),
				assignment.getDeactivatedAt(),
				assignment.getNotes(),
				assignment.getCreatedAt(),
				assignment.getUpdatedAt()
		);
	}

	public static DocumentRequirementSetAssignmentSummaryResponse toSummaryResponse(
			DocumentRequirementSetAssignment assignment) {
		CourseClass courseClass = assignment.getCourseClass();
		Project project = assignment.getProject();
		User assignedBy = assignment.getAssignedBy();

		return new DocumentRequirementSetAssignmentSummaryResponse(
				assignment.getId(),
				assignment.getRequirementSet().getId(),
				assignment.getRequirementSet().getName(),
				assignment.getAssignmentType(),
				assignment.getStatus(),
				courseClass == null ? null : courseClass.getId(),
				courseClass == null ? null : courseClass.getName(),
				project == null ? null : project.getId(),
				project == null ? null : project.getTitle(),
				assignedBy == null ? null : assignedBy.getId(),
				assignedBy == null ? null : assignedBy.getEmail(),
				assignment.getAssignedAt(),
				assignment.getDeactivatedAt(),
				assignment.getNotes()
		);
	}

	private static CourseClassResponse toCourseClassResponse(CourseClass courseClass) {
		if (courseClass == null) {
			return null;
		}
		return CourseClassResponse.fromEntity(courseClass);
	}

	private static ProjectResponse toProjectResponse(Project project) {
		if (project == null) {
			return null;
		}
		return new ProjectResponse(
				project.getId(),
				project.getOwnerUserId(),
				project.getOwnerEmail(),
				project.getTitle(),
				project.getDescription(),
				project.getRepositoryUrl(),
				project.getProjectProposalId(),
				project.getCreatedAt(),
				project.getUpdatedAt()
		);
	}

	private static AssignedByResponse toAssignedByResponse(User user) {
		if (user == null) {
			return null;
		}
		return new AssignedByResponse(user.getId(), formatUserName(user), user.getEmail(), user.getRole());
	}

	private static String formatUserName(User user) {
		StringBuilder name = new StringBuilder(user.getFirstName());
		if (user.getMiddleName() != null && !user.getMiddleName().isBlank()) {
			name.append(' ').append(user.getMiddleName());
		}
		name.append(' ').append(user.getLastName());
		return name.toString();
	}
}
