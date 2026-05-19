import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import { Archive, Eye, Pencil, Plus, RefreshCw, RotateCcw, Search } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Select } from "@/common/ui/shadcn/select";
import { Separator } from "@/common/ui/shadcn/separator";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/common/ui/shadcn/table";
import {
  PresetVisibilityBadge,
  RequirementSetStatusBadge,
} from "@/modules/document-evaluation/components/ConfigurationBadges";
import { documentEvaluationPaths } from "@/modules/document-evaluation/constants";
import { documentEvaluationService } from "@/modules/document-evaluation/services/documentEvaluation.service";
import type {
  ConfigurationStatus,
  DocumentRequirementPreset,
  PresetVisibility,
} from "@/modules/document-evaluation/types";
import { useAuth } from "@/modules/identity/context/AuthContext";

function formatDate(value: string | null | undefined) {
  return value ? new Date(value).toLocaleDateString() : "Not available";
}

function requirementCount(preset: DocumentRequirementPreset) {
  return preset.documentRequirements?.length ?? 0;
}

export function DocumentRequirementPresetsPage() {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";

  const [presets, setPresets] = useState<DocumentRequirementPreset[]>([]);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState<ConfigurationStatus | "ALL">("ALL");
  const [visibility, setVisibility] = useState<PresetVisibility | "ALL">("ALL");
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const filteredPresets = useMemo(() => {
    const term = search.trim().toLowerCase();
    return presets.filter((preset) => {
      const matchesSearch =
        !term ||
        preset.name.toLowerCase().includes(term) ||
        (preset.description ?? "").toLowerCase().includes(term) ||
        (preset.category ?? "").toLowerCase().includes(term);
      const matchesStatus = status === "ALL" || preset.status === status;
      const matchesVisibility = visibility === "ALL" || preset.visibility === visibility;
      return matchesSearch && matchesStatus && matchesVisibility;
    });
  }, [presets, search, status, visibility]);

  async function loadPresets() {
    try {
      setIsLoading(true);
      setError(null);
      const params = {
        search: search.trim() || undefined,
        status: status === "ALL" ? undefined : status,
        visibility: visibility === "ALL" ? undefined : visibility,
      };
      setPresets(await documentEvaluationService.listPresets(params));
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadPresets();
    // The explicit Refresh button applies filter changes to the backend.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function handleArchivePreset(preset: DocumentRequirementPreset) {
    if (!confirm(`Archive preset "${preset.name}"?`)) return;

    try {
      setError(null);
      await documentEvaluationService.archivePreset(preset.id);
      setNotice("Preset archived.");
      await loadPresets();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  async function handleActivatePreset(preset: DocumentRequirementPreset) {
    try {
      setError(null);
      await documentEvaluationService.activatePreset(preset.id);
      setNotice("Preset activated.");
      await loadPresets();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Document Requirement Presets</h1>
          <p className="text-sm text-muted-foreground">
            Manage reusable document requirement presets for instructor configuration workflows.
          </p>
        </div>
        {isAdmin && (
          <Button asChild>
            <Link to={documentEvaluationPaths.presetCreate}>
              <Plus className="h-4 w-4" aria-hidden="true" />
              Create Preset
            </Link>
          </Button>
        )}
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

      {!isAdmin && (
        <Alert>
          <AlertDescription>
            Preset authoring is restricted by the backend to administrators. Instructors can view and copy presets into requirement sets.
          </AlertDescription>
        </Alert>
      )}

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Presets</CardTitle>
          <CardDescription>Filter by text, status, or visibility.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-3 md:grid-cols-[minmax(0,1fr)_180px_220px_auto]">
            <div className="relative">
              <Search className="pointer-events-none absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" aria-hidden="true" />
              <Input
                className="pl-9"
                placeholder="Search presets"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
              />
            </div>
            <Select value={status} onChange={(event) => setStatus(event.target.value as ConfigurationStatus | "ALL")}>
              <option value="ALL">All statuses</option>
              <option value="DRAFT">Draft</option>
              <option value="ACTIVE">Active</option>
              <option value="ARCHIVED">Archived</option>
            </Select>
            <Select value={visibility} onChange={(event) => setVisibility(event.target.value as PresetVisibility | "ALL")}>
              <option value="ALL">All visibility</option>
              <option value="SYSTEM">System</option>
              <option value="INSTRUCTOR_PRIVATE">Instructor Private</option>
              <option value="DEPARTMENT_SHARED">Department Shared</option>
            </Select>
            <Button variant="outline" onClick={() => void loadPresets()}>
              <RefreshCw className="h-4 w-4" aria-hidden="true" />
              Refresh
            </Button>
          </div>

          <Separator />

          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading presets...</p>
          ) : filteredPresets.length === 0 ? (
            <p className="text-sm text-muted-foreground">No document requirement presets found.</p>
          ) : (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Name</TableHead>
                    <TableHead>Visibility</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Requirements</TableHead>
                    <TableHead>Updated</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredPresets.map((preset) => (
                    <TableRow key={preset.id}>
                      <TableCell className="min-w-[240px]">
                        <div className="font-medium">{preset.name}</div>
                        <div className="text-xs text-muted-foreground">{preset.category ?? "Uncategorized"}</div>
                      </TableCell>
                      <TableCell><PresetVisibilityBadge visibility={preset.visibility} /></TableCell>
                      <TableCell><RequirementSetStatusBadge status={preset.status} /></TableCell>
                      <TableCell>{requirementCount(preset)}</TableCell>
                      <TableCell className="whitespace-nowrap text-muted-foreground">{formatDate(preset.updatedAt)}</TableCell>
                      <TableCell>
                        <div className="flex flex-wrap justify-end gap-2">
                          <Button size="sm" variant="outline" asChild>
                            <Link to={documentEvaluationPaths.presetDetails(preset.id)}>
                              <Eye className="h-3.5 w-3.5" aria-hidden="true" />
                              View
                            </Link>
                          </Button>
                          {isAdmin && (
                            <>
                              <Button size="sm" variant="outline" asChild>
                                <Link to={documentEvaluationPaths.presetEdit(preset.id)}>
                                  <Pencil className="h-3.5 w-3.5" aria-hidden="true" />
                                  Edit
                                </Link>
                              </Button>
                              {preset.status === "ARCHIVED" ? (
                                <Button size="sm" variant="outline" onClick={() => void handleActivatePreset(preset)}>
                                  <RotateCcw className="h-3.5 w-3.5" aria-hidden="true" />
                                  Activate
                                </Button>
                              ) : (
                                <Button size="sm" variant="outline" onClick={() => void handleArchivePreset(preset)}>
                                  <Archive className="h-3.5 w-3.5" aria-hidden="true" />
                                  Archive
                                </Button>
                              )}
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
        </CardContent>
      </Card>
    </div>
  );
}
