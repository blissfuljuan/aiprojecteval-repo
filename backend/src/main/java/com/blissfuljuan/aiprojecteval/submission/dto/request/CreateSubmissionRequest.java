package com.blissfuljuan.aiprojecteval.submission.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateSubmissionRequest(
		@NotNull(message = "Assignment ID is required")
		Long assignmentId,

		@NotNull(message = "Requirement ID is required")
		Long requirementId,

		@NotBlank(message = "Submission title is required")
		@Size(max = 150, message = "Submission title must not exceed 150 characters")
		String submissionTitle,

		@Size(max = 2000, message = "Submission notes must not exceed 2000 characters")
		String submissionNotes,

		List<@Valid SubmissionFileMetadataRequest> files
) {
}
