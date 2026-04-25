package com.blissfuljuan.aiprojecteval.deploymentvalidation.mapper;

import com.blissfuljuan.aiprojecteval.deploymentvalidation.dto.DeploymentValidationResponse;
import com.blissfuljuan.aiprojecteval.deploymentvalidation.model.DeploymentValidation;
import org.springframework.stereotype.Component;

@Component
public class DeploymentValidationMapper {

	public DeploymentValidationResponse toResponse(DeploymentValidation deploymentValidation) {
		return new DeploymentValidationResponse(deploymentValidation.getId(), null);
	}
}
