package com.blissfuljuan.aiprojecteval.documentevaluation.mapper;

import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmission;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmissionFile;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import java.util.Comparator;

public final class DocumentSubmissionMapper {

	private DocumentSubmissionMapper() {
	}

	public static DocumentSubmissionResponse toResponse(DocumentSubmission submission) {
		DocumentRequirementSetAssignment assignment = submission.getAssignment();
		DocumentRequirement requirement = submission.getDocumentRequirement();
		Project project = submission.getProject();
		CourseClass courseClass = submission.getCourseClass();
		User submittedBy = submission.getSubmittedBy();

		return new DocumentSubmissionResponse(
				submission.getId(),
				assignment == null ? null : assignment.getId(),
				assignment == null || assignment.getRequirementSet() == null
						? null : assignment.getRequirementSet().getId(),
				requirement == null ? null : requirement.getId(),
				requirement == null ? null : requirement.getName(),
				project == null ? null : project.getId(),
				project == null ? null : project.getTitle(),
				courseClass == null ? null : courseClass.getId(),
				courseClass == null ? null : courseClass.getName(),
				submittedBy == null ? null : submittedBy.getId(),
				submittedBy == null ? null : formatUserName(submittedBy),
				submission.getStatus(),
				submission.getSubmissionTitle(),
				submission.getSubmissionNotes(),
				submission.getAttemptNumber(),
				submission.getSubmittedAt(),
				submission.getLastUpdatedAt(),
				submission.getFiles()
						.stream()
						.sorted(Comparator.comparing(file -> file.getId() == null ? 0L : file.getId()))
						.map(DocumentSubmissionMapper::toFileResponse)
						.toList()
		);
	}

	public static DocumentSubmissionSummaryResponse toSummaryResponse(DocumentSubmission submission) {
		DocumentRequirementSetAssignment assignment = submission.getAssignment();
		DocumentRequirement requirement = submission.getDocumentRequirement();
		Project project = submission.getProject();
		CourseClass courseClass = submission.getCourseClass();
		User submittedBy = submission.getSubmittedBy();

		return new DocumentSubmissionSummaryResponse(
				submission.getId(),
				assignment == null ? null : assignment.getId(),
				assignment == null || assignment.getRequirementSet() == null
						? null : assignment.getRequirementSet().getId(),
				requirement == null ? null : requirement.getId(),
				requirement == null ? null : requirement.getName(),
				project == null ? null : project.getId(),
				project == null ? null : project.getTitle(),
				courseClass == null ? null : courseClass.getId(),
				courseClass == null ? null : courseClass.getName(),
				submittedBy == null ? null : submittedBy.getId(),
				submittedBy == null ? null : formatUserName(submittedBy),
				submission.getStatus(),
				submission.getSubmissionTitle(),
				submission.getAttemptNumber(),
				submission.getSubmittedAt(),
				submission.getLastUpdatedAt()
		);
	}

	public static DocumentSubmissionFileResponse toFileResponse(DocumentSubmissionFile file) {
		return new DocumentSubmissionFileResponse(
				file.getId(),
				file.getOriginalFileName(),
				file.getStoredFileName(),
				file.getFileUrl(),
				file.getStoragePath(),
				file.getContentType(),
				file.getFileSize(),
				file.getFileExtension(),
				file.getChecksum(),
				file.getFileStatus(),
				file.getUploadedAt()
		);
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
