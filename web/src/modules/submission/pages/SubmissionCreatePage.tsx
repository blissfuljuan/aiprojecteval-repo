import { AlertCircle, CheckCircle2, FileText, HelpCircle } from "lucide-react";
import { Alert, AlertDescription, AlertTitle } from "@/common/ui/shadcn/alert";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";
import { SubmissionBasicInfoStep } from "@/modules/submission/components/SubmissionBasicInfoStep";
import { SubmissionDocumentsStep } from "@/modules/submission/components/SubmissionDocumentsStep";
import { SubmissionRepositoryStep } from "@/modules/submission/components/SubmissionRepositoryStep";
import { SubmissionReviewStep } from "@/modules/submission/components/SubmissionReviewStep";
import { SubmissionStepCard } from "@/modules/submission/components/SubmissionStepCard";
import { SubmissionStepper } from "@/modules/submission/components/SubmissionStepper";

export function SubmissionCreatePage() {
  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Create Submission</h1>
          <p className="text-sm text-muted-foreground">
            Submit project documents, repository link, and deployment URL for evaluation.
          </p>
        </div>
        <Button type="button" variant="outline">
          Save Draft
        </Button>
      </div>

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_22rem]">
        <div className="grid min-w-0 gap-6">
          <SubmissionStepper />
          <SubmissionStepCard title="Step 1: Basic Information" description="Provide the project and submission context.">
            <SubmissionBasicInfoStep />
          </SubmissionStepCard>
          <SubmissionStepCard
            title="Step 2: Required Documents"
            description="Static upload placeholders for required compliance documents."
          >
            <SubmissionDocumentsStep />
          </SubmissionStepCard>
          <SubmissionStepCard
            title="Step 3: Repository & Deployment"
            description="Add repository and deployment references for future validation."
          >
            <SubmissionRepositoryStep />
          </SubmissionStepCard>
          <SubmissionStepCard title="Step 4: Review Submission" description="Preview the package before final submission.">
            <SubmissionReviewStep />
          </SubmissionStepCard>

          <div className="flex flex-col-reverse justify-between gap-3 rounded-lg border bg-card p-4 sm:flex-row sm:items-center">
            <Button type="button" variant="outline">
              Back
            </Button>
            <div className="flex flex-col gap-3 sm:flex-row">
              <Button type="button" variant="secondary">
                Next
              </Button>
              <Button type="button">
                <AlertCircle className="h-4 w-4" aria-hidden="true" />
                Submit
              </Button>
            </div>
          </div>
        </div>

        <aside className="grid h-fit gap-4">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-lg">
                <CheckCircle2 className="h-5 w-5" aria-hidden="true" />
                Submission Checklist
              </CardTitle>
              <CardDescription>Static readiness markers for the submission package.</CardDescription>
            </CardHeader>
            <CardContent className="grid gap-3 text-sm">
              {["Project selected", "Four documents prepared", "Repository link added", "Deployment URL added"].map((item) => (
                <div key={item} className="flex items-center justify-between gap-3">
                  <span>{item}</span>
                  <Badge variant="secondary">Pending</Badge>
                </div>
              ))}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-lg">
                <FileText className="h-5 w-5" aria-hidden="true" />
                Submission Guidelines
              </CardTitle>
            </CardHeader>
            <CardContent className="grid gap-3 text-sm text-muted-foreground">
              <p>Use the current project documentation versions approved by the group adviser.</p>
              <Separator />
              <p>Repository and deployment fields are placeholders until backend validation is connected.</p>
            </CardContent>
          </Card>

          <Alert className="border-blue-200 bg-blue-50 text-blue-900">
            <HelpCircle className="h-4 w-4" aria-hidden="true" />
            <AlertTitle>Help / Reminder</AlertTitle>
            <AlertDescription>Review file naming conventions before uploading final documents.</AlertDescription>
          </Alert>
        </aside>
      </div>
    </div>
  );
}
