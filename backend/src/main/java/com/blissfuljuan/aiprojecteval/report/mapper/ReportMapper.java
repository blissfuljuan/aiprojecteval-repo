package com.blissfuljuan.aiprojecteval.report.mapper;

import com.blissfuljuan.aiprojecteval.report.dto.ReportResponse;
import com.blissfuljuan.aiprojecteval.report.model.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

	public ReportResponse toResponse(Report report) {
		return new ReportResponse(report.getId(), null);
	}
}
