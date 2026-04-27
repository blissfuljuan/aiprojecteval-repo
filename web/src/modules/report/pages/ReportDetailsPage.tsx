import { Download } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Separator } from "@/common/ui/shadcn/separator";
import { AIFeedbackPanel } from "@/modules/report/components/AIFeedbackPanel";
import { ComplianceFindingsList } from "@/modules/report/components/ComplianceFindingsList";
import { FinalRecommendationCard } from "@/modules/report/components/FinalRecommendationCard";
import { InstructorRemarksCard } from "@/modules/report/components/InstructorRemarksCard";
import { ReportSummaryCard } from "@/modules/report/components/ReportSummaryCard";

const summaryItems = [
  {
    label: "Overall Score",
    value: "87%",
    description: "Weighted evaluation result",
  },
  {
    label: "Document Completeness",
    value: "92%",
    description: "Required document coverage",
  },
  {
    label: "Compliance Score",
    value: "84%",
    description: "Documentation standard alignment",
  },
  {
    label: "Deployment Readiness",
    value: "Passed",
    description: "Validation outcome",
  },
];

export function ReportDetailsPage() {
  return (
    <div className="flex w-full flex-col gap-6">
      <div className="rounded-lg border bg-card p-6 shadow-sm">
        <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-start">
          <div className="space-y-4">
            <div className="space-y-1">
              <h1 className="text-3xl font-semibold tracking-normal">Final Evaluation Report</h1>
              <p className="text-sm text-muted-foreground">Generated Date: Apr 24, 2026</p>
            </div>
            <Separator />
            <div className="flex flex-col gap-3 text-sm sm:flex-row sm:flex-wrap sm:items-center">
              <span className="text-muted-foreground">
                Project: <span className="font-medium text-foreground">Campus Clinic System</span>
              </span>
              <Badge className="w-fit border-emerald-200 bg-emerald-50 text-emerald-700" variant="outline">
                Completed
              </Badge>
            </div>
          </div>
          <Button type="button" variant="outline">
            <Download className="h-4 w-4" aria-hidden="true" />
            Download PDF
          </Button>
        </div>
      </div>

      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4" aria-label="Report summary">
        {summaryItems.map((item) => (
          <ReportSummaryCard key={item.label} {...item} />
        ))}
      </section>

      <ComplianceFindingsList />
      <AIFeedbackPanel />
      <InstructorRemarksCard />
      <FinalRecommendationCard />
    </div>
  );
}
