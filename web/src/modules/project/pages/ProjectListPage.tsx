import { Link, useNavigate } from "react-router";
import { Eye, FolderKanban, Plus, RotateCcw, Search } from "lucide-react";
import { useMemo, useState } from "react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Separator } from "@/common/ui/shadcn/separator";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/common/ui/shadcn/table";
import { useAuth } from "@/modules/identity/context/AuthContext";
import { ProjectStatusBadge } from "@/modules/project/components/ProjectStatusBadge";
import { ProjectSummaryCard } from "@/modules/project/components/ProjectSummaryCard";
import { useProjects } from "@/modules/project/hooks/useProjects";
import type { Project } from "@/modules/project/types";
import { paths } from "@/routes/paths";

export function ProjectListPage() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { projects, isLoading, error } = useProjects();
  const [search, setSearch] = useState("");

  const filteredProjects = useMemo(() => {
    const keyword = search.trim().toLowerCase();
    if (!keyword) return projects;
    return projects.filter((project) =>
      [project.title, project.description, project.ownerEmail, project.repositoryUrl]
        .filter(Boolean)
        .some((value) => value!.toLowerCase().includes(keyword)),
    );
  }, [projects, search]);

  const generatedProjects = projects.filter((project) => project.projectProposalId !== null).length;
  const summaryCards = [
    { title: "Total Projects", value: String(projects.length), description: "Tracked project records", icon: FolderKanban },
    { title: "Approved Proposals", value: String(generatedProjects), description: "Created from approved proposals", icon: FolderKanban },
    { title: "Manual Projects", value: String(projects.length - generatedProjects), description: "Created directly in projects", icon: FolderKanban },
    { title: "Visible To You", value: String(filteredProjects.length), description: "Matching current filters", icon: FolderKanban },
  ];

  const heading = user?.role === "STUDENT" ? "My Projects" : "Projects";

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">{heading}</h1>
          <p className="text-sm text-muted-foreground">
            Manage approved software projects and monitor evaluation readiness.
          </p>
        </div>
        <Button asChild>
          <Link to={paths.projectCreate}>
            <Plus className="h-4 w-4" aria-hidden="true" />
            New Project
          </Link>
        </Button>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {summaryCards.map((card) => (
          <ProjectSummaryCard key={card.title} {...card} />
        ))}
      </div>

      <Card>
        <CardHeader className="pb-4">
          <CardTitle className="text-lg">Project Directory</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-3 md:grid-cols-[minmax(0,1fr)_auto]">
            <div className="relative">
              <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                placeholder="Search projects..."
                className="pl-9"
              />
            </div>
            <Button variant="outline" type="button" onClick={() => setSearch("")}>
              <RotateCcw className="h-4 w-4" aria-hidden="true" />
              Reset
            </Button>
          </div>

          <Separator />

          {error && <p className="text-sm text-destructive">{error}</p>}

          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading projects...</p>
          ) : filteredProjects.length === 0 ? (
            <p className="text-sm text-muted-foreground">No projects found.</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Project Title</TableHead>
                  <TableHead>Source</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Owner</TableHead>
                  <TableHead>Updated At</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredProjects.map((project) => (
                  <ProjectRow key={project.id} project={project} onView={() => navigate(paths.projectDetails(project.id))} />
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

function ProjectRow({ project, onView }: { project: Project; onView: () => void }) {
  return (
    <TableRow>
      <TableCell className="font-medium">{project.title}</TableCell>
      <TableCell>{project.projectProposalId ? "Approved proposal" : "Manual"}</TableCell>
      <TableCell>
        <ProjectStatusBadge status="Active" />
      </TableCell>
      <TableCell>{project.ownerEmail}</TableCell>
      <TableCell>{new Date(project.updatedAt).toLocaleDateString()}</TableCell>
      <TableCell>
        <div className="flex justify-end">
          <Button size="sm" variant="outline" type="button" onClick={onView}>
            <Eye className="h-3.5 w-3.5" aria-hidden="true" />
            View
          </Button>
        </div>
      </TableCell>
    </TableRow>
  );
}
