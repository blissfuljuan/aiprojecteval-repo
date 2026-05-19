import { useCallback, useEffect, useState, type ReactNode } from "react";
import { Link, useParams } from "react-router";
import { Archive, ArrowLeft, Copy, FilePlus, Pencil, RotateCcw, Trash2, X } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/common/ui/shadcn/table";
import {
  PresetVisibilityBadge,
  RequirementSetStatusBadge,
} from "@/modules/document-evaluation/components/ConfigurationBadges";
import {
  CopyPresetForm,
  DocumentRequirementForm,
} from "@/modules/document-evaluation/components/ConfigurationForms";
import { documentEvaluationPaths } from "@/modules/document-evaluation/constants";
import { documentEvaluationService } from "@/modules/document-evaluation/services/documentEvaluation.service";
import type {
  DocumentRequirementPreset,
  PresetDocumentRequirement,
  PresetDocumentRequirementRequest,
} from "@/modules/document-evaluation/types";
import { useAuth } from "@/modules/identity/context/AuthContext";

type RequirementFormMode = {
  mode: "create" | "edit";
  requirement?: PresetDocumentRequirement;
} | null;

function formatDate(value: string | null | undefined) {
  return value ? new Date(value).toLocaleDateString() : "Not available";
}

export function DocumentRequirementPresetDetailsPage() {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";
  const { presetId } = useParams<{ presetId: string }>();

  const [preset, setPreset] = useState<DocumentRequirementPreset | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [showCopyForm, setShowCopyForm] = useState(false);
  const [requirementFormMode, setRequirementFormMode] = useState<RequirementFormMode>(null);

  const loadPreset = useCallback(async () => {
    if (!presetId) return;

    try {
      setIsLoading(true);
      setError(null);
      setPreset(await documentEvaluationService.getPresetById(Number(presetId)));
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }, [presetId]);

  useEffect(() => {
    void loadPreset();
  }, [loadPreset]);

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

  async function handleCopyPreset(request: Parameters<typeof documentEvaluationService.copyPreset>[1]) {
    if (!preset) return;

    try {
      setIsSubmitting(true);
      setError(null);
      const copied = await documentEvaluationService.copyPreset(preset.id, request);
      setNotice(`Copied to requirement set "${copied.name}".`);
      setShowCopyForm(false);
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleArchivePreset() {
    if (!preset || !confirm(`Archive preset "${preset.name}"?`)) return;

    try {
      setError(null);
      await documentEvaluationService.archivePreset(preset.id);
      setNotice("Preset archived.");
      await loadPreset();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  async function handleActivatePreset() {
    if (!preset) return;

    try {
      setError(null);
      await documentEvaluationService.activatePreset(preset.id);
      setNotice("Preset activated.");
      await loadPreset();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  async function handleRequirementSubmit(request: PresetDocumentRequirementRequest) {
    if (!preset || !requirementFormMode) return;

    try {
      setIsSubmitting(true);
      setError(null);
      const updated =
        requirementFormMode.mode === "edit" && requirementFormMode.requirement
          ? await documentEvaluationService.updatePresetRequirement(
              preset.id,
              requirementFormMode.requirement.id,
              request,
            )
          : await documentEvaluationService.addPresetRequirement(preset.id, request);
      setPreset(updated);
      setNotice(requirementFormMode.mode === "edit" ? "Preset requirement updated." : "Preset requirement added.");
      setRequirementFormMode(null);
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleRemoveRequirement(requirement: PresetDocumentRequirement) {
    if (!preset || !confirm(`Remove requirement "${requirement.name}"?`)) return;

    try {
      setError(null);
      await documentEvaluationService.removePresetRequirement(preset.id, requirement.id);
      setNotice("Preset requirement removed.");
      await loadPreset();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Preset Details</h1>
          <p className="text-sm text-muted-foreground">
            View preset metadata and manage instructor-defined document requirements.
          </p>
        </div>
        <Button asChild variant="outline">
          <Link to={documentEvaluationPaths.presets}>
            <ArrowLeft className="h-4 w-4" aria-hidden="true" />
            Back to Presets
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
            <p className="text-sm text-muted-foreground">Loading preset...</p>
          </CardContent>
        </Card>
      ) : !preset ? (
        <Card>
          <CardContent className="pt-6">
            <p className="text-sm text-muted-foreground">Preset not found.</p>
          </CardContent>
        </Card>
      ) : (
        <>
          <Card>
            <CardHeader>
              <div className="flex flex-col justify-between gap-3 md:flex-row md:items-start">
                <div>
                  <CardTitle className="text-lg">{preset.name}</CardTitle>
                  <CardDescription>{preset.description || "No description provided."}</CardDescription>
                </div>
                <div className="flex flex-wrap gap-2">
                  <PresetVisibilityBadge visibility={preset.visibility} />
                  <RequirementSetStatusBadge status={preset.status} />
                </div>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid gap-3 text-sm md:grid-cols-4">
                <Info label="Category" value={preset.category ?? "Uncategorized"} />
                <Info label="Created By" value={preset.createdByName ?? preset.createdByEmail ?? "Not available"} />
                <Info label="Created" value={formatDate(preset.createdAt)} />
                <Info label="Updated" value={formatDate(preset.updatedAt)} />
              </div>

              <div className="flex flex-wrap gap-2">
                <Button variant="outline" onClick={() => setShowCopyForm((value) => !value)}>
                  <Copy className="h-4 w-4" aria-hidden="true" />
                  Copy to Requirement Set
                </Button>
                {isAdmin && (
                  <>
                    <Button asChild variant="outline">
                      <Link to={documentEvaluationPaths.presetEdit(preset.id)}>
                        <Pencil className="h-4 w-4" aria-hidden="true" />
                        Edit Preset
                      </Link>
                    </Button>
                    {preset.status === "ARCHIVED" ? (
                      <Button variant="outline" onClick={() => void handleActivatePreset()}>
                        <RotateCcw className="h-4 w-4" aria-hidden="true" />
                        Activate
                      </Button>
                    ) : (
                      <Button variant="outline" onClick={() => void handleArchivePreset()}>
                        <Archive className="h-4 w-4" aria-hidden="true" />
                        Archive
                      </Button>
                    )}
                  </>
                )}
              </div>
            </CardContent>
          </Card>

          {showCopyForm && (
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Copy Preset</CardTitle>
                <CardDescription>Create a requirement set from this preset.</CardDescription>
              </CardHeader>
              <CardContent>
                <CopyPresetForm
                  preset={preset}
                  isSubmitting={isSubmitting}
                  onCancel={() => setShowCopyForm(false)}
                  onSubmit={(request) => void handleCopyPreset(request)}
                />
              </CardContent>
            </Card>
          )}

          <Card>
            <CardHeader>
              <div className="flex flex-col justify-between gap-3 md:flex-row md:items-start">
                <div>
                  <CardTitle className="text-lg">Preset Requirements</CardTitle>
                  <CardDescription>
                    Document names and file rules are instructor-defined; no fixed document types are assumed.
                  </CardDescription>
                </div>
                {isAdmin && (
                  <Button variant="outline" onClick={() => setRequirementFormMode({ mode: "create" })}>
                    <FilePlus className="h-4 w-4" aria-hidden="true" />
                    Add Requirement
                  </Button>
                )}
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              {preset.documentRequirements?.length ? (
                <div className="overflow-x-auto">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Order</TableHead>
                        <TableHead>Name</TableHead>
                        <TableHead>Required</TableHead>
                        <TableHead>Allowed Types</TableHead>
                        <TableHead>Template/Rubric</TableHead>
                        {isAdmin && <TableHead className="text-right">Actions</TableHead>}
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {[...preset.documentRequirements]
                        .sort((a, b) => a.sortOrder - b.sortOrder)
                        .map((requirement) => (
                          <TableRow key={requirement.id}>
                            <TableCell>{requirement.sortOrder}</TableCell>
                            <TableCell className="min-w-[220px]">
                              <div className="font-medium">{requirement.name}</div>
                              {requirement.description && (
                                <div className="text-xs text-muted-foreground">{requirement.description}</div>
                              )}
                            </TableCell>
                            <TableCell>{requirement.required ? "Required" : "Optional"}</TableCell>
                            <TableCell>{requirement.allowedFileTypes?.join(", ") || "Any supported type"}</TableCell>
                            <TableCell className="text-sm text-muted-foreground">
                              {requirement.templateName || requirement.rubricName
                                ? [requirement.templateName, requirement.rubricName].filter(Boolean).join(" / ")
                                : "None"}
                            </TableCell>
                            {isAdmin && (
                              <TableCell>
                                <div className="flex justify-end gap-2">
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
                            )}
                          </TableRow>
                        ))}
                    </TableBody>
                  </Table>
                </div>
              ) : (
                <p className="text-sm text-muted-foreground">No preset requirements have been added yet.</p>
              )}

              {/* TODO Phase 11C: add preset requirement reordering if the backend exposes a reorder endpoint. */}
            </CardContent>
          </Card>

          {requirementFormMode && isAdmin && (
            <RequirementFormDialog
              mode={requirementFormMode.mode}
              isSubmitting={isSubmitting}
              onClose={() => setRequirementFormMode(null)}
            >
              <DocumentRequirementForm
                initialRequirement={requirementFormMode.requirement}
                isSubmitting={isSubmitting}
                requireAllowedFileTypes={false}
                onCancel={() => setRequirementFormMode(null)}
                onSubmit={(request) => void handleRequirementSubmit(request as PresetDocumentRequirementRequest)}
              />
            </RequirementFormDialog>
          )}
        </>
      )}
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
        aria-labelledby="requirement-dialog-title"
        aria-modal="true"
        className="w-full max-w-3xl rounded-lg border bg-card p-6 shadow-lg"
        role="dialog"
      >
        <div className="mb-5 flex items-start justify-between gap-4">
          <div>
            <h2 id="requirement-dialog-title" className="text-lg font-semibold">
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
