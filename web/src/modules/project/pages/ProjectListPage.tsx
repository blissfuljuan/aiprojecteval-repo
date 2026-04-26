import { Link, useNavigate } from "react-router";
import { Archive, Eye, FolderKanban, Pencil, Plus, RotateCcw, Search } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Select } from "@/common/ui/shadcn/select";
import { Separator } from "@/common/ui/shadcn/separator";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/common/ui/shadcn/table";
import { ProjectStatusBadge, type ProjectStatus } from "@/modules/project/components/ProjectStatusBadge";
import { ProjectSummaryCard } from "@/modules/project/components/ProjectSummaryCard";

const projects: Array<{
  id: string;
  title: string;
  courseCode: string;
  section: string;
  status: ProjectStatus;
  createdBy: string;
  updatedAt: string;
}> = [
  {
    id: "1",
    title: "AI Attendance Monitoring System",
    courseCode: "CAP101",
    section: "MCS-1A",
    status: "Active",
    createdBy: "Dr. Ana Reyes",
    updatedAt: "Apr 24, 2026",
  },
  {
    id: "2",
    title: "Campus Clinic Appointment System",
    courseCode: "SE201",
    section: "MCS-1B",
    status: "Under Review",
    createdBy: "Prof. Mark Santos",
    updatedAt: "Apr 22, 2026",
  },
  {
    id: "3",
    title: "Library Resource Tracker",
    courseCode: "IT302",
    section: "MCS-2A",
    status: "Completed",
    createdBy: "Dr. Liza Cruz",
    updatedAt: "Apr 18, 2026",
  },
  {
    id: "4",
    title: "Smart Classroom Access System",
    courseCode: "CS410",
    section: "MCS-2B",
    status: "Draft",
    createdBy: "Prof. Carlo Lim",
    updatedAt: "Apr 15, 2026",
  },
];

const summaryCards = [
  { title: "Total Projects", value: "4", description: "Tracked project records", icon: FolderKanban },
  { title: "Active Projects", value: "1", description: "Currently in progress", icon: FolderKanban },
  { title: "Under Review", value: "1", description: "Awaiting evaluation checks", icon: FolderKanban },
  { title: "Completed", value: "1", description: "Evaluation cycle finished", icon: FolderKanban },
];

export function ProjectListPage() {
  const navigate = useNavigate();

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Projects</h1>
          <p className="text-sm text-muted-foreground">
            Manage software project records and monitor evaluation readiness.
          </p>
        </div>
        <Button asChild>
          <Link to="/projects/create">
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
          <div className="grid gap-3 md:grid-cols-[minmax(0,1fr)_180px_180px_auto]">
            <div className="relative">
              <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input placeholder="Search projects..." className="pl-9" />
            </div>
            <Select defaultValue="all" aria-label="Filter by status">
              <option value="all">All statuses</option>
              <option value="draft">Draft</option>
              <option value="active">Active</option>
              <option value="under-review">Under Review</option>
              <option value="completed">Completed</option>
              <option value="archived">Archived</option>
            </Select>
            <Select defaultValue="all" aria-label="Filter by course">
              <option value="all">All courses</option>
              <option value="CAP101">CAP101</option>
              <option value="SE201">SE201</option>
              <option value="IT302">IT302</option>
              <option value="CS410">CS410</option>
            </Select>
            <Button variant="outline" type="button">
              <RotateCcw className="h-4 w-4" aria-hidden="true" />
              Reset
            </Button>
          </div>

          <Separator />

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Project Title</TableHead>
                <TableHead>Course Code</TableHead>
                <TableHead>Section</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Created By</TableHead>
                <TableHead>Updated At</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {projects.map((project) => (
                <TableRow key={project.id}>
                  <TableCell className="font-medium">{project.title}</TableCell>
                  <TableCell>{project.courseCode}</TableCell>
                  <TableCell>{project.section}</TableCell>
                  <TableCell>
                    <ProjectStatusBadge status={project.status} />
                  </TableCell>
                  <TableCell>{project.createdBy}</TableCell>
                  <TableCell>{project.updatedAt}</TableCell>
                  <TableCell>
                    <div className="flex justify-end gap-2">
                      <Button size="sm" variant="outline" type="button" onClick={() => navigate("/projects/1")}>
                        <Eye className="h-3.5 w-3.5" aria-hidden="true" />
                        View
                      </Button>
                      <Button size="sm" variant="ghost" type="button">
                        <Pencil className="h-3.5 w-3.5" aria-hidden="true" />
                        Edit
                      </Button>
                      <Button size="sm" variant="ghost" type="button">
                        <Archive className="h-3.5 w-3.5" aria-hidden="true" />
                        Archive
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
