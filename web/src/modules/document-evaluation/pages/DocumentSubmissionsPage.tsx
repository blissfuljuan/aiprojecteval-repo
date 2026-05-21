import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import { Eye, FileUp, Plus, RefreshCw, Send } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/common/ui/shadcn/table";
import { documentEvaluationPaths } from "@/modules/document-evaluation/constants";
import { documentEvaluationService } from "@/modules/document-evaluation/services/documentEvaluation.service";
import type { MyAssignedDocumentRequirement } from "@/modules/document-evaluation/types";
import { submissionService } from "@/modules/submission/services/submission.service";
import type { DocumentSubmissionStatus, DocumentSubmissionSummary } from "@/modules/submission/types";

function formatDateTime(value: string | null | undefined) {
  return value ? new Date(value).toLocaleString() : "Not submitted";
}

function statusLabel(status: DocumentSubmissionStatus | null | undefined) {
  if (!status) return "Not started";
  return status.replace(/_/g, " ").toLowerCase().replace(/\b\w/g, (letter: string) => letter.toUpperCase());
}

function StatusBadge({ status }: { status: DocumentSubmissionStatus | null | undefined }) {
  const className =
    status === "DRAFT"
      ? "border-sky-200 bg-sky-50 text-sky-700"
      : status === "SUBMITTED" || status === "RESUBMITTED"
        ? "border-amber-200 bg-amber-50 text-amber-700"
        : status === "ACCEPTED"
          ? "border-emerald-200 bg-emerald-50 text-emerald-700"
          : status === "RETURNED"
            ? "border-rose-200 bg-rose-50 text-rose-700"
            : "border-muted bg-muted/40 text-muted-foreground";

  return (
    <Badge variant="outline" className={className}>
      {statusLabel(status)}
    </Badge>
  );
}

function contextLabel(item: MyAssignedDocumentRequirement | DocumentSubmissionSummary) {
  if ("projectTitle" in item && item.projectTitle) return item.projectTitle;
  if ("courseClassName" in item && item.courseClassName) return item.courseClassName;
  if (item.projectId) return `Project #${item.projectId}`;
  if (item.courseClassId) return `Class #${item.courseClassId}`;
  return "No project/class context";
}

