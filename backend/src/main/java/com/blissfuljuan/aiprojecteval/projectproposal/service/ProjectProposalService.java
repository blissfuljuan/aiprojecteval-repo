package com.blissfuljuan.aiprojecteval.projectproposal.service;

import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.AdviserDecisionRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalDocumentLinkRequest;
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

	ProjectProposalResponse adviserDecision(String currentUserEmail, Long id, AdviserDecisionRequest request);

	ProjectProposalResponse instructorDecision(String currentUserEmail, Long id, ProposalDecisionRequest request);

	List<DocumentResponse> getProposalDocuments(String currentUserEmail, Long id);

	DocumentResponse submitProposalDocumentLink(
			String currentUserEmail,
			Long id,
			ProjectProposalDocumentLinkRequest request);

	void validateCanViewProposalDocuments(String currentUserEmail, Long id);

	void validateCanManageProposalDocuments(String currentUserEmail, Long id);
}
