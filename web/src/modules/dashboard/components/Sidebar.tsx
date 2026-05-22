import { NavLink } from "react-router";
import {
  BarChart3,
  BookOpenCheck,
  ClipboardCheck,
  ClipboardList,
  FileText,
  FolderKanban,
  Gauge,
  GraduationCap,
  GitBranch,
  Rocket,
  Settings,
  UploadCloud,
} from "lucide-react";
import type { ComponentType } from "react";
import { hasAnyRole } from "@/common/lib/auth";
import {
  courseClassManagementRoles,
  studentModuleRoles,
  studentOnlyRoles,
  unrestrictedRoles,
} from "@/common/lib/roleAccess";
import { cn } from "@/common/lib/utils";
import { useAuth } from "@/modules/identity/context/AuthContext";
import type { Role } from "@/modules/identity/types";
import { paths } from "@/routes/paths";

const navItems = [
  { label: "Dashboard", to: paths.dashboard, icon: Gauge, end: true, roles: unrestrictedRoles },
  { label: "Course Classes", to: paths.courseClasses, icon: BookOpenCheck, roles: courseClassManagementRoles },
  { label: "My Classes", to: paths.myCourseClasses, icon: GraduationCap, roles: studentOnlyRoles },
  { label: "Projects", to: paths.projects, icon: FolderKanban, roles: studentModuleRoles },
  { label: "Proposals", to: paths.proposals, icon: ClipboardList, roles: studentModuleRoles },
  { label: "Submissions", to: paths.submissions, icon: UploadCloud, roles: studentModuleRoles },
  { label: "Documents", to: paths.documents, icon: FileText, roles: unrestrictedRoles },
  { label: "Evaluations", to: paths.evaluations, icon: ClipboardCheck, roles: unrestrictedRoles },
  { label: "Repository Analysis", to: paths.repositoryAnalysis, icon: GitBranch, roles: studentModuleRoles },
  { label: "Deployment Validation", to: paths.deploymentValidation, icon: Rocket, roles: studentModuleRoles },
  { label: "Reports", to: paths.reports, icon: BarChart3, roles: unrestrictedRoles },
  { label: "Settings", to: paths.settings, icon: Settings, roles: unrestrictedRoles },
] satisfies Array<{
  label: string;
  to: string;
  icon: ComponentType<{ className?: string; "aria-hidden"?: boolean | "true" | "false" }>;
  end?: boolean;
  roles: Role[];
}>;

export function Sidebar() {
  const { user } = useAuth();
  const visibleNavItems = navItems.filter((item) => hasAnyRole(user, item.roles));

  return (
    <aside className="min-h-[calc(100vh-4rem)] w-[260px] shrink-0 border-r bg-background">
      <nav className="space-y-1 p-3">
        {visibleNavItems.map((item) => (
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
        ))}
      </nav>
    </aside>
  );
}
