package com.blissfuljuan.aiprojecteval.deploymentvalidation.service;

import com.blissfuljuan.aiprojecteval.deploymentvalidation.dto.DeploymentValidationResponse;
import java.util.List;

public interface DeploymentValidationService {

	List<DeploymentValidationResponse> findAll();
}
