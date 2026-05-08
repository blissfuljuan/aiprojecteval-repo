package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AddDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateRequirementSetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReorderDocumentRequirementsRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateRequirementSetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import java.util.List;

public interface DocumentRequirementSetService {

	DocumentRequirementSetResponse createRequirementSet(String currentUserEmail, CreateRequirementSetRequest request);

	List<DocumentRequirementSetSummaryResponse> getMyRequirementSets(
			String currentUserEmail,
			ConfigurationStatus status,
			String keyword);

	List<DocumentRequirementSetSummaryResponse> getRequirementSets(
			String currentUserEmail,
			ConfigurationStatus status,
			Long ownerInstructorId,
			String keyword);

	DocumentRequirementSetResponse getRequirementSetById(String currentUserEmail, Long id);

	DocumentRequirementSetResponse updateRequirementSet(
			String currentUserEmail,
			Long id,
			UpdateRequirementSetRequest request);

	DocumentRequirementSetResponse activateRequirementSet(String currentUserEmail, Long id);

	DocumentRequirementSetResponse archiveRequirementSet(String currentUserEmail, Long id);

	DocumentRequirementResponse addDocumentRequirement(
			String currentUserEmail,
			Long requirementSetId,
			AddDocumentRequirementRequest request);

	DocumentRequirementResponse updateDocumentRequirement(
			String currentUserEmail,
			Long requirementId,
			UpdateDocumentRequirementRequest request);

	void deleteDocumentRequirement(String currentUserEmail, Long requirementId);

	DocumentRequirementSetResponse reorderDocumentRequirements(
			String currentUserEmail,
			Long requirementSetId,
			ReorderDocumentRequirementsRequest request);
}
