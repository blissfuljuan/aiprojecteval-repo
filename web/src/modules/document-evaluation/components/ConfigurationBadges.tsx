import { Badge } from "@/common/ui/shadcn/badge";
import { cn } from "@/common/lib/utils";
import type {
  ConfigurationStatus,
  PresetVisibility,
  RequirementSetAssignmentStatus,
  RequirementSetAssignmentType,
} from "@/modules/document-evaluation/types";

const statusStyles: Record<string, string> = {
  DRAFT: "border-slate-200 bg-slate-50 text-slate-700",
  ACTIVE: "border-emerald-200 bg-emerald-50 text-emerald-700",
  ARCHIVED: "border-zinc-200 bg-zinc-50 text-zinc-600",
  INACTIVE: "border-amber-200 bg-amber-50 text-amber-700",
  SYSTEM: "border-indigo-200 bg-indigo-50 text-indigo-700",
  INSTRUCTOR_PRIVATE: "border-stone-200 bg-stone-50 text-stone-700",
  DEPARTMENT_SHARED: "border-cyan-200 bg-cyan-50 text-cyan-700",
  COURSE_CLASS: "border-blue-200 bg-blue-50 text-blue-700",
  PROJECT: "border-violet-200 bg-violet-50 text-violet-700",
};

const labels: Record<string, string> = {
  DRAFT: "Draft",
  ACTIVE: "Active",
  ARCHIVED: "Archived",
  INACTIVE: "Inactive",
  SYSTEM: "System",
  INSTRUCTOR_PRIVATE: "Instructor Private",
  DEPARTMENT_SHARED: "Department Shared",
  COURSE_CLASS: "Class",
  PROJECT: "Project",
};

function ConfigBadge({ value, className }: { value: string | null | undefined; className?: string }) {
  if (!value) return <span className="text-sm text-muted-foreground">Not set</span>;

  return (
    <Badge variant="outline" className={cn("whitespace-nowrap", statusStyles[value], className)}>
      {labels[value] ?? value}
    </Badge>
  );
}

export function RequirementSetStatusBadge({ status }: { status: ConfigurationStatus | null | undefined }) {
  return <ConfigBadge value={status} />;
}

export function PresetVisibilityBadge({ visibility }: { visibility: PresetVisibility | null | undefined }) {
  return <ConfigBadge value={visibility} />;
}

export function AssignmentStatusBadge({ status }: { status: RequirementSetAssignmentStatus | null | undefined }) {
  return <ConfigBadge value={status} />;
}

export function AssignmentTypeBadge({ assignmentType }: { assignmentType: RequirementSetAssignmentType | null | undefined }) {
  return <ConfigBadge value={assignmentType} />;
}
