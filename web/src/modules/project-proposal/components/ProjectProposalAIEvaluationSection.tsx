import { useCallback, useEffect, useMemo, useState } from "react";
import { Bot, CheckCircle2, Play, RotateCw } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Progress } from "@/common/ui/shadcn/progress";
import { Separator } from "@/common/ui/shadcn/separator";
import { DocumentStatusBadge } from "@/modules/document/components/DocumentStatusBadge";
import { documentService } from "@/modules/document/services/documentService";
import type { GenericDocument } from "@/modules/document/types";
import { aiService } from "@/modules/ai/services/ai.service";
import type { ProjectProposalAIEvaluation } from "@/modules/ai/types";
import { projectProposalService } from "@/modules/project-proposal/services/projectProposal.service";

type Props = {
  proposalId: number;
  canEvaluate: boolean;
};

export function ProjectProposalAIEvaluationSection({ proposalId, canEvaluate }: Props) {
  const [documents, setDocuments] = useState<GenericDocument[]>([]);
  const [latestEvaluation, setLatestEvaluation] = useState<ProjectProposalAIEvaluation | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isValidating, setIsValidating] = useState(false);
  const [isExtracting, setIsExtracting] = useState(false);
  const [isEvaluating, setIsEvaluating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const latestDocument = useMemo(() => documents[0] ?? null, [documents]);
  const currentVersion = latestDocument?.currentVersion ?? null;

  const refresh = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const [documentData, evaluationData] = await Promise.all([
        projectProposalService.getProposalDocuments(proposalId),
        aiService.getLatestProjectProposalEvaluation(proposalId),
      ]);
      setDocuments(documentData);
      setLatestEvaluation(evaluationData);
    } catch (err) {
      setError(projectProposalService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }, [proposalId]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  async function handleValidate() {
    if (!latestDocument) return;
    try {
      setIsValidating(true);
      setError(null);
      await documentService.validateCurrentVersion(latestDocument.id);
      await refresh();
    } catch (err) {
      setError(projectProposalService.getErrorMessage(err));
    } finally {
      setIsValidating(false);
    }
  }

  async function handleExtractText() {
    if (!latestDocument || !currentVersion) return;
    try {
      setIsExtracting(true);
      setError(null);
      await documentService.extractVersionText(latestDocument.id, currentVersion.id);
      await refresh();
    } catch (err) {
      setError(projectProposalService.getErrorMessage(err));
    } finally {
      setIsExtracting(false);
    }
  }

  async function handleRunEvaluation() {
    if (!currentVersion) return;
    try {
      setIsEvaluating(true);
      setError(null);
      const result = await aiService.runProjectProposalEvaluation(proposalId, {
        documentVersionId: currentVersion.id,
        provider: "MOCK",
      });
      setLatestEvaluation(result);
      await refresh();
    } catch (err) {
      setError(projectProposalService.getErrorMessage(err));
    } finally {
      setIsEvaluating(false);
    }
  }

  const canExtract = canEvaluate && currentVersion?.validationStatus === "VALID";
  const canRunEvaluation = canEvaluate && currentVersion?.extractionStatus === "EXTRACTED";
  const scoreValue = latestEvaluation?.overallScore && latestEvaluation.maxScore
    ? Math.round((latestEvaluation.overallScore / latestEvaluation.maxScore) * 100)
    : 0;

  return (
    <Card>
      <CardHeader className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <CardTitle className="flex items-center gap-2 text-lg">
          <Bot className="h-5 w-5 text-muted-foreground" aria-hidden="true" />
          Proposal AI Evaluation
        </CardTitle>
        <Button type="button" variant="outline" size="sm" onClick={() => void refresh()} disabled={isLoading}>
          <RotateCw className="h-4 w-4" aria-hidden="true" />
          Refresh
        </Button>
      </CardHeader>
      <CardContent className="space-y-5">
        {error && <p className="text-sm text-destructive">{error}</p>}
        {isLoading ? (
          <p className="text-sm text-muted-foreground">Loading AI evaluation workflow...</p>
        ) : latestDocument && currentVersion ? (
          <>
            <div className="grid gap-3 md:grid-cols-2 lg:grid-cols-4">
              <WorkflowMeta label="Linked Document" value={latestDocument.title} />
              <WorkflowMeta label="Validation" value={<DocumentStatusBadge status={currentVersion.validationStatus} />} />
              <WorkflowMeta label="Extraction" value={<DocumentStatusBadge status={currentVersion.extractionStatus} />} />
              <WorkflowMeta label="AI Status" value={<DocumentStatusBadge status={latestEvaluation?.status} />} />
            </div>

            {canEvaluate && (
              <div className="flex flex-wrap gap-3">
                <Button type="button" variant="outline" onClick={() => void handleValidate()} disabled={isValidating}>
                  <CheckCircle2 className="h-4 w-4" aria-hidden="true" />
                  {isValidating ? "Validating..." : "Validate"}
                </Button>
                <Button type="button" variant="outline" onClick={() => void handleExtractText()} disabled={!canExtract || isExtracting}>
                  <RotateCw className="h-4 w-4" aria-hidden="true" />
                  {isExtracting ? "Extracting..." : "Extract Text"}
                </Button>
                <Button type="button" onClick={() => void handleRunEvaluation()} disabled={!canRunEvaluation || isEvaluating}>
                  <Play className="h-4 w-4" aria-hidden="true" />
                  {isEvaluating ? "Running..." : "Run AI Evaluation"}
                </Button>
              </div>
            )}

            {latestEvaluation ? (
              <div className="space-y-4">
                <Separator />
                <div className="grid gap-4 lg:grid-cols-[0.8fr_1.2fr]">
                  <div className="space-y-3">
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-muted-foreground">Overall Score</span>
                      <span className="font-medium">
                        {latestEvaluation.overallScore ?? 0}/{latestEvaluation.maxScore ?? 0}
                      </span>
                    </div>
                    <Progress value={scoreValue} />
                    <div className="flex flex-wrap gap-2">
                      <Badge variant="outline">{formatLabel(latestEvaluation.recommendation)}</Badge>
                      <Badge variant="outline">{formatLabel(latestEvaluation.readinessLevel)}</Badge>
                    </div>
                  </div>
                  <div className="space-y-2">
                    <p className="text-sm font-medium">Summary</p>
                    <p className="text-sm leading-6 text-muted-foreground">
                      {latestEvaluation.summary ?? "No summary returned."}
                    </p>
                  </div>
                </div>
                <EvaluationList title="Suggested Revisions" items={latestEvaluation.suggestedRevisions} />
                <EvaluationList title="Risks" items={latestEvaluation.riskNotes} />
              </div>
            ) : (
              <p className="text-sm text-muted-foreground">No AI evaluation has been run for this proposal yet.</p>
            )}
          </>
        ) : (
          <p className="text-sm text-muted-foreground">Submit a proposal document link before running AI evaluation.</p>
        )}
      </CardContent>
    </Card>
  );
}

function WorkflowMeta({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div className="rounded-md border bg-muted/30 p-3">
      <p className="text-xs font-medium uppercase tracking-normal text-muted-foreground">{label}</p>
      <div className="mt-1 text-sm font-medium">{value}</div>
    </div>
  );
}

function EvaluationList({ title, items }: { title: string; items: string[] }) {
  if (items.length === 0) return null;

  return (
    <div className="space-y-2">
      <p className="text-sm font-medium">{title}</p>
      <ul className="list-disc space-y-1 pl-5 text-sm text-muted-foreground">
        {items.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    </div>
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
