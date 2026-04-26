import { Badge } from "@/common/ui/shadcn/badge";
import { cn } from "@/common/lib/utils";

export type ProjectStatus = "Draft" | "Active" | "Under Review" | "Completed" | "Archived";

const statusStyles: Record<ProjectStatus, string> = {
  Draft: "border-slate-200 bg-slate-50 text-slate-700",
  Active: "border-emerald-200 bg-emerald-50 text-emerald-700",
  "Under Review": "border-amber-200 bg-amber-50 text-amber-700",
  Completed: "border-blue-200 bg-blue-50 text-blue-700",
  Archived: "border-zinc-200 bg-zinc-100 text-zinc-600",
};

type ProjectStatusBadgeProps = {
  status: ProjectStatus;
  className?: string;
};

export function ProjectStatusBadge({ status, className }: ProjectStatusBadgeProps) {
  return (
    <Badge variant="outline" className={cn("whitespace-nowrap", statusStyles[status], className)}>
      {status}
    </Badge>
  );
}
