import { analysisResult, documentHistory, documents } from "@/modules/document/data/documentMockData";
import { api } from "@/common/lib/api";
import type { ApiResponse } from "@/modules/identity/types";
import type {
  DocumentLinkSubmitRequest,
  GenericDocument,
  GenericDocumentContextType,
  GenericDocumentSummary,
  GenericDocumentVersion,
} from "@/modules/document/types";

async function listDocuments(): Promise<GenericDocumentSummary[]> {
  const response = await api.get<ApiResponse<GenericDocumentSummary[]>>("/api/documents");
  return response.data.data;
}

async function submitExternalLink(request: DocumentLinkSubmitRequest): Promise<GenericDocument> {
  const response = await api.post<ApiResponse<GenericDocument>>("/api/documents/link", request);
  return response.data.data;
}

async function getDocumentByBackendId(documentId: number): Promise<GenericDocument> {
  const response = await api.get<ApiResponse<GenericDocument>>(`/api/documents/${documentId}`);
  return response.data.data;
}

async function getDocumentsByContext(
  contextType: GenericDocumentContextType,
  contextId: number,
): Promise<GenericDocumentSummary[]> {
  const response = await api.get<ApiResponse<GenericDocumentSummary[]>>(
    `/api/documents/context/${contextType}/${contextId}`,
  );
  return response.data.data;
}

async function getDocumentVersions(documentId: number): Promise<GenericDocumentVersion[]> {
  const response = await api.get<ApiResponse<GenericDocumentVersion[]>>(`/api/documents/${documentId}/versions`);
  return response.data.data;
}

async function validateCurrentVersion(documentId: number): Promise<GenericDocument> {
  const response = await api.post<ApiResponse<GenericDocument>>(`/api/documents/${documentId}/validate-current`);
  return response.data.data;
}

async function extractVersionText(documentId: number, versionId: number): Promise<GenericDocumentVersion> {
  const response = await api.post<ApiResponse<GenericDocumentVersion>>(
    `/api/documents/${documentId}/versions/${versionId}/extract`,
  );
  return response.data.data;
}

export const documentService = {
  listDocuments,
  getDocumentById: (documentId: string | undefined) =>
    documents.find((document) => document.id === documentId) ?? documents[0],
  getAnalysisResult: () => analysisResult,
  getDocumentHistory: () => documentHistory,
  submitExternalLink,
  getDocumentByBackendId,
  getDocumentsByContext,
  getDocumentVersions,
  validateCurrentVersion,
  extractVersionText,
};
