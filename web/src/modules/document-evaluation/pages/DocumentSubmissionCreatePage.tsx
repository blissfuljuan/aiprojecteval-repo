import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router";
import { ArrowLeft, FilePlus, RefreshCw } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Label } from "@/common/ui/shadcn/label";
import { Select } from "@/common/ui/shadcn/select";
import { Textarea } from "@/common/ui/shadcn/textarea";
import { documentEvaluationPaths } from "@/modules/document-evaluation/constants";
import { documentEvaluationService } from "@/modules/document-evaluation/services/documentEvaluation.service";
import type { MyAssignedDocumentRequirement } from "@/modules/document-evaluation/types";
import { submissionService } from "@/modules/submission/services/submission.service";

function optionLabel(requirement: MyAssignedDocumentRequirement) {
  const context = requirement.projectTitle ?? requirement.courseClassName ?? "Assigned context";
  return `${requirement.requirementName} - ${requirement.requirementSetName} (${context})`;
}

export function DocumentSubmissionCreatePage() {
  const navigate = useNavigate();
  const [assignedRequirements, setAssignedRequirements] = useState<MyAssignedDocumentRequirement[]>([]);
  const [selectedKey, setSelectedKey] = useState("");
  const [notes, setNotes] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const selectedRequirement = useMemo(
    () =>
      assignedRequirements.find(
        (item) => `${item.assignmentId}:${item.documentRequirementId}` === selectedKey,
      ) ?? null,
    [assignedRequirements, selectedKey],
  );

  async function loadRequirements() {
    try {
      setIsLoading(true);
      setError(null);
      setAssignedRequirements(await documentEvaluationService.listMyAssignedDocumentRequirements());
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadRequirements();
  }, []);

  async function createDraft() {
    if (!selectedRequirement) {
      setError("Select an assigned document requirement before creating a draft.");
      return;
    }
    if (selectedRequirement.existingDraftSubmissionId) {
      navigate(documentEvaluationPaths.submissionDetails(selectedRequirement.existingDraftSubmissionId));
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
      const draft = await submissionService.createSubmissionDraft({
        assignmentId: selectedRequirement.assignmentId,
        requirementId: selectedRequirement.documentRequirementId,
        submissionTitle: selectedRequirement.requirementName,
        submissionNotes: notes.trim() || undefined,
      });
      navigate(documentEvaluationPaths.submissionDetails(draft.id));
    } catch (err) {
      setError(submissionService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">New Document Submission</h1>
          <p className="text-sm text-muted-foreground">
            Create a draft for one assigned document requirement, then upload the required file.
          </p>
        </div>
        <Button variant="outline" asChild>
          <Link to={documentEvaluationPaths.submissions}>
            <ArrowLeft className="h-4 w-4" aria-hidden="true" />
            Back to Submissions
          </Link>
        </Button>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Create Draft</CardTitle>
          <CardDescription>Only configured requirements assigned to your project context are listed.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading assigned requirements...</p>
          ) : assignedRequirements.length === 0 ? (
            <div className="rounded-md border bg-muted/30 p-4 text-sm text-muted-foreground">
              No active document requirements are available for draft creation.
            </div>
          ) : (
            <>
              <div className="grid gap-2">
                <Label htmlFor="requirement">Assigned requirement</Label>
                <Select id="requirement" value={selectedKey} onChange={(event) => setSelectedKey(event.target.value)}>
                  <option value="">Select a requirement</option>
                  {assignedRequirements.map((requirement) => (
                    <option
                      key={`${requirement.assignmentId}:${requirement.documentRequirementId}`}
                      value={`${requirement.assignmentId}:${requirement.documentRequirementId}`}
                    >
                      {optionLabel(requirement)}
                    </option>
                  ))}
                </Select>
              </div>

              {selectedRequirement && (
                <div className="rounded-md border bg-muted/30 p-4 text-sm">
                  <div className="flex flex-col justify-between gap-2 sm:flex-row sm:items-start">
                    <div>
                      <p className="font-medium">{selectedRequirement.requirementName}</p>
                      <p className="text-muted-foreground">{selectedRequirement.requirementSetName}</p>
                    </div>
                    <span className="text-xs text-muted-foreground">
                      {selectedRequirement.required ? "Required" : "Optional"}
                    </span>
                  </div>
                  {selectedRequirement.requirementDescription && (
                    <p className="mt-2 text-muted-foreground">{selectedRequirement.requirementDescription}</p>
                  )}
                  {selectedRequirement.allowedFileTypes?.length ? (
                    <p className="mt-2 text-xs text-muted-foreground">
                      Allowed file types: {selectedRequirement.allowedFileTypes.join(", ")}
                    </p>
                  ) : null}
                  {selectedRequirement.existingDraftSubmissionId && (
                    <p className="mt-2 text-xs text-muted-foreground">
                      A draft already exists. Creating will continue draft #{selectedRequirement.existingDraftSubmissionId}.
                    </p>
                  )}
                </div>
              )}

              <div className="grid gap-2">
                <Label htmlFor="notes">Notes</Label>
                <Textarea
                  id="notes"
                  value={notes}
                  onChange={(event) => setNotes(event.target.value)}
                  placeholder="Optional remarks for your instructor"
                />
              </div>
            </>
          )}

          <div className="flex flex-wrap justify-between gap-2">
            <Button variant="outline" onClick={() => void loadRequirements()}>
              <RefreshCw className="h-4 w-4" aria-hidden="true" />
              Refresh
            </Button>
            <Button disabled={isLoading || isSubmitting || assignedRequirements.length === 0} onClick={() => void createDraft()}>
              <FilePlus className="h-4 w-4" aria-hidden="true" />
              {isSubmitting ? "Creating..." : "Create Draft"}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
