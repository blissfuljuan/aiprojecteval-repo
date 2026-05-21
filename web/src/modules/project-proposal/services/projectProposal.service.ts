import { isAxiosError } from "axios";
import { api } from "@/common/lib/api";
import type { GenericDocument } from "@/modules/document/types";
import type { ApiResponse } from "@/modules/identity/types";
import type {
  AdviserDecisionRequest,
  InstructorDecisionRequest,
  ProjectProposal,
  ProjectProposalDocumentLinkRequest,
  ProposalCreateRequest,
  ProposalUpdateRequest,
} from "@/modules/project-proposal/types";

async function createProposal(request: ProposalCreateRequest): Promise<ProjectProposal> {
  const response = await api.post<ApiResponse<ProjectProposal>>("/api/project-proposals", request);
  return response.data.data;
}

async function getMyProposals(): Promise<ProjectProposal[]> {
  const response = await api.get<ApiResponse<ProjectProposal[]>>("/api/project-proposals/my");
  return response.data.data;
}

async function getAllProposals(): Promise<ProjectProposal[]> {
  const response = await api.get<ApiResponse<ProjectProposal[]>>("/api/project-proposals");
  return response.data.data;
}

async function getProposalById(id: number): Promise<ProjectProposal> {
  const response = await api.get<ApiResponse<ProjectProposal>>(`/api/project-proposals/${id}`);
  return response.data.data;
}

async function updateProposal(id: number, request: ProposalUpdateRequest): Promise<ProjectProposal> {
  const response = await api.put<ApiResponse<ProjectProposal>>(`/api/project-proposals/${id}`, request);
  return response.data.data;
}

async function deleteProposal(id: number): Promise<void> {
  await api.delete(`/api/project-proposals/${id}`);
}

async function adviserDecision(id: number, request: AdviserDecisionRequest): Promise<ProjectProposal> {
  const response = await api.patch<ApiResponse<ProjectProposal>>(
    `/api/project-proposals/${id}/adviser-decision`,
    request,
  );
  return response.data.data;
}

async function instructorDecision(id: number, request: InstructorDecisionRequest): Promise<ProjectProposal> {
  const response = await api.patch<ApiResponse<ProjectProposal>>(
    `/api/project-proposals/${id}/instructor-decision`,
    request,
  );
  return response.data.data;
}

async function getProposalDocuments(id: number): Promise<GenericDocument[]> {
  const response = await api.get<ApiResponse<GenericDocument[]>>(`/api/project-proposals/${id}/documents`);
  return response.data.data;
}

async function submitProposalDocumentLink(
  id: number,
  request: ProjectProposalDocumentLinkRequest,
): Promise<GenericDocument> {
  const response = await api.post<ApiResponse<GenericDocument>>(`/api/project-proposals/${id}/documents/link`, request);
  return response.data.data;
}

function getErrorMessage(error: unknown): string {
  if (isAxiosError<ApiResponse<unknown>>(error)) {
    const data = error.response?.data;
    return data?.errors?.[0] ?? data?.message ?? "Request failed. Please try again.";
  }
  if (error instanceof Error) return error.message;
  return "Request failed. Please try again.";
}

export const projectProposalService = {
  createProposal,
  getMyProposals,
  getAllProposals,
  getProposalById,
  updateProposal,
  deleteProposal,
  adviserDecision,
  instructorDecision,
  getProposalDocuments,
  submitProposalDocumentLink,
  getErrorMessage,
};
