import { useEffect, useMemo, useState } from "react";
import type { ChangeEvent } from "react";
import { Link, useParams } from "react-router";
import { ArrowLeft, Download, Eye, RefreshCw, Send, Trash2, Upload, Wand2 } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Separator } from "@/common/ui/shadcn/separator";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/common/ui/shadcn/table";
import { documentEvaluationPaths } from "@/modules/document-evaluation/constants";
import { documentEvaluationService } from "@/modules/document-evaluation/services/documentEvaluation.service";
import type { MyAssignedDocumentRequirement } from "@/modules/document-evaluation/types";
import { submissionService } from "@/modules/submission/services/submission.service";
import type { DocumentSubmission, DocumentSubmissionStatus, SubmissionFileResponse } from "@/modules/submission/types";

function formatDateTime(value: string | null | undefined) {
  return value ? new Date(value).toLocaleString() : "Not available";
}

function formatBytes(value: number | null | undefined) {
  if (!value) return "Not available";
  if (value < 1024) return `${value} B`;
  if (value < 1024 * 1024) return `${(value / 1024).toFixed(1)} KB`;
  return `${(value / 1024 / 1024).toFixed(1)} MB`;
}

function statusLabel(status: DocumentSubmissionStatus) {
  return status.replace(/_/g, " ").toLowerCase().replace(/\b\w/g, (letter: string) => letter.toUpperCase());
}

function StatusBadge({ status }: { status: DocumentSubmissionStatus }) {
  const className =
    status === "DRAFT"
      ? "border-sky-200 bg-sky-50 text-sky-700"
      : status === "RETURNED"
        ? "border-rose-200 bg-rose-50 text-rose-700"
        : status === "ACCEPTED"
          ? "border-emerald-200 bg-emerald-50 text-emerald-700"
          : "border-amber-200 bg-amber-50 text-amber-700";

  return (
    <Badge variant="outline" className={className}>
      {statusLabel(status)}
    </Badge>
  );
}

async function openBlob(blob: Blob, fileName: string, download: boolean) {
  const url = URL.createObjectURL(blob);
  if (download) {
    const anchor = document.createElement("a");
    anchor.href = url;
    anchor.download = fileName;
    anchor.click();
    URL.revokeObjectURL(url);
    return;
  }
  window.open(url, "_blank", "noopener,noreferrer");
  window.setTimeout(() => URL.revokeObjectURL(url), 60_000);
}

