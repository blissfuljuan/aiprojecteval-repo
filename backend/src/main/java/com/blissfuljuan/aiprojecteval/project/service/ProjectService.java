package com.blissfuljuan.aiprojecteval.project.service;

import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import java.util.List;

public interface ProjectService {

	List<ProjectResponse> findAll();
}
