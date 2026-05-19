import { useCallback, useEffect, useState, type ReactNode } from "react";
import { Link, useParams } from "react-router";
import {
  Archive,
  ArrowDown,
  ArrowLeft,
  ArrowUp,
  FilePlus,
  Pencil,
  RotateCcw,
  Trash2,
  X,
} from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/common/ui/shadcn/table";
import { RequirementSetStatusBadge } from "@/modules/document-evaluation/components/ConfigurationBadges";
import { DocumentRequirementForm } from "@/modules/document-evaluation/components/ConfigurationForms";
import { documentEvaluationPaths } from "@/modules/document-evaluation/constants";
import { documentEvaluationService } from "@/modules/document-evaluation/services/documentEvaluation.service";
import type {
  DocumentRequirement,
  DocumentRequirementRequest,
  DocumentRequirementSet,
  UpdateDocumentRequirementRequest,
} from "@/modules/document-evaluation/types";

type RequirementFormMode = {
  mode: "create" | "edit";
  requirement?: DocumentRequirement;
} | null;

function formatDate(value: string | null | undefined) {
  return value ? new Date(value).toLocaleDateString() : "Not available";
}

function templateRubricSummary(requirement: DocumentRequirement) {
  const template = requirement.template?.name ?? requirement.template?.title;
  const rubric = requirement.rubric?.name ?? requirement.rubric?.title;
  return [template, rubric].filter(Boolean).join(" / ") || "None";
}

