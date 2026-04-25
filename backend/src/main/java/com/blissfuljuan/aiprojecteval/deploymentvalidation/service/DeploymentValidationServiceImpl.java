package com.blissfuljuan.aiprojecteval.deploymentvalidation.service;

import com.blissfuljuan.aiprojecteval.deploymentvalidation.dto.DeploymentValidationResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class DeploymentValidationServiceImpl implements DeploymentValidationService {

	@Override
	public List<DeploymentValidationResponse> findAll() {
		return List.of();
	}
}
