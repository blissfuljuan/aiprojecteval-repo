package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AssignRequirementSetToClassRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AssignRequirementSetToProjectRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.DeactivateRequirementSetAssignmentRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetAssignmentResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetAssignmentSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.MyAssignedDocumentRequirementResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import java.util.List;

public interface DocumentRequirementSetAssignmentService {

	DocumentRequirementSetAssignmentResponse assignToClass(
			String currentUserEmail,
			AssignRequirementSetToClassRequest request);

	DocumentRequirementSetAssignmentResponse assignToProject(
			String currentUserEmail,
			AssignRequirementSetToProjectRequest request);

	DocumentRequirementSetAssignmentResponse getAssignmentById(String currentUserEmail, Long id);

	List<MyAssignedDocumentRequirementResponse> getMyAssignedDocumentRequirements(String currentUserEmail);

	List<DocumentRequirementSetAssignmentSummaryResponse> getAssignmentsByClass(
			String currentUserEmail,
			Long courseClassId,
			RequirementSetAssignmentStatus status);

	List<DocumentRequirementSetAssignmentSummaryResponse> getAssignmentsByProject(
			String currentUserEmail,
			Long projectId,
			RequirementSetAssignmentStatus status);

	List<DocumentRequirementSetResponse> getActiveRequirementSetsByClass(
			String currentUserEmail,
			Long courseClassId);

	List<DocumentRequirementSetResponse> getActiveRequirementSetsByProject(
			String currentUserEmail,
			Long projectId);

	DocumentRequirementSetAssignmentResponse deactivateAssignment(
			String currentUserEmail,
			Long id,
			DeactivateRequirementSetAssignmentRequest request);

	DocumentRequirementSetAssignmentResponse archiveAssignment(String currentUserEmail, Long id);

	DocumentRequirementSetAssignmentResponse reactivateAssignment(String currentUserEmail, Long id);
}
