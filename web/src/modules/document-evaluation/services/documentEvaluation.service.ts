import { isAxiosError } from "axios";
import { api } from "@/common/lib/api";
import type { ApiResponse } from "@/modules/identity/types";
import type {
  AssignRequirementSetToClassRequest,
  AssignRequirementSetToProjectRequest,
  CopyPresetRequest,
  DeactivateRequirementSetAssignmentRequest,
  DocumentRequirement,
  DocumentRequirementPreset,
  DocumentRequirementPresetRequest,
  DocumentRequirementRequest,
  DocumentRequirementSet,
  DocumentRequirementSetAssignment,
  DocumentRequirementSetAssignmentSummary,
  DocumentRequirementSetSummary,
  PresetDocumentRequirementRequest,
  QueryParams,
  ReorderDocumentRequirementsRequest,
  RequirementSetRequest,
  RequestBody,
  UpdateDocumentRequirementRequest,
} from "@/modules/document-evaluation/types";

const BASE_URL = "/api/document-evaluation";
const PRESETS_URL = `${BASE_URL}/presets`;
const REQUIREMENT_SETS_URL = `${BASE_URL}/requirement-sets`;
const ASSIGNMENTS_URL = `${BASE_URL}/requirement-set-assignments`;
const EVALUATIONS_URL = `${BASE_URL}/evaluations`;

function unwrap<T>(response: { data: ApiResponse<T> }): T {
  return response.data.data;
}

async function listPresets(params?: QueryParams): Promise<DocumentRequirementPreset[]> {
  return unwrap(await api.get<ApiResponse<DocumentRequirementPreset[]>>(PRESETS_URL, { params }));
}

async function getPresetById(presetId: number): Promise<DocumentRequirementPreset> {
  return unwrap(await api.get<ApiResponse<DocumentRequirementPreset>>(`${PRESETS_URL}/${presetId}`));
}

async function createPreset(request: DocumentRequirementPresetRequest): Promise<DocumentRequirementPreset> {
  return unwrap(await api.post<ApiResponse<DocumentRequirementPreset>>(PRESETS_URL, request));
}

async function updatePreset(
  presetId: number,
  request: DocumentRequirementPresetRequest,
): Promise<DocumentRequirementPreset> {
  return unwrap(await api.put<ApiResponse<DocumentRequirementPreset>>(`${PRESETS_URL}/${presetId}`, request));
}

async function archivePreset(presetId: number): Promise<DocumentRequirementPreset> {
  return unwrap(await api.patch<ApiResponse<DocumentRequirementPreset>>(`${PRESETS_URL}/${presetId}/archive`));
}

async function activatePreset(presetId: number): Promise<DocumentRequirementPreset> {
  return unwrap(await api.patch<ApiResponse<DocumentRequirementPreset>>(`${PRESETS_URL}/${presetId}/activate`));
}

async function copyPreset(presetId: number, request: CopyPresetRequest): Promise<DocumentRequirementSet> {
  return unwrap(await api.post<ApiResponse<DocumentRequirementSet>>(`${PRESETS_URL}/${presetId}/copy`, request));
}

async function addPresetRequirement(
  presetId: number,
  request: PresetDocumentRequirementRequest,
): Promise<DocumentRequirementPreset> {
  return unwrap(
    await api.post<ApiResponse<DocumentRequirementPreset>>(`${PRESETS_URL}/${presetId}/document-requirements`, request),
  );
}

async function updatePresetRequirement(
  presetId: number,
  requirementId: number,
  request: PresetDocumentRequirementRequest,
): Promise<DocumentRequirementPreset> {
  return unwrap(
    await api.put<ApiResponse<DocumentRequirementPreset>>(
      `${PRESETS_URL}/${presetId}/document-requirements/${requirementId}`,
      request,
    ),
  );
}

async function removePresetRequirement(presetId: number, requirementId: number): Promise<void> {
  await api.delete<ApiResponse<void>>(`${PRESETS_URL}/${presetId}/document-requirements/${requirementId}`);
}

// TODO Phase 11B: backend currently has no preset requirement reorder endpoint.

async function listRequirementSets(params?: QueryParams): Promise<DocumentRequirementSetSummary[]> {
  return unwrap(await api.get<ApiResponse<DocumentRequirementSetSummary[]>>(REQUIREMENT_SETS_URL, { params }));
}

