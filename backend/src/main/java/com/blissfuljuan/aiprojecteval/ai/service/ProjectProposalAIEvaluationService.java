package com.blissfuljuan.aiprojecteval.ai.service;

import com.blissfuljuan.aiprojecteval.ai.dto.ProjectProposalAIEvaluationRequest;
import com.blissfuljuan.aiprojecteval.ai.dto.ProjectProposalAIEvaluationResponse;
import java.util.List;

public interface ProjectProposalAIEvaluationService {

	ProjectProposalAIEvaluationResponse evaluate(Long proposalId, ProjectProposalAIEvaluationRequest request);

	List<ProjectProposalAIEvaluationResponse> findByProposal(Long proposalId);

	ProjectProposalAIEvaluationResponse findLatestByProposal(Long proposalId);

	ProjectProposalAIEvaluationResponse findByEvaluationId(Long evaluationId);
}
