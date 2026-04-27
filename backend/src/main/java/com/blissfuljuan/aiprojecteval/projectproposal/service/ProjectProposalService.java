package com.blissfuljuan.aiprojecteval.projectproposal.service;

import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalCreateRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalResponse;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalUpdateRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProposalDecisionRequest;
import java.util.List;

public interface ProjectProposalService {

	ProjectProposalResponse createProposal(String currentUserEmail, ProjectProposalCreateRequest request);

	List<ProjectProposalResponse> getMyProposals(String currentUserEmail);

	List<ProjectProposalResponse> getAllProposals(String currentUserEmail);

	ProjectProposalResponse getProposalById(String currentUserEmail, Long id);

	ProjectProposalResponse updateProposal(String currentUserEmail, Long id, ProjectProposalUpdateRequest request);

	void deleteProposal(String currentUserEmail, Long id);

	ProjectProposalResponse instructorDecision(String currentUserEmail, Long id, ProposalDecisionRequest request);
}
