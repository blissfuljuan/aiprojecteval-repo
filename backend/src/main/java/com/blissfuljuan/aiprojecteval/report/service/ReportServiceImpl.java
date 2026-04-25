package com.blissfuljuan.aiprojecteval.report.service;

import com.blissfuljuan.aiprojecteval.report.dto.ReportResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class ReportServiceImpl implements ReportService {

	@Override
	public List<ReportResponse> findAll() {
		return List.of();
	}
}
