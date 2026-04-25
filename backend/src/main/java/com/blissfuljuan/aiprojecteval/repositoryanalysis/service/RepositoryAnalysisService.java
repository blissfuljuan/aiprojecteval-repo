package com.blissfuljuan.aiprojecteval.repositoryanalysis.service;

import com.blissfuljuan.aiprojecteval.repositoryanalysis.dto.RepositoryAnalysisResponse;
import java.util.List;

public interface RepositoryAnalysisService {

	List<RepositoryAnalysisResponse> findAll();
}
