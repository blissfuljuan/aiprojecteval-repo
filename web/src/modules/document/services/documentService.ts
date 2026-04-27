import { analysisResult, documentHistory, documents } from "@/modules/document/data/documentMockData";

export const documentService = {
  listDocuments: () => documents,
  getDocumentById: (documentId: string | undefined) =>
    documents.find((document) => document.id === documentId) ?? documents[0],
  getAnalysisResult: () => analysisResult,
  getDocumentHistory: () => documentHistory,
};
