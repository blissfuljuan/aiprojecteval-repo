package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AddDocumentEvaluationFindingRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.BulkUpdateCriterionScoresRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CompleteDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReturnDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.StartDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateCriterionScoreRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentEvaluationFeedbackRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentEvaluationFindingRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationFindingResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationSummaryResponse;
import java.util.List;

public interface DocumentEvaluationService {

	DocumentEvaluationResponse startEvaluation(String currentUserEmail, StartDocumentEvaluationRequest request);

	DocumentEvaluationResponse getEvaluationById(String currentUserEmail, Long evaluationId);

	DocumentEvaluationResponse getEvaluationBySubmission(String currentUserEmail, Long submissionId);

	List<DocumentEvaluationSummaryResponse> getEvaluationsByAssignment(String currentUserEmail, Long assignmentId);

	List<DocumentEvaluationSummaryResponse> getMySubmittedEvaluations(String currentUserEmail);

	List<DocumentEvaluationSummaryResponse> getMyAssignedEvaluations(String currentUserEmail);

	DocumentEvaluationResponse updateCriterionScore(
			String currentUserEmail,
			Long evaluationId,
			Long criterionScoreId,
			UpdateCriterionScoreRequest request);

	DocumentEvaluationResponse bulkUpdateCriterionScores(
			String currentUserEmail,
			Long evaluationId,
			BulkUpdateCriterionScoresRequest request);

	DocumentEvaluationResponse addFinding(
			String currentUserEmail,
			Long evaluationId,
			AddDocumentEvaluationFindingRequest request);

	DocumentEvaluationResponse updateFinding(
			String currentUserEmail,
			Long evaluationId,
			Long findingId,
			UpdateDocumentEvaluationFindingRequest request);

	DocumentEvaluationFindingResponse deleteFinding(String currentUserEmail, Long evaluationId, Long findingId);

	DocumentEvaluationResponse updateFeedback(
			String currentUserEmail,
			Long evaluationId,
			UpdateDocumentEvaluationFeedbackRequest request);

	DocumentEvaluationResponse completeEvaluation(
			String currentUserEmail,
			Long evaluationId,
			CompleteDocumentEvaluationRequest request);

	DocumentEvaluationResponse returnEvaluation(
			String currentUserEmail,
			Long evaluationId,
			ReturnDocumentEvaluationRequest request);

	DocumentEvaluationResponse archiveEvaluation(String currentUserEmail, Long evaluationId);
}
