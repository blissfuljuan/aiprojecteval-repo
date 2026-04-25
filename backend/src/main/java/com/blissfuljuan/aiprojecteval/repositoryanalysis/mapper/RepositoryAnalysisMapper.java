package com.blissfuljuan.aiprojecteval.repositoryanalysis.mapper;

import com.blissfuljuan.aiprojecteval.repositoryanalysis.dto.RepositoryAnalysisResponse;
import com.blissfuljuan.aiprojecteval.repositoryanalysis.model.RepositoryAnalysis;
import org.springframework.stereotype.Component;

@Component
public class RepositoryAnalysisMapper {

	public RepositoryAnalysisResponse toResponse(RepositoryAnalysis repositoryAnalysis) {
		return new RepositoryAnalysisResponse(repositoryAnalysis.getId(), null);
	}
}
