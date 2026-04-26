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

const navItems = [
  { label: "Dashboard", to: "/dashboard", icon: Gauge },
  { label: "Projects", to: "/projects", icon: FolderKanban },
  { label: "Submissions", to: "/submissions", icon: UploadCloud },
  { label: "Documents", to: "/documents", icon: FileText },
  { label: "Evaluations", to: "/evaluations", icon: ClipboardCheck },
  { label: "Repository Analysis", to: "/repository-analysis", icon: GitBranch },
  { label: "Deployment Validation", to: "/deployment-validation", icon: Rocket },
  { label: "Reports", to: "/reports", icon: BarChart3 },
  { label: "Settings", to: "/settings", icon: Settings },
];

export function Sidebar() {
  return (
    <aside className="min-h-[calc(100vh-4rem)] w-[260px] shrink-0 border-r bg-background">
      <nav className="space-y-1 p-3">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
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
