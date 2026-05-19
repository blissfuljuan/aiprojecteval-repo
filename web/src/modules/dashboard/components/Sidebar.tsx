import { NavLink, useLocation } from "react-router";
import {
  BarChart3,
  BookOpenCheck,
  ChevronDown,
  ClipboardCheck,
  ClipboardList,
  FileText,
  FolderKanban,
  Gauge,
  GitBranch,
  ListChecks,
  NotebookTabs,
  PenLine,
  Rocket,
  Settings,
  UploadCloud,
} from "lucide-react";
import type { ComponentType } from "react";
import { hasAnyRole } from "@/common/lib/auth";
import { studentModuleRoles, unrestrictedRoles } from "@/common/lib/roleAccess";
import { cn } from "@/common/lib/utils";
import {
  documentEvaluationEvaluatorRoles,
  documentEvaluationInstructorRoles,
  documentEvaluationStudentOnlyRoles,
} from "@/modules/document-evaluation/constants";
import { useAuth } from "@/modules/identity/context/AuthContext";
import type { Role } from "@/modules/identity/types";
import { paths } from "@/routes/paths";

type NavIcon = ComponentType<{ className?: string; "aria-hidden"?: boolean | "true" | "false" }>;

type NavItem = {
  label: string;
  to: string;
  icon: NavIcon;
  end?: boolean;
  roles: Role[];
};

type NavGroup = {
  label: string;
  icon: NavIcon;
  pathRoot: string;
  items: NavItem[];
};

type SidebarItem = NavItem | NavGroup;

function isNavGroup(item: SidebarItem): item is NavGroup {
  return "items" in item;
}

const navItems: SidebarItem[] = [
  { label: "Dashboard", to: paths.dashboard, icon: Gauge, end: true, roles: unrestrictedRoles },
  { label: "Projects", to: paths.projects, icon: FolderKanban, roles: studentModuleRoles },
  { label: "Proposals", to: paths.proposals, icon: ClipboardList, roles: studentModuleRoles },
  { label: "Submissions", to: paths.submissions, icon: UploadCloud, roles: studentModuleRoles },
  { label: "Documents", to: paths.documents, icon: FileText, roles: unrestrictedRoles },
  { label: "Evaluations", to: paths.evaluations, icon: ClipboardCheck, roles: unrestrictedRoles },
  {
    label: "Document Evaluation",
    icon: BookOpenCheck,
    pathRoot: paths.documentEvaluation,
    items: [
      {
        label: "Overview",
        to: paths.documentEvaluation,
        icon: BookOpenCheck,
        end: true,
        roles: studentModuleRoles,
      },
      {
        label: "Presets",
        to: paths.documentEvaluationPresets,
        icon: NotebookTabs,
        roles: documentEvaluationInstructorRoles,
      },
      {
        label: "Requirement Sets",
        to: paths.documentEvaluationRequirementSets,
        icon: ListChecks,
        roles: documentEvaluationInstructorRoles,
      },
      {
        label: "Assignments",
        to: paths.documentEvaluationAssignments,
        icon: ClipboardList,
        roles: documentEvaluationInstructorRoles,
      },
      {
        label: "Submissions",
        to: paths.documentEvaluationSubmissions,
        icon: UploadCloud,
        roles: ["ADMIN", "INSTRUCTOR", "STUDENT"],
      },
      {
        label: "Completeness",
        to: paths.documentEvaluationCompleteness,
        icon: ClipboardCheck,
        roles: documentEvaluationInstructorRoles,
      },
      {
        label: "Manual Evaluations",
        to: paths.documentEvaluationEvaluations,
        icon: PenLine,
        roles: documentEvaluationEvaluatorRoles,
      },
      {
        label: "My Results",
        to: paths.documentEvaluationMyResults,
        icon: BarChart3,
        roles: documentEvaluationStudentOnlyRoles,
      },
    ],
  },
  { label: "Repository Analysis", to: paths.repositoryAnalysis, icon: GitBranch, roles: studentModuleRoles },
  { label: "Deployment Validation", to: paths.deploymentValidation, icon: Rocket, roles: studentModuleRoles },
  { label: "Reports", to: paths.reports, icon: BarChart3, roles: unrestrictedRoles },
  { label: "Settings", to: paths.settings, icon: Settings, roles: unrestrictedRoles },
];

export function Sidebar() {
  const { user } = useAuth();
  const location = useLocation();

  return (
    <aside className="min-h-[calc(100vh-4rem)] w-[260px] shrink-0 border-r bg-background">
      <nav className="space-y-1 p-3">
        {navItems.map((item) => {
          if (isNavGroup(item)) {
            const visibleGroupItems = item.items.filter((groupItem) => hasAnyRole(user, groupItem.roles));
            const isGroupActive =
              location.pathname === item.pathRoot || location.pathname.startsWith(`${item.pathRoot}/`);

            if (visibleGroupItems.length === 0) return null;

            return (
              <details key={item.label} className="group">
                <summary
                  className={cn(
                    "flex cursor-pointer list-none items-center justify-between gap-3 rounded-md px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground [&::-webkit-details-marker]:hidden",
                    isGroupActive && "bg-accent text-accent-foreground",
                  )}
                >
                  <span className="flex min-w-0 items-center gap-3">
                    <item.icon className="h-4 w-4 shrink-0" aria-hidden="true" />
                    <span>{item.label}</span>
                  </span>
                  <ChevronDown
                    className="h-4 w-4 shrink-0 transition-transform group-open:rotate-180"
                    aria-hidden="true"
                  />
                </summary>
                <div className="mt-1 space-y-1 pl-4">
                  {visibleGroupItems.map((groupItem) => (
                    <NavLink
                      key={groupItem.to}
                      to={groupItem.to}
                      end={groupItem.end}
                      className={({ isActive }) =>
                        cn(
                          "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground",
                          isActive && "bg-accent text-accent-foreground",
                        )
                      }
                    >
                      <groupItem.icon className="h-4 w-4" aria-hidden="true" />
                      <span>{groupItem.label}</span>
                    </NavLink>
                  ))}
                </div>
              </details>
            );
          }

          if (!hasAnyRole(user, item.roles)) return null;

          return (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) =>
                cn(
                  "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground",
                  isActive && "bg-accent text-accent-foreground",
                )
              }
            >
              <item.icon className="h-4 w-4" aria-hidden="true" />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </nav>
    </aside>
  );
}