async function listMyRequirementSets(params?: QueryParams): Promise<DocumentRequirementSetSummary[]> {
  return unwrap(await api.get<ApiResponse<DocumentRequirementSetSummary[]>>(`${REQUIREMENT_SETS_URL}/my`, { params }));
}

async function getRequirementSetById(requirementSetId: number): Promise<DocumentRequirementSet> {
  return unwrap(await api.get<ApiResponse<DocumentRequirementSet>>(`${REQUIREMENT_SETS_URL}/${requirementSetId}`));
}

async function createRequirementSet(request: RequirementSetRequest): Promise<DocumentRequirementSet> {
  return unwrap(await api.post<ApiResponse<DocumentRequirementSet>>(REQUIREMENT_SETS_URL, request));
}

async function updateRequirementSet(
  requirementSetId: number,
  request: RequirementSetRequest,
): Promise<DocumentRequirementSet> {
  return unwrap(await api.put<ApiResponse<DocumentRequirementSet>>(`${REQUIREMENT_SETS_URL}/${requirementSetId}`, request));
}

async function archiveRequirementSet(requirementSetId: number): Promise<DocumentRequirementSet> {
  return unwrap(await api.patch<ApiResponse<DocumentRequirementSet>>(`${REQUIREMENT_SETS_URL}/${requirementSetId}/archive`));
}

async function activateRequirementSet(requirementSetId: number): Promise<DocumentRequirementSet> {
  return unwrap(await api.patch<ApiResponse<DocumentRequirementSet>>(`${REQUIREMENT_SETS_URL}/${requirementSetId}/activate`));
}

async function addDocumentRequirement(
  requirementSetId: number,
  request: DocumentRequirementRequest,
): Promise<DocumentRequirement> {
  return unwrap(
    await api.post<ApiResponse<DocumentRequirement>>(`${REQUIREMENT_SETS_URL}/${requirementSetId}/requirements`, request),
  );
}

async function updateDocumentRequirement(
  requirementId: number,
  request: UpdateDocumentRequirementRequest,
): Promise<DocumentRequirement> {
  return unwrap(await api.put<ApiResponse<DocumentRequirement>>(`${BASE_URL}/requirements/${requirementId}`, request));
}

async function removeDocumentRequirement(requirementId: number): Promise<void> {
  await api.delete<ApiResponse<void>>(`${BASE_URL}/requirements/${requirementId}`);
}

async function reorderDocumentRequirements(
  requirementSetId: number,
  request: ReorderDocumentRequirementsRequest,
): Promise<DocumentRequirementSet> {
  return unwrap(
    await api.patch<ApiResponse<DocumentRequirementSet>>(
      `${REQUIREMENT_SETS_URL}/${requirementSetId}/requirements/reorder`,
      request,
    ),
  );
}

async function getAssignmentById(assignmentId: number): Promise<DocumentRequirementSetAssignment> {
  return unwrap(await api.get<ApiResponse<DocumentRequirementSetAssignment>>(`${ASSIGNMENTS_URL}/${assignmentId}`));
}

async function listAssignmentsByClass(
  courseClassId: number,
  params?: QueryParams,
): Promise<DocumentRequirementSetAssignmentSummary[]> {
  return unwrap(
    await api.get<ApiResponse<DocumentRequirementSetAssignmentSummary[]>>(
      `${BASE_URL}/classes/${courseClassId}/requirement-set-assignments`,
      {
      params,
      },
    ),
  );
}

async function listAssignmentsByProject(
  projectId: number,
  params?: QueryParams,
): Promise<DocumentRequirementSetAssignmentSummary[]> {
  return unwrap(
    await api.get<ApiResponse<DocumentRequirementSetAssignmentSummary[]>>(
      `${BASE_URL}/projects/${projectId}/requirement-set-assignments`,
      {
      params,
      },
    ),
  );
}

async function assignRequirementSetToClass(
  request: AssignRequirementSetToClassRequest,
): Promise<DocumentRequirementSetAssignment> {
  return unwrap(await api.post<ApiResponse<DocumentRequirementSetAssignment>>(`${ASSIGNMENTS_URL}/class`, request));
}

async function assignRequirementSetToProject(
  request: AssignRequirementSetToProjectRequest,
): Promise<DocumentRequirementSetAssignment> {
  return unwrap(await api.post<ApiResponse<DocumentRequirementSetAssignment>>(`${ASSIGNMENTS_URL}/project`, request));
}

