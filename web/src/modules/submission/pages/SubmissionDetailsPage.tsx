import { Edit3, ExternalLink, MoreHorizontal, PlayCircle } from "lucide-react";
import { Alert, AlertDescription, AlertTitle } from "@/common/ui/shadcn/alert";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/common/ui/shadcn/dropdown-menu";
import { Separator } from "@/common/ui/shadcn/separator";
import { SubmissionDocumentList } from "@/modules/submission/components/SubmissionDocumentList";
import { SubmissionEvaluationPreview } from "@/modules/submission/components/SubmissionEvaluationPreview";
import { SubmissionStatusBadge } from "@/modules/submission/components/SubmissionStatusBadge";
import { SubmissionSummaryCards } from "@/modules/submission/components/SubmissionSummaryCards";

export function SubmissionDetailsPage() {
  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-start">
        <div className="space-y-2">
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="text-3xl font-semibold tracking-normal">Campus Clinic Appointment System</h1>
            <SubmissionStatusBadge status="Submitted" />
          </div>
          <p className="text-sm text-muted-foreground">Submission overview and evaluation readiness.</p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button type="button" variant="outline">
            <Edit3 className="h-4 w-4" aria-hidden="true" />
            Edit Submission
          </Button>
          <Button type="button">
            <PlayCircle className="h-4 w-4" aria-hidden="true" />
            Trigger Evaluation
          </Button>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button type="button" variant="outline" size="icon" aria-label="More actions">
                <MoreHorizontal className="h-4 w-4" aria-hidden="true" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem>Download summary</DropdownMenuItem>
              <DropdownMenuItem>Archive submission</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      <SubmissionSummaryCards />

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_22rem]">
        <div className="grid min-w-0 gap-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Submission Details</CardTitle>
              <CardDescription>Static metadata for the submitted evaluation package.</CardDescription>
            </CardHeader>
            <CardContent className="grid gap-3 text-sm md:grid-cols-2">
              <DetailRow label="Project" value="Campus Clinic Appointment System" />
              <DetailRow label="Submission Status" value="Submitted" />
              <DetailRow label="Documents Uploaded" value="4/4" />
              <DetailRow label="Evaluation Status" value="Pending Review" />
            </CardContent>
          </Card>

          <SubmissionDocumentList />

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Repository Information</CardTitle>
              <CardDescription>Repository connection preview.</CardDescription>
            </CardHeader>
            <CardContent className="grid gap-3 text-sm">
              <DetailRow label="Status" value="Connected" badge />
              <DetailLink label="GitHub URL" value="https://github.com/sample/campus-clinic" />
              <DetailRow label="Branch" value="main" />
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Deployment Information</CardTitle>
              <CardDescription>Deployment readiness preview.</CardDescription>
            </CardHeader>
            <CardContent className="grid gap-3 text-sm">
              <DetailRow label="Status" value="Reachable" badge />
              <DetailLink label="Deployment URL" value="https://campus-clinic-demo.vercel.app" />
              <DetailRow label="API Base URL" value="Not provided" />
            </CardContent>
          </Card>

          <div className="flex justify-end rounded-lg border bg-card p-4">
            <Button type="button" variant="outline">
              Back to Submissions
            </Button>
          </div>
        </div>

        <aside className="grid h-fit gap-4">
          <SubmissionEvaluationPreview />

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Timeline</CardTitle>
              <CardDescription>Static submission activity.</CardDescription>
            </CardHeader>
            <CardContent className="grid gap-4 text-sm">
              {["Submission created", "Documents uploaded", "Repository linked", "Submitted for review"].map((item, index) => (
                <div key={item} className="flex gap-3">
                  <div className="mt-1 h-2 w-2 rounded-full bg-primary" />
                  <div>
                    <p className="font-medium">{item}</p>
                    <p className="text-muted-foreground">Apr {22 + index}, 2026</p>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Instructor Remarks</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4">
              <Alert>
                <AlertTitle>No remarks yet</AlertTitle>
                <AlertDescription>Instructor feedback will appear here after review.</AlertDescription>
              </Alert>
              <Separator />
              <Badge variant="outline" className="w-fit">
                Pending Review
              </Badge>
            </CardContent>
          </Card>
        </aside>
      </div>
    </div>
  );
}

interface DetailRowProps {
  label: string;
  value: string;
  badge?: boolean;
}

function DetailRow({ label, value, badge }: DetailRowProps) {
  return (
    <div className="flex items-center justify-between gap-4 rounded-md border bg-background p-3">
      <span className="text-muted-foreground">{label}</span>
      {badge ? (
        <Badge variant="outline" className="border-emerald-200 bg-emerald-50 text-emerald-700">
          {value}
        </Badge>
      ) : (
        <span className="text-right font-medium">{value}</span>
      )}
    </div>
  );
}

function DetailLink({ label, value }: DetailRowProps) {
  return (
    <div className="flex items-center justify-between gap-4 rounded-md border bg-background p-3">
      <span className="text-muted-foreground">{label}</span>
      <span className="flex min-w-0 items-center gap-1 text-right font-medium text-primary">
        <span className="truncate">{value}</span>
        <ExternalLink className="h-3.5 w-3.5 shrink-0" aria-hidden="true" />
      </span>
    </div>
  );
}
