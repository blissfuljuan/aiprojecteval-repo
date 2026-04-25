package com.blissfuljuan.aiprojecteval.submission.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.SubmissionResponse;
import com.blissfuljuan.aiprojecteval.submission.service.SubmissionService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

	private final SubmissionService submissionService;

	public SubmissionController(SubmissionService submissionService) {
		this.submissionService = submissionService;
	}

	@GetMapping
	public ApiResponse<List<SubmissionResponse>> findAll() {
		return ApiResponse.ok(submissionService.findAll());
	}
}
