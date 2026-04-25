package com.blissfuljuan.aiprojecteval.project.service;

import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class ProjectServiceImpl implements ProjectService {

	@Override
	public List<ProjectResponse> findAll() {
		return List.of();
	}
}
