package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AddDocumentEvaluationFindingRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.BulkUpdateCriterionScoresRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CompleteDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.PublishDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReturnDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.StartDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UnpublishDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateCriterionScoreRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentEvaluationFeedbackRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentEvaluationFindingRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationFindingResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.EvaluationPublicationStatusResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.StudentEvaluationResultResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.StudentEvaluationResultSummaryResponse;
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

	EvaluationPublicationStatusResponse publishEvaluation(
			String currentUserEmail,
			Long evaluationId,
			PublishDocumentEvaluationRequest request);

	EvaluationPublicationStatusResponse unpublishEvaluation(
			String currentUserEmail,
			Long evaluationId,
			UnpublishDocumentEvaluationRequest request);

	EvaluationPublicationStatusResponse getPublicationStatus(String currentUserEmail, Long evaluationId);

	StudentEvaluationResultResponse getMySubmissionResult(String currentUserEmail, Long submissionId);

	List<StudentEvaluationResultSummaryResponse> getMyPublishedResults(String currentUserEmail);

	List<StudentEvaluationResultSummaryResponse> getMyPublishedResultsByAssignment(
			String currentUserEmail,
			Long assignmentId);

	List<StudentEvaluationResultSummaryResponse> getMyPublishedResultsByProject(
			String currentUserEmail,
			Long projectId);

	List<StudentEvaluationResultSummaryResponse> getPublishedResultsByAssignment(
			String currentUserEmail,
			Long assignmentId);
}
