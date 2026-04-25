package com.blissfuljuan.aiprojecteval.report.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.report.dto.ReportResponse;
import com.blissfuljuan.aiprojecteval.report.service.ReportService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping
	public ApiResponse<List<ReportResponse>> findAll() {
		return ApiResponse.ok(reportService.findAll());
	}
}