async function deactivateAssignment(
  assignmentId: number,
  request?: DeactivateRequirementSetAssignmentRequest,
): Promise<DocumentRequirementSetAssignment> {
  return unwrap(
    await api.patch<ApiResponse<DocumentRequirementSetAssignment>>(`${ASSIGNMENTS_URL}/${assignmentId}/deactivate`, request),
  );
}

async function archiveAssignment(assignmentId: number): Promise<DocumentRequirementSetAssignment> {
  return unwrap(await api.patch<ApiResponse<DocumentRequirementSetAssignment>>(`${ASSIGNMENTS_URL}/${assignmentId}/archive`));
}

async function reactivateAssignment(assignmentId: number): Promise<DocumentRequirementSetAssignment> {
  return unwrap(
    await api.patch<ApiResponse<DocumentRequirementSetAssignment>>(`${ASSIGNMENTS_URL}/${assignmentId}/reactivate`),
  );
}

async function listActiveRequirementSetsByClass(courseClassId: number): Promise<DocumentRequirementSet[]> {
  return unwrap(
    await api.get<ApiResponse<DocumentRequirementSet[]>>(`${BASE_URL}/classes/${courseClassId}/requirement-sets/active`),
  );
}

async function listActiveRequirementSetsByProject(projectId: number): Promise<DocumentRequirementSet[]> {
  return unwrap(
    await api.get<ApiResponse<DocumentRequirementSet[]>>(`${BASE_URL}/projects/${projectId}/requirement-sets/active`),
  );
}

// TODO Phase 11B: backend has class/project-specific assignment list endpoints, not a global listAssignments endpoint.

async function getMyCompletenessReport(assignmentId: number): Promise<unknown> {
  return unwrap(await api.get<ApiResponse<unknown>>(`${ASSIGNMENTS_URL}/${assignmentId}/completeness/my`));
}

async function getUserCompletenessReport(assignmentId: number, userId: number): Promise<unknown> {
  return unwrap(await api.get<ApiResponse<unknown>>(`${ASSIGNMENTS_URL}/${assignmentId}/completeness/users/${userId}`));
}

async function getProjectCompletenessReport(projectId: number, assignmentId: number): Promise<unknown> {
  return unwrap(
    await api.get<ApiResponse<unknown>>(
      `${BASE_URL}/projects/${projectId}/requirement-set-assignments/${assignmentId}/completeness`,
    ),
  );
}

async function getClassCompletenessSummary(courseClassId: number, assignmentId: number): Promise<unknown> {
  return unwrap(
    await api.get<ApiResponse<unknown>>(
      `${BASE_URL}/classes/${courseClassId}/requirement-set-assignments/${assignmentId}/completeness/summary`,
    ),
  );
}

// TODO Phase 11B: backend currently has no project completeness summary endpoint.

async function getEvaluationById(evaluationId: number): Promise<unknown> {
  return unwrap(await api.get<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}`));
}

async function getEvaluationBySubmission(submissionId: number): Promise<unknown> {
  return unwrap(await api.get<ApiResponse<unknown>>(`${BASE_URL}/submissions/${submissionId}/evaluation`));
}

async function listEvaluationsByAssignment(assignmentId: number): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>(`${ASSIGNMENTS_URL}/${assignmentId}/evaluations`));
}

async function listMySubmittedEvaluations(): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>(`${EVALUATIONS_URL}/my-submissions`));
}

async function listMyAssignedEvaluations(): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>(`${EVALUATIONS_URL}/my-evaluations`));
}

async function startEvaluation(request: RequestBody): Promise<unknown> {
  return unwrap(await api.post<ApiResponse<unknown>>(`${EVALUATIONS_URL}/start`, request));
}

async function updateCriterionScore(
  evaluationId: number,
  criterionScoreId: number,
  request: RequestBody,
): Promise<unknown> {
  return unwrap(
    await api.put<ApiResponse<unknown>>(
      `${EVALUATIONS_URL}/${evaluationId}/criterion-scores/${criterionScoreId}`,
      request,
    ),
  );
}

async function bulkUpdateCriterionScores(evaluationId: number, request: RequestBody): Promise<unknown> {
  return unwrap(await api.put<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/criterion-scores`, request));
}

async function addFinding(evaluationId: number, request: RequestBody): Promise<unknown> {
  return unwrap(await api.post<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/findings`, request));
}

async function updateFinding(evaluationId: number, findingId: number, request: RequestBody): Promise<unknown> {
  return unwrap(
    await api.put<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/findings/${findingId}`, request),
  );
}

