import { useEffect, useMemo, useState } from "react";
import { Eye, PowerOff, RefreshCw, RotateCcw } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Select } from "@/common/ui/shadcn/select";
import { Separator } from "@/common/ui/shadcn/separator";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/common/ui/shadcn/table";
import {
  AssignmentStatusBadge,
  AssignmentTypeBadge,
  RequirementSetStatusBadge,
} from "@/modules/document-evaluation/components/ConfigurationBadges";
import { RequirementSetAssignmentForm } from "@/modules/document-evaluation/components/ConfigurationForms";
import { documentEvaluationService } from "@/modules/document-evaluation/services/documentEvaluation.service";
import type {
  DocumentRequirementSetAssignment,
  DocumentRequirementSetAssignmentSummary,
  DocumentRequirementSetSummary,
  RequirementSetAssignmentStatus,
  RequirementSetAssignmentType,
} from "@/modules/document-evaluation/types";
import { projectService } from "@/modules/project/services/project.service";
import type { Project } from "@/modules/project/types";
import { courseClassService } from "@/modules/project-proposal/services/courseClass.service";
import type { CourseClass } from "@/modules/project-proposal/types";

function formatDateTime(value: string | null | undefined) {
  return value ? new Date(value).toLocaleString() : "Not available";
}

function targetName(assignment: DocumentRequirementSetAssignmentSummary | DocumentRequirementSetAssignment) {
  const courseClass = "courseClass" in assignment ? assignment.courseClass : undefined;
  const project = "project" in assignment ? assignment.project : undefined;

  return assignment.assignmentType === "COURSE_CLASS"
    ? assignment.courseClassName ?? courseClass?.name ?? `Class #${assignment.courseClassId ?? courseClass?.id ?? "unknown"}`
    : assignment.projectTitle ?? project?.title ?? project?.name ?? `Project #${assignment.projectId ?? project?.id ?? "unknown"}`;
}

