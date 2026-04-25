package com.blissfuljuan.aiprojecteval.repositoryanalysis.service;

import com.blissfuljuan.aiprojecteval.repositoryanalysis.dto.RepositoryAnalysisResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class RepositoryAnalysisServiceImpl implements RepositoryAnalysisService {

	@Override
	public List<RepositoryAnalysisResponse> findAll() {
		return List.of();
	}
}