export function DocumentSubmissionDetailsPage() {
  const params = useParams();
  const submissionId = Number(params.submissionId);
  const [submission, setSubmission] = useState<DocumentSubmission | null>(null);
  const [requirement, setRequirement] = useState<MyAssignedDocumentRequirement | null>(null);
  const [files, setFiles] = useState<SubmissionFileResponse[]>([]);
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [replaceFileById, setReplaceFileById] = useState<Record<number, File | undefined>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [isBusy, setIsBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const isEditable = submission?.status === "DRAFT";
  const uploadedFiles = useMemo(() => files.filter((file) => file.fileStatus === "UPLOADED"), [files]);

  async function loadDetails() {
    if (!submissionId) {
      setError("Invalid submission ID.");
      setIsLoading(false);
      return;
    }

    try {
      setIsLoading(true);
      setError(null);
      const [submissionDetails, assignedRequirements, submissionFiles] = await Promise.all([
        submissionService.getSubmissionById(submissionId),
        documentEvaluationService.listMyAssignedDocumentRequirements(),
        submissionService.listSubmissionFiles(submissionId),
      ]);
      setSubmission(submissionDetails);
      setFiles(submissionFiles);
      setRequirement(
        assignedRequirements.find(
          (item) =>
            item.assignmentId === submissionDetails.assignmentId &&
            item.documentRequirementId === submissionDetails.requirementId,
        ) ?? null,
      );
    } catch (err) {
      setError(submissionService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadDetails();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [submissionId]);

  function handleUploadSelection(event: ChangeEvent<HTMLInputElement>) {
    setSelectedFiles(Array.from(event.target.files ?? []));
  }

  async function uploadFiles() {
    if (!submission || selectedFiles.length === 0) {
      setError("Select at least one file before uploading.");
      return;
    }

    try {
      setIsBusy(true);
      setError(null);
      if (selectedFiles.length === 1) {
        await submissionService.uploadSubmissionFile(submission.id, selectedFiles[0]);
      } else {
        await submissionService.uploadMultipleSubmissionFiles(submission.id, selectedFiles);
      }
      setSelectedFiles([]);
      setNotice("File upload completed.");
      await loadDetails();
    } catch (err) {
      setError(submissionService.getErrorMessage(err));
    } finally {
      setIsBusy(false);
    }
  }

  async function replaceFile(file: SubmissionFileResponse) {
    const replacement = replaceFileById[file.id];
    if (!replacement) {
      setError("Select a replacement file first.");
      return;
    }
    if (!confirm(`Replace "${file.originalFileName ?? "this file"}"?`)) return;

    try {
      setIsBusy(true);
      setError(null);
      await submissionService.replaceSubmissionFile(file.id, replacement);
      setReplaceFileById((current) => ({ ...current, [file.id]: undefined }));
      setNotice("File replaced.");
      await loadDetails();
    } catch (err) {
      setError(submissionService.getErrorMessage(err));
    } finally {
      setIsBusy(false);
    }
  }

  async function deleteFile(file: SubmissionFileResponse) {
    if (!confirm(`Delete "${file.originalFileName ?? "this file"}" from the draft?`)) return;

    try {
      setIsBusy(true);
      setError(null);
      await submissionService.deleteSubmissionFile(file.id);
      setNotice("File deleted.");
      await loadDetails();
    } catch (err) {
      setError(submissionService.getErrorMessage(err));
    } finally {
      setIsBusy(false);
    }
  }

  async function submitDraft() {
    if (!submission) return;
    if (uploadedFiles.length < 1) {
      setError("Upload at least one file before submitting this draft.");
      return;
    }
    if (!confirm(`Submit draft #${submission.id}? Editing will be disabled after submission.`)) return;

    try {
      setIsBusy(true);
      setError(null);
      await submissionService.submitDraft(submission.id);
      setNotice("Draft submitted.");
      await loadDetails();
    } catch (err) {
      setError(submissionService.getErrorMessage(err));
    } finally {
      setIsBusy(false);
    }
  }

  async function viewFile(file: SubmissionFileResponse) {
    try {
      const blob = await submissionService.viewSubmissionFile(file.id);
      await openBlob(blob, file.originalFileName ?? "submission-file", false);
    } catch {
      try {
        const blob = await submissionService.downloadSubmissionFile(file.id);
        await openBlob(blob, file.originalFileName ?? "submission-file", true);
      } catch (err) {
        setError(submissionService.getErrorMessage(err));
      }
    }
  }

  async function downloadFile(file: SubmissionFileResponse) {
    try {
      const blob = await submissionService.downloadSubmissionFile(file.id);
      await openBlob(blob, file.originalFileName ?? "submission-file", true);
    } catch (err) {
      setError(submissionService.getErrorMessage(err));
    }
  }

  if (isLoading) {
    return <p className="text-sm text-muted-foreground">Loading document submission...</p>;
  }

  if (!submission) {
    return (
      <Alert variant="destructive">
        <AlertDescription>{error ?? "Document submission could not be loaded."}</AlertDescription>
      </Alert>
    );
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-2">
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="text-3xl font-semibold tracking-normal">
              {requirement?.requirementName ?? submission.submissionTitle}
            </h1>
            <StatusBadge status={submission.status} />
          </div>
          <p className="text-sm text-muted-foreground">
            Manage uploaded files for this document requirement and submit the draft when ready.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button variant="outline" asChild>
            <Link to={documentEvaluationPaths.submissions}>
              <ArrowLeft className="h-4 w-4" aria-hidden="true" />
              Back to Submissions
            </Link>
          </Button>
          <Button variant="outline" onClick={() => void loadDetails()}>
            <RefreshCw className="h-4 w-4" aria-hidden="true" />
            Refresh
          </Button>
          <Button disabled={!isEditable || isBusy} onClick={() => void submitDraft()}>
            <Send className="h-4 w-4" aria-hidden="true" />
            Submit Draft
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

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_22rem]">
        <div className="grid gap-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Requirement Details</CardTitle>
              <CardDescription>{requirement?.requirementSetName ?? `Assignment #${submission.assignmentId}`}</CardDescription>
            </CardHeader>
            <CardContent className="grid gap-3 text-sm md:grid-cols-2">
              <Info label="Submission ID" value={String(submission.id)} />
              <Info label="Attempt" value={String(submission.attemptNumber ?? "Not available")} />
              <Info label="Context" value={requirement?.projectTitle ?? requirement?.courseClassName ?? "Not available"} />
              <Info label="Submitted At" value={formatDateTime(submission.submittedAt)} />
              <Info label="Required" value={requirement ? (requirement.required ? "Yes" : "No") : "Not available"} />
              <Info label="Allowed File Types" value={requirement?.allowedFileTypes?.join(", ") || "Not restricted"} />
              <div className="md:col-span-2">
                <Info label="Instructions" value={requirement?.requirementDescription ?? submission.submissionNotes ?? "No instructions provided."} />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Uploaded Files</CardTitle>
              <CardDescription>
                {isEditable
                  ? "Upload, replace, or delete files while this submission is still a draft."
                  : "Submitted and completed documents are read-only."}
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {isEditable && (
                <div className="rounded-md border bg-muted/30 p-4">
                  <div className="grid gap-3 md:grid-cols-[minmax(0,1fr)_auto] md:items-end">
                    <div className="grid gap-2">
                      <Label htmlFor="upload">Upload file</Label>
                      <Input id="upload" type="file" multiple onChange={handleUploadSelection} />
                      <p className="text-xs text-muted-foreground">
                        {requirement?.allowedFileTypes?.length
                          ? `Allowed file types: ${requirement.allowedFileTypes.join(", ")}`
                          : "Select one or more files required by this document requirement."}
                      </p>
                    </div>
                    <Button disabled={isBusy || selectedFiles.length === 0} onClick={() => void uploadFiles()}>
                      <Upload className="h-4 w-4" aria-hidden="true" />
                      Upload
                    </Button>
                  </div>
                  {selectedFiles.length > 0 && (
                    <p className="mt-2 text-xs text-muted-foreground">
                      Selected: {selectedFiles.map((file) => file.name).join(", ")}
                    </p>
                  )}
                </div>
              )}

              {files.length === 0 ? (
                <p className="text-sm text-muted-foreground">No files have been uploaded yet.</p>
              ) : (
                <div className="overflow-x-auto">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Filename</TableHead>
                        <TableHead>Size</TableHead>
                        <TableHead>Type</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead>Uploaded</TableHead>
                        <TableHead className="text-right">Actions</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {files.map((file) => (
                        <TableRow key={file.id}>
                          <TableCell className="min-w-[220px]">
                            <div className="font-medium">{file.originalFileName ?? "Unnamed file"}</div>
                            {file.checksum && <div className="text-xs text-muted-foreground">Checksum: {file.checksum}</div>}
                          </TableCell>
                          <TableCell>{formatBytes(file.fileSize)}</TableCell>
                          <TableCell>{file.fileExtension ?? file.contentType ?? "Not available"}</TableCell>
                          <TableCell>
                            <Badge variant="outline">{file.fileStatus.replace(/_/g, " ")}</Badge>
                          </TableCell>
                          <TableCell className="whitespace-nowrap text-muted-foreground">{formatDateTime(file.uploadedAt)}</TableCell>
                          <TableCell>
                            <div className="flex flex-wrap justify-end gap-2">
                              <Button size="sm" variant="outline" onClick={() => void viewFile(file)}>
                                <Eye className="h-3.5 w-3.5" aria-hidden="true" />
                                View
                              </Button>
                              <Button size="sm" variant="outline" onClick={() => void downloadFile(file)}>
                                <Download className="h-3.5 w-3.5" aria-hidden="true" />
                                Download
                              </Button>
                              {isEditable && (
                                <>
                                  <Label className="sr-only" htmlFor={`replace-${file.id}`}>
                                    Replacement file
                                  </Label>
                                  <Input
                                    id={`replace-${file.id}`}
                                    className="h-9 w-52"
                                    type="file"
                                    onChange={(event) =>
                                      setReplaceFileById((current) => ({
                                        ...current,
                                        [file.id]: event.target.files?.[0],
                                      }))
                                    }
                                  />
                                  <Button size="sm" variant="outline" onClick={() => void replaceFile(file)}>
                                    <Wand2 className="h-3.5 w-3.5" aria-hidden="true" />
                                    Replace
                                  </Button>
                                  <Button size="sm" variant="outline" onClick={() => void deleteFile(file)}>
                                    <Trash2 className="h-3.5 w-3.5" aria-hidden="true" />
                                    Delete
                                  </Button>
                                </>
                              )}
                            </div>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </div>
              )}
              <Separator />
              <p className="text-xs text-muted-foreground">
                The backend remains the source of truth for file type validation and whether a submission can be edited.
              </p>
            </CardContent>
          </Card>
        </div>

        <aside className="grid h-fit gap-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Submission State</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 text-sm">
              <Info label="Status" value={statusLabel(submission.status)} />
              <Info label="Uploaded Files" value={String(uploadedFiles.length)} />
              <Info label="Last Updated" value={formatDateTime(submission.lastUpdatedAt)} />
              <Info label="Editing" value={isEditable ? "Enabled" : "Disabled"} />
            </CardContent>
          </Card>
        </aside>
      </div>
    </div>
  );
}

function Info({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-md border bg-background p-3">
      <p className="text-xs font-medium uppercase tracking-normal text-muted-foreground">{label}</p>
      <p className="mt-1 break-words font-medium">{value}</p>
    </div>
  );
}