export function DocumentRequirementSetAssignmentsPage() {
  const [requirementSets, setRequirementSets] = useState<DocumentRequirementSetSummary[]>([]);
  const [courseClasses, setCourseClasses] = useState<CourseClass[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [assignments, setAssignments] = useState<DocumentRequirementSetAssignmentSummary[]>([]);
  const [selectedAssignment, setSelectedAssignment] = useState<DocumentRequirementSetAssignment | null>(null);
  const [filterType, setFilterType] = useState<RequirementSetAssignmentType>("COURSE_CLASS");
  const [filterTargetId, setFilterTargetId] = useState("");
  const [filterStatus, setFilterStatus] = useState<RequirementSetAssignmentStatus | "ALL">("ALL");
  const [isLoadingLookups, setIsLoadingLookups] = useState(true);
  const [isLoadingAssignments, setIsLoadingAssignments] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const filterOptions = useMemo(
    () => (filterType === "COURSE_CLASS" ? courseClasses : projects),
    [filterType, courseClasses, projects],
  );

  async function loadLookups() {
    try {
      setIsLoadingLookups(true);
      setError(null);
      const [sets, classes, projectItems] = await Promise.all([
        documentEvaluationService.listRequirementSets({ status: "ACTIVE" }),
        courseClassService.findAll(),
        projectService.findAll(),
      ]);
      setRequirementSets(sets);
      setCourseClasses(classes);
      setProjects(projectItems);
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsLoadingLookups(false);
    }
  }

  async function loadAssignments() {
    const targetId = Number(filterTargetId);
    if (!targetId) {
      setAssignments([]);
      setSelectedAssignment(null);
      setError("Select a class or project before loading assignments.");
      return;
    }

    try {
      setIsLoadingAssignments(true);
      setError(null);
      const params = { status: filterStatus === "ALL" ? undefined : filterStatus };
      const items =
        filterType === "COURSE_CLASS"
          ? await documentEvaluationService.listAssignmentsByClass(targetId, params)
          : await documentEvaluationService.listAssignmentsByProject(targetId, params);
      setAssignments(items);
      setSelectedAssignment(null);
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsLoadingAssignments(false);
    }
  }

  useEffect(() => {
    void loadLookups();
  }, []);

  async function handleCreateAssignment(request: {
    assignmentType: RequirementSetAssignmentType;
    requirementSetId: number;
    courseClassId?: number;
    projectId?: number;
    notes?: string;
  }) {
    try {
      setIsSubmitting(true);
      setError(null);
      const created =
        request.assignmentType === "COURSE_CLASS" && request.courseClassId
          ? await documentEvaluationService.assignRequirementSetToClass({
              requirementSetId: request.requirementSetId,
              courseClassId: request.courseClassId,
              notes: request.notes,
            })
          : request.assignmentType === "PROJECT" && request.projectId
            ? await documentEvaluationService.assignRequirementSetToProject({
                requirementSetId: request.requirementSetId,
                projectId: request.projectId,
                notes: request.notes,
              })
            : null;

      if (!created) {
        setError("Select a valid assignment target.");
        return;
      }

      setNotice("Requirement set assignment created.");
      setSelectedAssignment(created);
      const createdTargetId =
        created.assignmentType === "COURSE_CLASS"
          ? created.courseClassId ?? created.courseClass?.id
          : created.projectId ?? created.project?.id;
      if (created.assignmentType === filterType && createdTargetId && String(createdTargetId) === filterTargetId) {
        await loadAssignments();
      }
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  async function openAssignment(assignmentId: number) {
    try {
      setError(null);
      setSelectedAssignment(await documentEvaluationService.getAssignmentById(assignmentId));
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  async function deactivateAssignment(assignment: DocumentRequirementSetAssignmentSummary) {
    if (!confirm(`Deactivate assignment #${assignment.id}?`)) return;
    const notes = prompt("Optional deactivation notes") ?? undefined;
    try {
      setError(null);
      await documentEvaluationService.deactivateAssignment(assignment.id, { notes });
      setNotice("Assignment deactivated.");
      await loadAssignments();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  async function reactivateAssignment(assignment: DocumentRequirementSetAssignmentSummary) {
    try {
      setError(null);
      await documentEvaluationService.reactivateAssignment(assignment.id);
      setNotice("Assignment reactivated.");
      await loadAssignments();
    } catch (err) {
      setError(documentEvaluationService.getErrorMessage(err));
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Requirement Set Assignments</h1>
          <p className="text-sm text-muted-foreground">
            Assign configured requirement sets to a class or project and review active assignments.
          </p>
        </div>
        <Button variant="outline" onClick={() => void loadLookups()}>
          <RefreshCw className="h-4 w-4" aria-hidden="true" />
          Refresh Lookups
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
          <CardTitle className="text-lg">Create Assignment</CardTitle>
          <CardDescription>Use existing class, project, and requirement set lookup APIs.</CardDescription>
        </CardHeader>
        <CardContent>
          {isLoadingLookups ? (
            <p className="text-sm text-muted-foreground">Loading selectors...</p>
          ) : (
            <RequirementSetAssignmentForm
              requirementSets={requirementSets}
              courseClasses={courseClasses}
              projects={projects}
              isSubmitting={isSubmitting}
              onSubmit={(request) => void handleCreateAssignment(request)}
            />
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Assignments</CardTitle>
          <CardDescription>
            The backend exposes class-specific and project-specific assignment lists, so choose a target to load.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-3 md:grid-cols-[180px_minmax(0,1fr)_180px_auto]">
            <Select
              value={filterType}
              onChange={(event) => {
                setFilterType(event.target.value as RequirementSetAssignmentType);
                setFilterTargetId("");
                setAssignments([]);
              }}
            >
              <option value="COURSE_CLASS">Class</option>
              <option value="PROJECT">Project</option>
            </Select>
            <Select value={filterTargetId} onChange={(event) => setFilterTargetId(event.target.value)}>
              <option value="">Select {filterType === "COURSE_CLASS" ? "a course class" : "a project"}</option>
              {filterOptions.map((target) => (
                <option key={target.id} value={target.id}>
                  {"code" in target ? `${target.code ? `${target.code} - ` : ""}${target.name}` : target.title}
                </option>
              ))}
            </Select>
            <Select value={filterStatus} onChange={(event) => setFilterStatus(event.target.value as RequirementSetAssignmentStatus | "ALL")}>
              <option value="ALL">All statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
              <option value="ARCHIVED">Archived</option>
            </Select>
            <Button onClick={() => void loadAssignments()}>
              <RefreshCw className="h-4 w-4" aria-hidden="true" />
              Load
            </Button>
          </div>

          <Separator />

          {isLoadingAssignments ? (
            <p className="text-sm text-muted-foreground">Loading assignments...</p>
          ) : assignments.length === 0 ? (
            <p className="text-sm text-muted-foreground">No assignments loaded for the selected target.</p>
          ) : (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    <TableHead>Requirement Set</TableHead>
                    <TableHead>Type</TableHead>
                    <TableHead>Target</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Assigned By</TableHead>
                    <TableHead>Assigned At</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {assignments.map((assignment) => (
                    <TableRow key={assignment.id}>
                      <TableCell className="font-mono text-xs">{assignment.id}</TableCell>
                      <TableCell className="min-w-[220px] font-medium">{assignment.requirementSetName}</TableCell>
                      <TableCell><AssignmentTypeBadge assignmentType={assignment.assignmentType} /></TableCell>
                      <TableCell>{targetName(assignment)}</TableCell>
                      <TableCell><AssignmentStatusBadge status={assignment.status} /></TableCell>
                      <TableCell>{assignment.assignedByEmail ?? "Not available"}</TableCell>
                      <TableCell className="whitespace-nowrap text-muted-foreground">{formatDateTime(assignment.assignedAt)}</TableCell>
                      <TableCell>
                        <div className="flex flex-wrap justify-end gap-2">
                          <Button size="sm" variant="outline" onClick={() => void openAssignment(assignment.id)}>
                            <Eye className="h-3.5 w-3.5" aria-hidden="true" />
                            View
                          </Button>
                          {assignment.status === "ACTIVE" ? (
                            <Button size="sm" variant="outline" onClick={() => void deactivateAssignment(assignment)}>
                              <PowerOff className="h-3.5 w-3.5" aria-hidden="true" />
                              Deactivate
                            </Button>
                          ) : assignment.status === "INACTIVE" ? (
                            <Button size="sm" variant="outline" onClick={() => void reactivateAssignment(assignment)}>
                              <RotateCcw className="h-3.5 w-3.5" aria-hidden="true" />
                              Reactivate
                            </Button>
                          ) : null}
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

      {selectedAssignment && (
        <Card>
          <CardHeader>
            <div className="flex flex-col justify-between gap-3 md:flex-row md:items-start">
              <div>
                <CardTitle className="text-lg">Assignment #{selectedAssignment.id}</CardTitle>
                <CardDescription>{selectedAssignment.requirementSetName ?? selectedAssignment.requirementSet?.name}</CardDescription>
              </div>
              <div className="flex flex-wrap gap-2">
                <AssignmentTypeBadge assignmentType={selectedAssignment.assignmentType} />
                <AssignmentStatusBadge status={selectedAssignment.status} />
              </div>
            </div>
          </CardHeader>
          <CardContent className="space-y-4">
            {selectedAssignment.requirementSet && (
              <div className="rounded-md border bg-muted/30 p-4">
                <div className="flex flex-wrap items-center gap-2">
                  <p className="font-medium">{selectedAssignment.requirementSet.name}</p>
                  <RequirementSetStatusBadge status={selectedAssignment.requirementSet.status} />
                </div>
                {selectedAssignment.requirementSet.description && (
                  <p className="mt-1 text-sm text-muted-foreground">{selectedAssignment.requirementSet.description}</p>
                )}
              </div>
            )}
            <div className="grid gap-3 text-sm md:grid-cols-3">
              <Info label="Target" value={targetName(selectedAssignment)} />
              <Info label="Assigned By" value={selectedAssignment.assignedBy?.email ?? selectedAssignment.assignedByEmail ?? "Not available"} />
              <Info label="Assigned At" value={formatDateTime(selectedAssignment.assignedAt)} />
              <Info label="Deactivated At" value={formatDateTime(selectedAssignment.deactivatedAt)} />
              <Info label="Notes" value={selectedAssignment.notes ?? "None"} />
            </div>
          </CardContent>
        </Card>
      )}

      {/* TODO Phase 11C: add a global assignments view if the backend exposes a global list endpoint. */}
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
