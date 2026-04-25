package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.submission.dto.SubmissionResponse;
import java.util.List;

public interface SubmissionService {

	List<SubmissionResponse> findAll();
}
