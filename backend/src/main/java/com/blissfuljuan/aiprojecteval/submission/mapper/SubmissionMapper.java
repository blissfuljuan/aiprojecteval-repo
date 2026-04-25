package com.blissfuljuan.aiprojecteval.submission.mapper;

import com.blissfuljuan.aiprojecteval.submission.dto.SubmissionResponse;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {

	public SubmissionResponse toResponse(Submission submission) {
		return new SubmissionResponse(submission.getId(), null);
	}
}
