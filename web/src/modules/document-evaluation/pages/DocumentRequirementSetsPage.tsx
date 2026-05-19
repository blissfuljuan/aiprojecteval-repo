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
import { RequirementSetStatusBadge } from "@/modules/document-evaluation/components/ConfigurationBadges";
import { documentEvaluationPaths } from "@/modules/document-evaluation/constants";
import { documentEvaluationService } from "@/modules/document-evaluation/services/documentEvaluation.service";
import type {
  ConfigurationStatus,
  DocumentRequirementSetSummary,
} from "@/modules/document-evaluation/types";

function formatDate(value: string | null | undefined) {
  return value ? new Date(value).toLocaleDateString() : "Not available";
}

export function DocumentRequirementSetsPage() {
  const [requirementSets, setRequirementSets] = useState<DocumentRequirementSetSummary[]>([]);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState<ConfigurationStatus | "ALL">("ALL");
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const filteredRequirementSets = useMemo(() => {
    const term = search.trim().toLowerCase();
    return requirementSets.filter((requirementSet) => {
      const matchesSearch =
        !term ||
        requirementSet.name.toLowerCase().includes(term) ||
        (requirementSet.description ?? "").toLowerCase().includes(term) ||
        (requirementSet.ownerInstructorName ?? "").toLowerCase().includes(term);
      const matchesStatus = status === "ALL" || requirementSet.status === status;
      return matchesSearch && matchesStatus;
    });
  }, [requirementSets, search, status]);

  async function loadRequirementSets() {
    try {
      setIsLoading(true);
      setError(null);
      setRequirementSets(
        await documentEvaluationService.listRequirementSets({
          search: search.trim() || undefined,
          status: status === "ALL" ? undefined : status,
        }),
      );
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadRequirementSets();
    // The explicit Refresh button applies filter changes to the backend.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function handleArchiveRequirementSet(requirementSet: DocumentRequirementSetSummary) {
    if (!confirm(`Archive requirement set "${requirementSet.name}"?`)) return;

    try {
      setError(null);
      await documentEvaluationService.archiveRequirementSet(requirementSet.id);
      setNotice("Requirement set archived.");
      await loadRequirementSets();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  async function handleActivateRequirementSet(requirementSet: DocumentRequirementSetSummary) {
    try {
      setError(null);
      await documentEvaluationService.activateRequirementSet(requirementSet.id);
      setNotice("Requirement set activated.");
      await loadRequirementSets();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Document Requirement Sets</h1>
          <p className="text-sm text-muted-foreground">
            Build assignable document requirement sets for classes and projects.
          </p>
        </div>
        <Button asChild>
          <Link to={documentEvaluationPaths.requirementSetCreate}>
            <Plus className="h-4 w-4" aria-hidden="true" />
            Create Requirement Set
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

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Requirement Sets</CardTitle>
          <CardDescription>Search by name, description, or owner.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-3 md:grid-cols-[minmax(0,1fr)_180px_auto]">
            <div className="relative">
              <Search className="pointer-events-none absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" aria-hidden="true" />
              <Input
                className="pl-9"
                placeholder="Search requirement sets"
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
            <Button variant="outline" onClick={() => void loadRequirementSets()}>
              <RefreshCw className="h-4 w-4" aria-hidden="true" />
              Refresh
            </Button>
          </div>

          <Separator />

          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading requirement sets...</p>
          ) : filteredRequirementSets.length === 0 ? (
            <p className="text-sm text-muted-foreground">No document requirement sets found.</p>
          ) : (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Name</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Requirements</TableHead>
                    <TableHead>Owner</TableHead>
                    <TableHead>Updated</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredRequirementSets.map((requirementSet) => (
                    <TableRow key={requirementSet.id}>
                      <TableCell className="min-w-[240px]">
                        <div className="font-medium">{requirementSet.name}</div>
                        {requirementSet.description && (
                          <div className="text-xs text-muted-foreground">{requirementSet.description}</div>
                        )}
                      </TableCell>
                      <TableCell><RequirementSetStatusBadge status={requirementSet.status} /></TableCell>
                      <TableCell>{requirementSet.documentRequirementCount}</TableCell>
                      <TableCell>{requirementSet.ownerInstructorName ?? requirementSet.ownerInstructorEmail ?? "Not available"}</TableCell>
                      <TableCell className="whitespace-nowrap text-muted-foreground">{formatDate(requirementSet.updatedAt)}</TableCell>
                      <TableCell>
                        <div className="flex flex-wrap justify-end gap-2">
                          <Button size="sm" variant="outline" asChild>
                            <Link to={documentEvaluationPaths.requirementSetDetails(requirementSet.id)}>
                              <Eye className="h-3.5 w-3.5" aria-hidden="true" />
                              View
                            </Link>
                          </Button>
                          <Button size="sm" variant="outline" asChild>
                            <Link to={documentEvaluationPaths.requirementSetEdit(requirementSet.id)}>
                              <Pencil className="h-3.5 w-3.5" aria-hidden="true" />
                              Edit
                            </Link>
                          </Button>
                          {requirementSet.status === "ARCHIVED" ? (
                            <Button size="sm" variant="outline" onClick={() => void handleActivateRequirementSet(requirementSet)}>
                              <RotateCcw className="h-3.5 w-3.5" aria-hidden="true" />
                              Activate
                            </Button>
                          ) : (
                            <Button size="sm" variant="outline" onClick={() => void handleArchiveRequirementSet(requirementSet)}>
                              <Archive className="h-3.5 w-3.5" aria-hidden="true" />
                              Archive
                            </Button>
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
