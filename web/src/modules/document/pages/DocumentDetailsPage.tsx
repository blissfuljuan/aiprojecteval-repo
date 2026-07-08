import { useCallback, useEffect, useState } from "react";
import { useParams } from "react-router";
import { ExternalLink, RotateCw } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { DocumentAnalysisSummary } from "@/modules/document/components/DocumentAnalysisSummary";
import { DocumentMetadataCard } from "@/modules/document/components/DocumentMetadataCard";
import { DocumentTabs } from "@/modules/document/components/DocumentTabs";
import { documentService } from "@/modules/document/services/documentService";
import type { GenericDocument, GenericDocumentVersion } from "@/modules/document/types";

export function DocumentDetailsPage() {
  const { documentId } = useParams();
  const numericDocumentId = Number(documentId);
  const [document, setDocument] = useState<GenericDocument | null>(null);
  const [versions, setVersions] = useState<GenericDocumentVersion[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isExtracting, setIsExtracting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const analysis = documentService.getAnalysisResult();

  const refresh = useCallback(async () => {
    if (!Number.isFinite(numericDocumentId)) {
      setError("Document not found.");
      setIsLoading(false);
      return;
    }

    try {
      setIsLoading(true);
      setError(null);
      const [documentData, versionData] = await Promise.all([
        documentService.getDocumentByBackendId(numericDocumentId),
        documentService.getDocumentVersions(numericDocumentId),
      ]);
      setDocument(documentData);
      setVersions(versionData);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to load document.");
    } finally {
      setIsLoading(false);
    }
  }, [numericDocumentId]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  async function handleExtractText() {
    if (!document?.currentVersionId) return;
    try {
      setIsExtracting(true);
      setError(null);
      await documentService.extractVersionText(document.id, document.currentVersionId);
      await refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to extract document text.");
    } finally {
      setIsExtracting(false);
    }
  }

  if (isLoading) {
    return <p className="text-sm text-muted-foreground">Loading document...</p>;
  }

  if (!document) {
    return <p className="text-sm text-destructive">{error ?? "Document not found."}</p>;
  }

  const currentVersion = document.currentVersion;

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Document Details</h1>
          <p className="text-sm text-muted-foreground">Review uploaded document metadata.</p>
        </div>
        <div className="flex flex-wrap gap-2">
          {currentVersion?.originalUrl && (
            <Button asChild variant="outline">
              <a href={currentVersion.originalUrl} target="_blank" rel="noreferrer">
                <ExternalLink className="h-4 w-4" aria-hidden="true" />
                Open
              </a>
            </Button>
          )}
          <Button type="button" onClick={() => void handleExtractText()} disabled={!currentVersion || isExtracting}>
            <RotateCw className="h-4 w-4" aria-hidden="true" />
            {isExtracting ? "Extracting..." : "Extract Text"}
          </Button>
        </div>
      </div>

      {error && <p className="text-sm text-destructive">{error}</p>}

      <div className="grid gap-4 xl:grid-cols-[1.35fr_1fr]">
        <DocumentMetadataCard document={document} />
        <DocumentAnalysisSummary document={document} analysis={analysis} />
      </div>

      <DocumentTabs document={document} versions={versions} />
    </div>
  );
}
