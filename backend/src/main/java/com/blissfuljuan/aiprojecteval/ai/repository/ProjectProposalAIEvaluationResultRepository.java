package com.blissfuljuan.aiprojecteval.ai.repository;

import com.blissfuljuan.aiprojecteval.ai.model.AIEvaluationStatus;
import com.blissfuljuan.aiprojecteval.ai.model.AIProviderType;
import com.blissfuljuan.aiprojecteval.ai.model.ProjectProposalAIEvaluationResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectProposalAIEvaluationResultRepository
		extends JpaRepository<ProjectProposalAIEvaluationResult, Long> {

	List<ProjectProposalAIEvaluationResult> findByProposalIdOrderByCreatedAtDesc(Long proposalId);

	Optional<ProjectProposalAIEvaluationResult> findTopByProposalIdOrderByCreatedAtDesc(Long proposalId);

	Optional<ProjectProposalAIEvaluationResult> findByEvaluationId(Long evaluationId);

	Optional<ProjectProposalAIEvaluationResult> findTopByProposalIdAndDocumentVersionIdAndEvaluationProviderAndEvaluationStatusOrderByCreatedAtDesc(
			Long proposalId,
			Long documentVersionId,
			AIProviderType provider,
			AIEvaluationStatus status);
}