async function removeFinding(evaluationId: number, findingId: number): Promise<unknown> {
  return unwrap(await api.delete<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/findings/${findingId}`));
}

async function updateFeedback(evaluationId: number, request: RequestBody): Promise<unknown> {
  return unwrap(await api.put<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/feedback`, request));
}

async function completeEvaluation(evaluationId: number, request: RequestBody): Promise<unknown> {
  return unwrap(await api.patch<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/complete`, request));
}

async function returnEvaluation(evaluationId: number, request: RequestBody): Promise<unknown> {
  return unwrap(await api.patch<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/return`, request));
}

async function archiveEvaluation(evaluationId: number): Promise<unknown> {
  return unwrap(await api.patch<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/archive`));
}

async function publishEvaluation(evaluationId: number, request: RequestBody): Promise<unknown> {
  return unwrap(await api.patch<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/publish`, request));
}

async function unpublishEvaluation(evaluationId: number, request: RequestBody): Promise<unknown> {
  return unwrap(await api.patch<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/unpublish`, request));
}

async function getPublicationStatus(evaluationId: number): Promise<unknown> {
  return unwrap(await api.get<ApiResponse<unknown>>(`${EVALUATIONS_URL}/${evaluationId}/publication-status`));
}

async function getStudentPublishedResults(): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>(`${BASE_URL}/evaluation-results/my`));
}

async function getStudentPublishedResultsByAssignment(assignmentId: number): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>(`${ASSIGNMENTS_URL}/${assignmentId}/evaluation-results/my`));
}

async function getStudentPublishedResultsByProject(projectId: number): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>(`${BASE_URL}/projects/${projectId}/evaluation-results/my`));
}

async function getStudentPublishedResultBySubmission(submissionId: number): Promise<unknown> {
  return unwrap(await api.get<ApiResponse<unknown>>(`${BASE_URL}/submissions/${submissionId}/result`));
}

async function listPublishedResultsByAssignment(assignmentId: number): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>(`${ASSIGNMENTS_URL}/${assignmentId}/evaluation-results`));
}

// TODO Phase 11B: backend exposes student result details by submission id, not result id.

function getErrorMessage(error: unknown): string {
  if (isAxiosError<ApiResponse<unknown>>(error)) {
    const data = error.response?.data;
    return data?.errors?.[0] ?? data?.message ?? "Request failed. Please try again.";
  }
  if (error instanceof Error) return error.message;
  return "Request failed. Please try again.";
}

export const documentEvaluationService = {
  listPresets,
  getPresetById,
  createPreset,
  updatePreset,
  archivePreset,
  activatePreset,
  copyPreset,
  addPresetRequirement,
  updatePresetRequirement,
  removePresetRequirement,
  listRequirementSets,
  listMyRequirementSets,
  getRequirementSetById,
  createRequirementSet,
  updateRequirementSet,
  archiveRequirementSet,
  activateRequirementSet,
  addDocumentRequirement,
  updateDocumentRequirement,
  removeDocumentRequirement,
  reorderDocumentRequirements,
  getAssignmentById,
  listAssignmentsByClass,
  listAssignmentsByProject,
  assignRequirementSetToClass,
  assignRequirementSetToProject,
  deactivateAssignment,
  archiveAssignment,
  reactivateAssignment,
  listActiveRequirementSetsByClass,
  listActiveRequirementSetsByProject,
  getMyCompletenessReport,
  getUserCompletenessReport,
  getProjectCompletenessReport,
  getClassCompletenessSummary,
  getEvaluationById,
  getEvaluationBySubmission,
  listEvaluationsByAssignment,
  listMySubmittedEvaluations,
  listMyAssignedEvaluations,
  startEvaluation,
  updateCriterionScore,
  bulkUpdateCriterionScores,
  addFinding,
  updateFinding,
  removeFinding,
  updateFeedback,
  completeEvaluation,
  returnEvaluation,
  archiveEvaluation,
  publishEvaluation,
  unpublishEvaluation,
  getPublicationStatus,
  getStudentPublishedResults,
  getStudentPublishedResultsByAssignment,
  getStudentPublishedResultsByProject,
  getStudentPublishedResultBySubmission,
  listPublishedResultsByAssignment,
  getErrorMessage,
};
