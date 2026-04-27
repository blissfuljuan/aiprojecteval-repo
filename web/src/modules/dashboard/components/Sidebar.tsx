import { NavLink } from "react-router";
import {
  BarChart3,
  ClipboardCheck,
  FileText,
  FolderKanban,
  Gauge,
  GitBranch,
  Rocket,
  Settings,
  UploadCloud,
} from "lucide-react";
import { cn } from "@/common/lib/utils";
import { paths } from "@/routes/paths";

const navItems = [
  { label: "Dashboard", to: paths.dashboard, icon: Gauge, end: true },
  { label: "Projects", to: paths.projects, icon: FolderKanban },
  { label: "Submissions", to: paths.submissions, icon: UploadCloud },
  { label: "Documents", to: paths.documents, icon: FileText },
  { label: "Evaluations", to: paths.evaluations, icon: ClipboardCheck },
  { label: "Repository Analysis", to: paths.repositoryAnalysis, icon: GitBranch },
  { label: "Deployment Validation", to: paths.deploymentValidation, icon: Rocket },
  { label: "Reports", to: paths.reports, icon: BarChart3 },
  { label: "Settings", to: paths.settings, icon: Settings },
];

export function Sidebar() {
  return (
    <aside className="min-h-[calc(100vh-4rem)] w-[260px] shrink-0 border-r bg-background">
      <nav className="space-y-1 p-3">
        {navItems.map((item) => (
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
