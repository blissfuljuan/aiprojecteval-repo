import { documentService } from "@/modules/document/services/documentService";

export function useDocuments() {
  const documents = documentService.listDocuments();

  return {
    documents,
    totalDocuments: documents.length,
    pendingAnalysis: documents.filter((document) => document.status === "PENDING_ANALYSIS").length,
    analyzed: documents.filter((document) => document.status === "ANALYZED").length,
    needsReview: documents.filter((document) => document.status === "NEEDS_REVIEW").length,
  };
}
