package com.blissfuljuan.aiprojecteval.project.service;

import com.blissfuljuan.aiprojecteval.project.dto.ProjectRequest;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import java.util.List;

public interface ProjectService {

	ProjectResponse create(String currentUserEmail, ProjectRequest request);

	List<ProjectResponse> findAll(String currentUserEmail);

	ProjectResponse findById(String currentUserEmail, Long id);

	ProjectResponse update(String currentUserEmail, Long id, ProjectRequest request);

	void delete(String currentUserEmail, Long id);
}
