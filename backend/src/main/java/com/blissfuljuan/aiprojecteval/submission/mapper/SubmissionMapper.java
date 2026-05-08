package com.blissfuljuan.aiprojecteval.submission.mapper;

import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionSummaryResponse;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import com.blissfuljuan.aiprojecteval.submission.model.SubmissionFile;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import java.util.Comparator;

public final class SubmissionMapper {

	private SubmissionMapper() {
	}

	public static SubmissionResponse toResponse(Submission submission) {
		User submittedBy = submission.getSubmittedBy();

		return new SubmissionResponse(
				submission.getId(),
				submission.getType(),
				submission.getAssignmentId(),
				submission.getRequirementId(),
				submission.getProjectId(),
				submission.getCourseClassId(),
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
						.map(SubmissionMapper::toFileResponse)
						.toList()
		);
	}

	public static SubmissionSummaryResponse toSummaryResponse(Submission submission) {
		User submittedBy = submission.getSubmittedBy();

		return new SubmissionSummaryResponse(
				submission.getId(),
				submission.getType(),
				submission.getAssignmentId(),
				submission.getRequirementId(),
				submission.getProjectId(),
				submission.getCourseClassId(),
				submittedBy == null ? null : submittedBy.getId(),
				submittedBy == null ? null : formatUserName(submittedBy),
				submission.getStatus(),
				submission.getSubmissionTitle(),
				submission.getAttemptNumber(),
				submission.getSubmittedAt(),
				submission.getLastUpdatedAt()
		);
	}

	public static SubmissionFileResponse toFileResponse(SubmissionFile file) {
		Long fileId = file.getId();
		Long submissionId = file.getSubmission() == null ? null : file.getSubmission().getId();
		String downloadUrl = fileId == null ? null
				: "/api/submission-files/" + fileId + "/download";
		String viewUrl = fileId == null ? null
				: "/api/submission-files/" + fileId + "/view";

		return new SubmissionFileResponse(
				fileId,
				submissionId,
				file.getOriginalFileName(),
				file.getStoredFileName(),
				file.getFileUrl(),
				downloadUrl,
				viewUrl,
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
