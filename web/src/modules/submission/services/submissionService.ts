import { isAxiosError } from "axios";
import { api } from "@/common/lib/api";
import type { ApiResponse } from "@/modules/identity/types";

type QueryParams = Record<string, string | number | boolean | null | undefined>;
type RequestBody = Record<string, unknown>;

function unwrap<T>(response: { data: ApiResponse<T> }): T {
  return response.data.data;
}

async function createSubmissionDraft(request: RequestBody): Promise<unknown> {
  return unwrap(await api.post<ApiResponse<unknown>>("/api/submissions/draft", request));
}

async function createSubmission(request: RequestBody): Promise<unknown> {
  return unwrap(await api.post<ApiResponse<unknown>>("/api/submissions", request));
}

async function updateSubmissionDraft(submissionId: number, request: RequestBody): Promise<unknown> {
  return unwrap(await api.put<ApiResponse<unknown>>(`/api/submissions/${submissionId}/draft`, request));
}

async function submitDraft(submissionId: number): Promise<unknown> {
  return unwrap(await api.patch<ApiResponse<unknown>>(`/api/submissions/${submissionId}/submit`));
}

async function listMySubmissions(params?: QueryParams): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>("/api/submissions/my", { params }));
}

async function getSubmissionById(submissionId: number): Promise<unknown> {
  return unwrap(await api.get<ApiResponse<unknown>>(`/api/submissions/${submissionId}`));
}

async function listSubmissionsByAssignment(assignmentId: number, params?: QueryParams): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>("/api/submissions", { params: { ...params, assignmentId } }));
}

async function listSubmissionsByProject(projectId: number, params?: QueryParams): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>(`/api/submissions/by-project/${projectId}`, { params }));
}

async function listSubmissionsByClass(courseClassId: number, params?: QueryParams): Promise<unknown[]> {
  return unwrap(await api.get<ApiResponse<unknown[]>>(`/api/submissions/by-class/${courseClassId}`, { params }));
}

async function archiveSubmission(submissionId: number): Promise<unknown> {
  return unwrap(await api.patch<ApiResponse<unknown>>(`/api/submissions/${submissionId}/archive`));
}

async function addSubmissionFileMetadata(submissionId: number, request: RequestBody): Promise<unknown> {
  return unwrap(await api.post<ApiResponse<unknown>>(`/api/submissions/${submissionId}/files`, request));
}

async function uploadSubmissionFile(submissionId: number, file: File, notes?: string): Promise<unknown> {
  const formData = new FormData();
  formData.append("file", file);
  if (notes) formData.append("notes", notes);

  return unwrap(await api.post<ApiResponse<unknown>>(`/api/submissions/${submissionId}/files/upload`, formData));
}

async function uploadMultipleSubmissionFiles(submissionId: number, files: File[]): Promise<unknown> {
  const formData = new FormData();
  files.forEach((file) => formData.append("files", file));

  return unwrap(
    await api.post<ApiResponse<unknown>>(`/api/submissions/${submissionId}/files/upload-multiple`, formData),
  );
}

async function listSubmissionFiles(submissionId: number, includeInactive = false): Promise<unknown[]> {
  return unwrap(
    await api.get<ApiResponse<unknown[]>>(`/api/submissions/${submissionId}/files`, {
      params: { includeInactive },
    }),
  );
}

async function downloadSubmissionFile(fileId: number): Promise<Blob> {
  const response = await api.get<Blob>(`/api/submission-files/${fileId}/download`, { responseType: "blob" });
  return response.data;
}

async function viewSubmissionFile(fileId: number): Promise<Blob> {
  const response = await api.get<Blob>(`/api/submission-files/${fileId}/view`, { responseType: "blob" });
  return response.data;
}

async function deleteSubmissionFile(fileId: number): Promise<unknown> {
  return unwrap(await api.delete<ApiResponse<unknown>>(`/api/submission-files/${fileId}`));
}

async function replaceSubmissionFile(fileId: number, file: File): Promise<unknown> {
  const formData = new FormData();
  formData.append("file", file);

  return unwrap(await api.post<ApiResponse<unknown>>(`/api/submission-files/${fileId}/replace`, formData));
}

async function uploadReplacementToSubmission(
  submissionId: number,
  replaceFileId: number,
  file: File,
  notes?: string,
): Promise<unknown> {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("replaceFileId", String(replaceFileId));
  if (notes) formData.append("notes", notes);

  return unwrap(await api.post<ApiResponse<unknown>>(`/api/submissions/${submissionId}/files/upload`, formData));
}

function getErrorMessage(error: unknown): string {
  if (isAxiosError<ApiResponse<unknown>>(error)) {
    const data = error.response?.data;
    return data?.errors?.[0] ?? data?.message ?? "Request failed. Please try again.";
  }
  if (error instanceof Error) return error.message;
  return "Request failed. Please try again.";
}

export const submissionService = {
  createSubmissionDraft,
  createSubmission,
  updateSubmissionDraft,
  submitDraft,
  listMySubmissions,
  getSubmissionById,
  listSubmissionsByAssignment,
  listSubmissionsByProject,
  listSubmissionsByClass,
  archiveSubmission,
  addSubmissionFileMetadata,
  uploadSubmissionFile,
  uploadMultipleSubmissionFiles,
  listSubmissionFiles,
  downloadSubmissionFile,
  viewSubmissionFile,
  deleteSubmissionFile,
  replaceSubmissionFile,
  uploadReplacementToSubmission,
  getErrorMessage,
};
