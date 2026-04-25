package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.submission.dto.SubmissionResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class SubmissionServiceImpl implements SubmissionService {

	@Override
	public List<SubmissionResponse> findAll() {
		return List.of();
	}
}
