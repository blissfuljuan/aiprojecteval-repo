import { BarChart3, ClipboardList, FileText, FolderKanban } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { EvaluationStatusCard } from "@/modules/dashboard/components/EvaluationStatusCard";
import { MetricCard } from "@/modules/dashboard/components/MetricCard";
import { ProjectStatusCard } from "@/modules/dashboard/components/ProjectStatusCard";
import { RecentActivityCard } from "@/modules/dashboard/components/RecentActivityCard";
import { RecentSubmissionsTable } from "@/modules/dashboard/components/RecentSubmissionsTable";
import { UpcomingDeadlinesCard } from "@/modules/dashboard/components/UpcomingDeadlinesCard";

export function DashboardPage() {
  return (
    <div className="space-y-6 p-4 md:p-6">
      <section className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Dashboard</h1>
          <p className="text-sm text-muted-foreground">
            Overview of project evaluations, submissions, and compliance status
          </p>
        </div>
        <div className="flex flex-col gap-2 sm:flex-row">
          <Button variant="outline">View Reports</Button>
          <Button>New Project</Button>
        </div>
      </section>

      <section className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
        <MetricCard
          title="Total Projects"
          value="24"
          description="Projects currently tracked"
          icon={<FolderKanban className="h-4 w-4" aria-hidden="true" />}
        />
        <MetricCard
          title="Total Submissions"
          value="68"
          description="Documents and deployments submitted"
          icon={<ClipboardList className="h-4 w-4" aria-hidden="true" />}
        />
        <MetricCard
          title="Pending Evaluations"
          value="12"
          description="Awaiting review or analysis"
          icon={<BarChart3 className="h-4 w-4" aria-hidden="true" />}
        />
        <MetricCard
          title="Completed Reports"
          value="35"
          description="Reports generated for review"
          icon={<FileText className="h-4 w-4" aria-hidden="true" />}
        />
      </section>

      <section className="grid grid-cols-1 gap-6 md:grid-cols-2">
        <ProjectStatusCard />
        <EvaluationStatusCard />
      </section>

      <section className="grid grid-cols-1 gap-6 md:grid-cols-2">
        <RecentActivityCard />
        <UpcomingDeadlinesCard />
      </section>

      <RecentSubmissionsTable />
    </div>
  );
}
