package com.blissfuljuan.aiprojecteval.project.service;

import com.blissfuljuan.aiprojecteval.project.dto.ProjectRequest;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import java.util.List;

public interface ProjectService {

	ProjectResponse create(String currentUserEmail, ProjectRequest request);

	List<ProjectResponse> findAll(String currentUserEmail);

	List<ProjectResponse> findByOwner(Long ownerUserId);

	ProjectResponse findById(String currentUserEmail, Long id);

	ProjectResponse update(String currentUserEmail, Long id, ProjectRequest request);

	void delete(String currentUserEmail, Long id);

	boolean existsByProjectProposalId(Long projectProposalId);

	ProjectResponse createFromApprovedProposal(
			Long ownerUserId,
			String ownerEmail,
			String title,
			String description,
			Long projectProposalId);
}
