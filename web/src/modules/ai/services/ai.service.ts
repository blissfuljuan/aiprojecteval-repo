import { api } from "@/common/lib/api";
import type { ApiResponse } from "@/modules/identity/types";
import type {
  ProjectProposalAIEvaluation,
  ProjectProposalAIEvaluationRequest,
} from "@/modules/ai/types";

async function runProjectProposalEvaluation(
  proposalId: number,
  request: ProjectProposalAIEvaluationRequest,
): Promise<ProjectProposalAIEvaluation> {
  const response = await api.post<ApiResponse<ProjectProposalAIEvaluation>>(
    `/api/project-proposals/${proposalId}/ai-evaluations`,
    request,
  );
  return response.data.data;
}

async function getLatestProjectProposalEvaluation(proposalId: number): Promise<ProjectProposalAIEvaluation | null> {
  try {
    const response = await api.get<ApiResponse<ProjectProposalAIEvaluation>>(
      `/api/project-proposals/${proposalId}/ai-evaluations/latest`,
    );
    return response.data.data;
  } catch (error) {
    if (isNotFound(error)) return null;
    throw error;
  }
}

async function getProjectProposalEvaluations(proposalId: number): Promise<ProjectProposalAIEvaluation[]> {
  const response = await api.get<ApiResponse<ProjectProposalAIEvaluation[]>>(
    `/api/project-proposals/${proposalId}/ai-evaluations`,
  );
  return response.data.data;
}

function isNotFound(error: unknown) {
  return typeof error === "object" && error !== null && "response" in error
    && (error as { response?: { status?: number } }).response?.status === 404;
}

export const aiService = {
  runProjectProposalEvaluation,
  getLatestProjectProposalEvaluation,
  getProjectProposalEvaluations,
};