export function DocumentRequirementSetDetailsPage() {
  const { requirementSetId } = useParams<{ requirementSetId: string }>();

  const [requirementSet, setRequirementSet] = useState<DocumentRequirementSet | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [requirementFormMode, setRequirementFormMode] = useState<RequirementFormMode>(null);

  const loadRequirementSet = useCallback(async () => {
    if (!requirementSetId) return;

    try {
      setIsLoading(true);
      setError(null);
      setRequirementSet(await documentEvaluationService.getRequirementSetById(Number(requirementSetId)));
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }, [requirementSetId]);

  useEffect(() => {
    void loadRequirementSet();
  }, [loadRequirementSet]);

  useEffect(() => {
    if (!requirementFormMode) return;

    function closeOnEscape(event: KeyboardEvent) {
      if (event.key === "Escape" && !isSubmitting) {
        setRequirementFormMode(null);
      }
    }

    document.addEventListener("keydown", closeOnEscape);
    return () => document.removeEventListener("keydown", closeOnEscape);
  }, [isSubmitting, requirementFormMode]);

  async function handleArchiveRequirementSet() {
    if (!requirementSet || !confirm(`Archive requirement set "${requirementSet.name}"?`)) return;

    try {
      setError(null);
      await documentEvaluationService.archiveRequirementSet(requirementSet.id);
      setNotice("Requirement set archived.");
      await loadRequirementSet();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  async function handleActivateRequirementSet() {
    if (!requirementSet) return;

    try {
      setError(null);
      await documentEvaluationService.activateRequirementSet(requirementSet.id);
      setNotice("Requirement set activated.");
      await loadRequirementSet();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  async function handleRequirementSubmit(request: DocumentRequirementRequest | UpdateDocumentRequirementRequest) {
    if (!requirementSet || !requirementFormMode) return;

    try {
      setIsSubmitting(true);
      setError(null);
      if (requirementFormMode.mode === "edit" && requirementFormMode.requirement) {
        await documentEvaluationService.updateDocumentRequirement(
          requirementFormMode.requirement.id,
          request as UpdateDocumentRequirementRequest,
        );
        setNotice("Document requirement updated.");
      } else {
        await documentEvaluationService.addDocumentRequirement(requirementSet.id, request as DocumentRequirementRequest);
        setNotice("Document requirement added.");
      }
      setRequirementFormMode(null);
      await loadRequirementSet();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleRemoveRequirement(requirement: DocumentRequirement) {
    if (!requirementSet || !confirm(`Remove requirement "${requirement.name}"?`)) return;

    try {
      setError(null);
      await documentEvaluationService.removeDocumentRequirement(requirement.id);
      setNotice("Document requirement removed.");
      await loadRequirementSet();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  async function moveRequirement(requirement: DocumentRequirement, direction: -1 | 1) {
    if (!requirementSet?.documentRequirements) return;

    const ordered = [...requirementSet.documentRequirements].sort((a, b) => a.sortOrder - b.sortOrder);
    const index = ordered.findIndex((item) => item.id === requirement.id);
    const swapIndex = index + direction;
    if (index < 0 || swapIndex < 0 || swapIndex >= ordered.length) return;

    const reordered = [...ordered];
    const [moved] = reordered.splice(index, 1);
    reordered.splice(swapIndex, 0, moved);

    try {
      setError(null);
      const updated = await documentEvaluationService.reorderDocumentRequirements(requirementSet.id, {
        items: reordered.map((item, sortOrder) => ({ requirementId: item.id, sortOrder })),
      });
      setRequirementSet(updated);
      setNotice("Document requirements reordered.");
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Requirement Set Details</h1>
          <p className="text-sm text-muted-foreground">
            View requirement set metadata and manage assignable document requirements.
          </p>
        </div>
        <Button asChild variant="outline">
          <Link to={documentEvaluationPaths.requirementSets}>
            <ArrowLeft className="h-4 w-4" aria-hidden="true" />
            Back to Requirement Sets
          </Link>
        </Button>
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

      {isLoading ? (
        <Card>
          <CardContent className="pt-6">
            <p className="text-sm text-muted-foreground">Loading requirement set...</p>
          </CardContent>
        </Card>
      ) : !requirementSet ? (
        <Card>
          <CardContent className="pt-6">
            <p className="text-sm text-muted-foreground">Requirement set not found.</p>
          </CardContent>
        </Card>
      ) : (
        <>
          <Card>
            <CardHeader>
              <div className="flex flex-col justify-between gap-3 md:flex-row md:items-start">
                <div>
                  <CardTitle className="text-lg">{requirementSet.name}</CardTitle>
                  <CardDescription>{requirementSet.description || "No description provided."}</CardDescription>
                </div>
                <RequirementSetStatusBadge status={requirementSet.status} />
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid gap-3 text-sm md:grid-cols-4">
                <Info
                  label="Owner"
                  value={requirementSet.ownerInstructorName ?? requirementSet.ownerInstructorEmail ?? "Not available"}
                />
                <Info label="Source Preset" value={requirementSet.sourcePresetId ? String(requirementSet.sourcePresetId) : "None"} />
                <Info label="Created" value={formatDate(requirementSet.createdAt)} />
                <Info label="Updated" value={formatDate(requirementSet.updatedAt)} />
              </div>

              <div className="flex flex-wrap gap-2">
                <Button asChild variant="outline">
                  <Link to={documentEvaluationPaths.requirementSetEdit(requirementSet.id)}>
                    <Pencil className="h-4 w-4" aria-hidden="true" />
                    Edit Requirement Set
                  </Link>
                </Button>
                {requirementSet.status === "ARCHIVED" ? (
                  <Button variant="outline" onClick={() => void handleActivateRequirementSet()}>
                    <RotateCcw className="h-4 w-4" aria-hidden="true" />
                    Activate
                  </Button>
                ) : (
                  <Button variant="outline" onClick={() => void handleArchiveRequirementSet()}>
                    <Archive className="h-4 w-4" aria-hidden="true" />
                    Archive
                  </Button>
                )}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <div className="flex flex-col justify-between gap-3 md:flex-row md:items-start">
                <div>
                  <CardTitle className="text-lg">Document Requirements</CardTitle>
                  <CardDescription>
                    Add any instructor-defined document requirement and the file types it accepts.
                  </CardDescription>
                </div>
                <Button variant="outline" onClick={() => setRequirementFormMode({ mode: "create" })}>
                  <FilePlus className="h-4 w-4" aria-hidden="true" />
                  Add Requirement
                </Button>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              {requirementSet.documentRequirements?.length ? (
                <div className="overflow-x-auto">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Order</TableHead>
                        <TableHead>Name</TableHead>
                        <TableHead>Required</TableHead>
                        <TableHead>Allowed Types</TableHead>
                        <TableHead>Template/Rubric</TableHead>
                        <TableHead className="text-right">Actions</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {[...requirementSet.documentRequirements]
                        .sort((a, b) => a.sortOrder - b.sortOrder)
                        .map((requirement, index, ordered) => (
                          <TableRow key={requirement.id}>
                            <TableCell>{requirement.sortOrder}</TableCell>
                            <TableCell className="min-w-[220px]">
                              <div className="font-medium">{requirement.name}</div>
                              {requirement.description && (
                                <div className="text-xs text-muted-foreground">{requirement.description}</div>
                              )}
                            </TableCell>
                            <TableCell>
                              <Badge variant={requirement.required ? "default" : "secondary"}>
                                {requirement.required ? "Required" : "Optional"}
                              </Badge>
                            </TableCell>
                            <TableCell>{requirement.allowedFileTypes?.join(", ") || "None configured"}</TableCell>
                            <TableCell className="text-sm text-muted-foreground">{templateRubricSummary(requirement)}</TableCell>
                            <TableCell>
                              <div className="flex flex-wrap justify-end gap-2">
                                <Button
                                  size="icon"
                                  variant="outline"
                                  title="Move up"
                                  disabled={index === 0}
                                  onClick={() => void moveRequirement(requirement, -1)}
                                >
                                  <ArrowUp className="h-3.5 w-3.5" aria-hidden="true" />
                                </Button>
                                <Button
                                  size="icon"
                                  variant="outline"
                                  title="Move down"
                                  disabled={index === ordered.length - 1}
                                  onClick={() => void moveRequirement(requirement, 1)}
                                >
                                  <ArrowDown className="h-3.5 w-3.5" aria-hidden="true" />
                                </Button>
                                <Button
                                  size="sm"
                                  variant="outline"
                                  onClick={() => setRequirementFormMode({ mode: "edit", requirement })}
                                >
                                  <Pencil className="h-3.5 w-3.5" aria-hidden="true" />
                                  Edit
                                </Button>
                                <Button
                                  size="sm"
                                  variant="outline"
                                  onClick={() => void handleRemoveRequirement(requirement)}
                                >
                                  <Trash2 className="h-3.5 w-3.5" aria-hidden="true" />
                                  Remove
                                </Button>
                              </div>
                            </TableCell>
                          </TableRow>
                        ))}
                    </TableBody>
                  </Table>
                </div>
              ) : (
                <p className="text-sm text-muted-foreground">No document requirements have been added yet.</p>
              )}
            </CardContent>
          </Card>

          {requirementFormMode && (
            <RequirementFormDialog
              mode={requirementFormMode.mode}
              isSubmitting={isSubmitting}
              onClose={() => setRequirementFormMode(null)}
            >
              <DocumentRequirementForm
                initialRequirement={requirementFormMode.requirement}
                isSubmitting={isSubmitting}
                requireAllowedFileTypes
                onCancel={() => setRequirementFormMode(null)}
                onSubmit={(request) =>
                  void handleRequirementSubmit(request as DocumentRequirementRequest | UpdateDocumentRequirementRequest)
                }
              />
            </RequirementFormDialog>
          )}
        </>
      )}

      {/* TODO Phase 11C: add copy-from-preset shortcuts here if a dedicated backend endpoint is introduced. */}
    </div>
  );
}

function Info({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-md border bg-muted/30 p-3">
      <p className="text-xs font-medium uppercase tracking-normal text-muted-foreground">{label}</p>
      <p className="mt-1 font-medium">{value}</p>
    </div>
  );
}

function RequirementFormDialog({
  mode,
  isSubmitting,
  onClose,
  children,
}: {
  mode: "create" | "edit";
  isSubmitting: boolean;
  onClose: () => void;
  children: ReactNode;
}) {
  return (
    <div
      className="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto bg-background/80 px-4 py-8 backdrop-blur-sm"
      role="presentation"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !isSubmitting) onClose();
      }}
    >
      <div
        aria-labelledby="requirement-set-requirement-dialog-title"
        aria-modal="true"
        className="w-full max-w-3xl rounded-lg border bg-card p-6 shadow-lg"
        role="dialog"
      >
        <div className="mb-5 flex items-start justify-between gap-4">
          <div>
            <h2 id="requirement-set-requirement-dialog-title" className="text-lg font-semibold">
              {mode === "edit" ? "Edit Requirement" : "Add Requirement"}
            </h2>
            <p className="mt-1 text-sm text-muted-foreground">
              Configure the document requirement fields supported by the backend.
            </p>
          </div>
          <Button
            aria-label="Close requirement dialog"
            disabled={isSubmitting}
            size="sm"
            type="button"
            variant="outline"
            onClick={onClose}
          >
            <X className="h-4 w-4" aria-hidden="true" />
          </Button>
        </div>
        {children}
      </div>
    </div>
  );
}
