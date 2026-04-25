package com.blissfuljuan.aiprojecteval.report.service;

import com.blissfuljuan.aiprojecteval.report.dto.ReportResponse;
import java.util.List;

public interface ReportService {

	List<ReportResponse> findAll();
}