export function DocumentSubmissionsPage() {
  const [submissions, setSubmissions] = useState<DocumentSubmissionSummary[]>([]);
  const [assignedRequirements, setAssignedRequirements] = useState<MyAssignedDocumentRequirement[]>([]);
  const [fileCounts, setFileCounts] = useState<Record<number, number>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const requirementByKey = useMemo(() => {
    return new Map(
      assignedRequirements.map((item) => [`${item.assignmentId}:${item.documentRequirementId}`, item] as const),
    );
  }, [assignedRequirements]);

  const counts = useMemo(() => {
    return {
      drafts: submissions.filter((submission) => submission.status === "DRAFT").length,
      submitted: submissions.filter((submission) => ["SUBMITTED", "RESUBMITTED"].includes(submission.status)).length,
      returned: submissions.filter((submission) => submission.status === "RETURNED").length,
      accepted: submissions.filter((submission) => submission.status === "ACCEPTED").length,
    };
  }, [submissions]);

  async function loadPage() {
    try {
      setIsLoading(true);
      setError(null);
      const [submissionItems, requirementItems] = await Promise.all([
        submissionService.listMySubmissions(),
        documentEvaluationService.listMyAssignedDocumentRequirements(),
      ]);
      setSubmissions(submissionItems);
      setAssignedRequirements(requirementItems);

      const countsBySubmission = Object.fromEntries(
        await Promise.all(
          submissionItems.map(async (submission) => {
            const files = await submissionService.listSubmissionFiles(submission.id);
            return [submission.id, files.filter((file) => file.fileStatus === "UPLOADED").length] as const;
          }),
        ),
      );
      setFileCounts(countsBySubmission);
    } catch (err) {
      setError(submissionService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadPage();
  }, []);

  async function submitDraft(submission: DocumentSubmissionSummary) {
    const uploadedCount = fileCounts[submission.id] ?? 0;
    if (uploadedCount < 1) {
      setError("Upload at least one file before submitting this draft.");
      return;
    }
    if (!confirm(`Submit draft #${submission.id}? Editing will be disabled after submission.`)) return;

    try {
      setError(null);
      await submissionService.submitDraft(submission.id);
      setNotice("Draft submitted.");
      await loadPage();
    } catch (err) {
      setError(submissionService.getErrorMessage(err));
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Document Submissions</h1>
          <p className="text-sm text-muted-foreground">
            Upload and submit the documents required by your instructor for assigned requirement sets.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button variant="outline" onClick={() => void loadPage()}>
            <RefreshCw className="h-4 w-4" aria-hidden="true" />
            Refresh
          </Button>
          <Button asChild>
            <Link to={documentEvaluationPaths.submissionCreate}>
              <Plus className="h-4 w-4" aria-hidden="true" />
              New Submission
            </Link>
          </Button>
        </div>
      </div>

      {notice && (
        <Alert>
          <AlertDescription>{notice}</AlertDescription>
        </Alert>
      )}
      {error && (
        <Alert variant="destructive">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="grid gap-4 md:grid-cols-4">
        <SummaryCard label="Drafts" value={counts.drafts} />
        <SummaryCard label="Submitted" value={counts.submitted} />
        <SummaryCard label="Needs Revision" value={counts.returned} />
        <SummaryCard label="Completed" value={counts.accepted} />
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Available Requirements</CardTitle>
          <CardDescription>Configured document requirements currently assigned to your project context.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading assigned requirements...</p>
          ) : assignedRequirements.length === 0 ? (
            <p className="text-sm text-muted-foreground">No active document requirements are assigned yet.</p>
          ) : (
            <div className="grid gap-3 md:grid-cols-2">
              {assignedRequirements.map((requirement) => (
                <div key={`${requirement.assignmentId}-${requirement.documentRequirementId}`} className="rounded-md border p-4">
                  <div className="flex flex-col justify-between gap-2 sm:flex-row sm:items-start">
                    <div className="min-w-0">
                      <p className="font-medium">{requirement.requirementName}</p>
                      <p className="text-xs text-muted-foreground">{requirement.requirementSetName}</p>
                    </div>
                    <StatusBadge status={requirement.latestSubmissionStatus} />
                  </div>
                  {requirement.requirementDescription && (
                    <p className="mt-2 text-sm text-muted-foreground">{requirement.requirementDescription}</p>
                  )}
                  <div className="mt-3 flex flex-wrap items-center justify-between gap-2 text-xs text-muted-foreground">
                    <span>{contextLabel(requirement)}</span>
                    {requirement.existingDraftSubmissionId ? (
                      <Button size="sm" variant="outline" asChild>
                        <Link to={documentEvaluationPaths.submissionDetails(requirement.existingDraftSubmissionId)}>
                          <FileUp className="h-3.5 w-3.5" aria-hidden="true" />
                          Continue Draft
                        </Link>
                      </Button>
                    ) : (
                      <Button size="sm" variant="outline" asChild>
                        <Link to={documentEvaluationPaths.submissionCreate}>
                          <Plus className="h-3.5 w-3.5" aria-hidden="true" />
                          Create Draft
                        </Link>
                      </Button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">My Submissions</CardTitle>
          <CardDescription>Draft and submitted document packages created from assigned requirements.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading submissions...</p>
          ) : submissions.length === 0 ? (
            <p className="text-sm text-muted-foreground">No document submissions have been created yet.</p>
          ) : (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    <TableHead>Requirement</TableHead>
                    <TableHead>Context</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Attempt</TableHead>
                    <TableHead>Submitted</TableHead>
                    <TableHead>Files</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {submissions.map((submission) => {
                    const requirement = requirementByKey.get(`${submission.assignmentId}:${submission.requirementId}`);
                    const isDraft = submission.status === "DRAFT";
                    return (
                      <TableRow key={submission.id}>
                        <TableCell className="font-mono text-xs">{submission.id}</TableCell>
                        <TableCell className="min-w-[220px]">
                          <div className="font-medium">{requirement?.requirementName ?? submission.submissionTitle}</div>
                          <div className="text-xs text-muted-foreground">
                            {requirement?.requirementSetName ?? `Assignment #${submission.assignmentId}`}
                          </div>
                        </TableCell>
                        <TableCell>{requirement ? contextLabel(requirement) : contextLabel(submission)}</TableCell>
                        <TableCell><StatusBadge status={submission.status} /></TableCell>
                        <TableCell>{submission.attemptNumber ?? "Not available"}</TableCell>
                        <TableCell className="whitespace-nowrap text-muted-foreground">{formatDateTime(submission.submittedAt)}</TableCell>
                        <TableCell>{fileCounts[submission.id] ?? 0}</TableCell>
                        <TableCell>
                          <div className="flex flex-wrap justify-end gap-2">
                            <Button size="sm" variant="outline" asChild>
                              <Link to={documentEvaluationPaths.submissionDetails(submission.id)}>
                                <Eye className="h-3.5 w-3.5" aria-hidden="true" />
                                {isDraft ? "Continue Draft" : "View"}
                              </Link>
                            </Button>
                            {isDraft && (
                              <Button size="sm" onClick={() => void submitDraft(submission)}>
                                <Send className="h-3.5 w-3.5" aria-hidden="true" />
                                Submit Draft
                              </Button>
                            )}
                          </div>
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </div>
          )}
          <Separator />
          <p className="text-xs text-muted-foreground">
            Returned, accepted, archived, and submitted documents are read-only in the student workflow.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}

function SummaryCard({ label, value }: { label: string; value: number }) {
  return (
    <Card>
      <CardHeader className="pb-2">
        <CardDescription>{label}</CardDescription>
        <CardTitle className="text-2xl">{value}</CardTitle>
      </CardHeader>
    </Card>
  );
}
