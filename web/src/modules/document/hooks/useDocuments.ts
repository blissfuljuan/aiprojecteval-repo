import { useCallback, useEffect, useState } from "react";
import { documentService } from "@/modules/document/services/documentService";
import type { GenericDocumentSummary } from "@/modules/document/types";

export function useDocuments() {
  const [documents, setDocuments] = useState<GenericDocumentSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      setDocuments(await documentService.listDocuments());
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to load documents.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  return {
    documents,
    isLoading,
    error,
    refresh,
    totalDocuments: documents.length,
    pendingAnalysis: documents.filter((document) => document.extractionStatus === "NOT_STARTED").length,
    analyzed: documents.filter((document) => document.extractionStatus === "EXTRACTED").length,
    needsReview: documents.filter((document) => document.validationStatus !== "VALID").length,
  };
}
