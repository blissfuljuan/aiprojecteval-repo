import { FileCheck2, FileText, Play, ScrollText, UploadCloud } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/common/ui/shadcn/tabs";
import { ProjectStatusBadge } from "@/modules/project/components/ProjectStatusBadge";
import { ProjectSummaryCard } from "@/modules/project/components/ProjectSummaryCard";

const summaryCards = [
  { title: "Submissions", value: "3", description: "Milestone packages", icon: UploadCloud },
  { title: "Documents", value: "4", description: "Required artifacts", icon: FileText },
  { title: "Evaluations", value: "2", description: "Completed checks", icon: FileCheck2 },
  { title: "Reports", value: "1", description: "Generated summary", icon: ScrollText },
];

const project = {
  title: "AI Attendance Monitoring System",
  courseCode: "CAP101",
  section: "MCS-1A",
  status: "Active" as const,
  description:
    "A software project focused on attendance tracking, class monitoring, and documentation compliance for academic evaluation.",
  semester: "First Semester",
  schoolYear: "2025-2026",
  instructor: "Dr. Ana Reyes",
  createdDate: "Jan 12, 2026",
  lastUpdated: "Apr 24, 2026",
};

export function ProjectDetailsPage() {
  return (
    <div className="flex w-full flex-col gap-6">
      <Card>
        <CardContent className="flex flex-col justify-between gap-5 p-6 lg:flex-row lg:items-start">
          <div className="min-w-0 space-y-3">
            <div className="flex flex-wrap items-center gap-3">
              <h1 className="text-3xl font-semibold tracking-normal">{project.title}</h1>
              <ProjectStatusBadge status={project.status} />
            </div>
            <p className="text-sm text-muted-foreground">
              {project.courseCode} - {project.section}
            </p>
          </div>
          <div className="flex flex-wrap gap-3">
            <Button variant="outline" type="button">
              Edit Project
            </Button>
            <Button type="button">
              <Play className="h-4 w-4" aria-hidden="true" />
              Start Evaluation
            </Button>
          </div>
        </CardContent>
      </Card>

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {summaryCards.map((card) => (
          <ProjectSummaryCard key={card.title} {...card} />
        ))}
      </div>

      <Tabs defaultValue="overview">
        <div className="overflow-x-auto">
          <TabsList className="min-w-max">
            <TabsTrigger value="overview">Overview</TabsTrigger>
            <TabsTrigger value="submissions">Submissions</TabsTrigger>
            <TabsTrigger value="documents">Documents</TabsTrigger>
            <TabsTrigger value="evaluations">Evaluations</TabsTrigger>
            <TabsTrigger value="reports">Reports</TabsTrigger>
          </TabsList>
        </div>

        <TabsContent value="overview">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Project Overview</CardTitle>
            </CardHeader>
            <CardContent className="space-y-5">
              <p className="max-w-3xl text-sm leading-6 text-muted-foreground">{project.description}</p>
              <Separator />
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                <InfoItem label="Course" value={project.courseCode} />
                <InfoItem label="Section" value={project.section} />
                <InfoItem label="Semester" value={project.semester} />
                <InfoItem label="School Year" value={project.schoolYear} />
                <InfoItem label="Instructor" value={project.instructor} />
                <InfoItem label="Created Date" value={project.createdDate} />
                <InfoItem label="Last Updated" value={project.lastUpdated} />
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="submissions">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Submissions</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-3">
              {["Initial proposal package", "Requirements documentation", "Design review materials"].map((item) => (
                <ListItem key={item} title={item} description="Static submission placeholder for layout preview." />
              ))}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="documents">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Documents</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
              {["SRS", "SDD", "SPMP", "STD"].map((document) => (
                <div key={document} className="space-y-3 rounded-md border p-4">
                  <div className="flex items-center justify-between gap-3">
                    <p className="font-medium">{document}</p>
                    <Badge variant="secondary">Placeholder</Badge>
                  </div>
                  <p className="text-sm text-muted-foreground">Document compliance preview item.</p>
                </div>
              ))}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="evaluations">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Evaluation Scores</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4 md:grid-cols-3">
              <ScoreCard title="Documentation Compliance" score="82%" />
              <ScoreCard title="Repository Readiness" score="76%" />
              <ScoreCard title="Deployment Validation" score="68%" />
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="reports">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Generated Reports</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-3">
              <ListItem title="Project compliance summary" description="Generated Apr 24, 2026 - static report placeholder." />
              <ListItem title="Documentation gap analysis" description="Generated Apr 20, 2026 - static report placeholder." />
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}

function InfoItem({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-md border bg-muted/30 p-4">
      <p className="text-xs font-medium uppercase tracking-normal text-muted-foreground">{label}</p>
      <p className="mt-1 text-sm font-medium">{value}</p>
    </div>
  );
}

function ListItem({ title, description }: { title: string; description: string }) {
  return (
    <div className="flex flex-col justify-between gap-2 rounded-md border p-4 sm:flex-row sm:items-center">
      <div className="space-y-1">
        <p className="font-medium">{title}</p>
        <p className="text-sm text-muted-foreground">{description}</p>
      </div>
      <Badge variant="outline">Static</Badge>
    </div>
  );
}

function ScoreCard({ title, score }: { title: string; score: string }) {
  return (
    <div className="rounded-md border p-4">
      <p className="text-sm font-medium text-muted-foreground">{title}</p>
      <p className="mt-3 text-3xl font-semibold tracking-normal">{score}</p>
      <p className="mt-1 text-xs text-muted-foreground">Placeholder score for UI preview.</p>
    </div>
  );
}
