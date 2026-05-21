import { useCallback, useEffect, useMemo, useState } from "react";
import { ExternalLink, FileText, RefreshCw } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Separator } from "@/common/ui/shadcn/separator";
import type { GenericDocument } from "@/modules/document/types";
import { projectProposalService } from "@/modules/project-proposal/services/projectProposal.service";

type Props = {
  proposalId: number;
  proposalTitle: string;
  canManage: boolean;
  onDocumentChanged?: () => Promise<void> | void;
};

const validationStyles: Record<string, string> = {
  PENDING: "border-amber-200 bg-amber-50 text-amber-700",
  VALID: "border-emerald-200 bg-emerald-50 text-emerald-700",
  INVALID_URL: "border-rose-200 bg-rose-50 text-rose-700",
  INACCESSIBLE: "border-rose-200 bg-rose-50 text-rose-700",
  UNSUPPORTED_FILE_TYPE: "border-rose-200 bg-rose-50 text-rose-700",
  TOO_LARGE: "border-rose-200 bg-rose-50 text-rose-700",
  NOT_A_DOCUMENT: "border-rose-200 bg-rose-50 text-rose-700",
  FAILED: "border-red-200 bg-red-50 text-red-700",
};

const extractionStyles: Record<string, string> = {
  NOT_STARTED: "border-slate-200 bg-slate-50 text-slate-700",
  PROCESSING: "border-blue-200 bg-blue-50 text-blue-700",
  EXTRACTED: "border-emerald-200 bg-emerald-50 text-emerald-700",
  FAILED: "border-red-200 bg-red-50 text-red-700",
};

export function ProjectProposalDocumentSection({
  proposalId,
  proposalTitle,
  canManage,
  onDocumentChanged,
}: Props) {
  const [documents, setDocuments] = useState<GenericDocument[]>([]);
  const [documentUrl, setDocumentUrl] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const latestDocument = useMemo(() => documents[0] ?? null, [documents]);
  const currentVersion = latestDocument?.currentVersion ?? null;

  const refreshDocuments = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await projectProposalService.getProposalDocuments(proposalId);
      setDocuments(data);
    } catch (err) {
      setError(projectProposalService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }, [proposalId]);

  useEffect(() => {
    void refreshDocuments();
  }, [refreshDocuments]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!documentUrl.trim()) {
      setError("Document link is required.");
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
      setSuccessMessage(null);
      await projectProposalService.submitProposalDocumentLink(proposalId, {
        title: `${proposalTitle} Proposal Document`,
        documentUrl: documentUrl.trim(),
      });
      setDocumentUrl("");
      setSuccessMessage("Document link submitted.");
      await refreshDocuments();
      await onDocumentChanged?.();
    } catch (err) {
      setError(projectProposalService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <Card>
      <CardHeader className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <CardTitle className="flex items-center gap-2 text-lg">
          <FileText className="h-5 w-5 text-muted-foreground" aria-hidden="true" />
          Project Proposal Document
        </CardTitle>
        <Button type="button" variant="outline" size="sm" onClick={() => void refreshDocuments()} disabled={isLoading}>
          <RefreshCw className="h-4 w-4" aria-hidden="true" />
          Refresh
        </Button>
      </CardHeader>
      <CardContent className="space-y-5">
        {error && <p className="text-sm text-destructive">{error}</p>}
        {successMessage && <p className="text-sm text-emerald-700">{successMessage}</p>}

        {isLoading ? (
          <p className="text-sm text-muted-foreground">Loading document...</p>
        ) : latestDocument && currentVersion ? (
          <div className="space-y-4">
            <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
              <div className="space-y-1">
                <p className="text-sm font-medium">{latestDocument.title}</p>
                {currentVersion.fileName && <p className="text-sm text-muted-foreground">{currentVersion.fileName}</p>}
              </div>
              {currentVersion.originalUrl && (
                <Button asChild variant="outline" size="sm">
                  <a href={currentVersion.originalUrl} target="_blank" rel="noreferrer">
                    <ExternalLink className="h-4 w-4" aria-hidden="true" />
                    Open
                  </a>
                </Button>
              )}
            </div>

            <div className="grid gap-3 md:grid-cols-2 lg:grid-cols-3">
              <DocumentMeta label="Source Type" value={formatLabel(currentVersion.sourceType)} />
              <DocumentMeta
                label="Validation"
                value={<StatusBadge value={currentVersion.validationStatus} styles={validationStyles} />}
              />
              <DocumentMeta
                label="Extraction"
                value={<StatusBadge value={currentVersion.extractionStatus} styles={extractionStyles} />}
              />
              <DocumentMeta label="Word Count" value={currentVersion.wordCount?.toLocaleString() ?? "Not available"} />
              <DocumentMeta label="Latest Version" value={`Version ${currentVersion.versionNumber}`} />
              <DocumentMeta label="Provider" value={formatLabel(currentVersion.provider)} />
            </div>

            {(currentVersion.validationMessage || currentVersion.extractionErrorMessage) && (
              <div className="space-y-1 rounded-md border border-amber-200 bg-amber-50 p-4 text-sm text-amber-900">
                {currentVersion.validationMessage && <p>{currentVersion.validationMessage}</p>}
                {currentVersion.extractionErrorMessage && <p>{currentVersion.extractionErrorMessage}</p>}
              </div>
            )}
          </div>
        ) : (
          <p className="text-sm text-muted-foreground">No proposal document has been submitted yet.</p>
        )}

        {canManage && (
          <>
            <Separator />
            <form className="grid gap-3 md:grid-cols-[1fr_auto] md:items-end" onSubmit={(e) => void handleSubmit(e)}>
              <div className="grid gap-2">
                <Label htmlFor="proposal-document-url">Google Drive or Docs Link</Label>
                <Input
                  id="proposal-document-url"
                  type="url"
                  placeholder="https://docs.google.com/document/d/..."
                  value={documentUrl}
                  onChange={(e) => setDocumentUrl(e.target.value)}
                  required
                />
              </div>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Submitting..." : latestDocument ? "Update Link" : "Submit Link"}
              </Button>
            </form>
          </>
        )}
      </CardContent>
    </Card>
  );
}

function DocumentMeta({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div className="rounded-md border bg-muted/30 p-3">
      <p className="text-xs font-medium uppercase tracking-normal text-muted-foreground">{label}</p>
      <div className="mt-1 text-sm font-medium">{value}</div>
    </div>
  );
}

function StatusBadge({ value, styles }: { value: string; styles: Record<string, string> }) {
  return (
    <Badge variant="outline" className={styles[value] ?? "border-slate-200 bg-slate-50 text-slate-700"}>
      {formatLabel(value)}
    </Badge>
  );
}

function formatLabel(value: string | null | undefined) {
  if (!value) return "Not available";
  return value
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}
