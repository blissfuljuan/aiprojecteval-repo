import { AlertCircle, ExternalLink } from "lucide-react";
import { Alert, AlertDescription, AlertTitle } from "@/common/ui/shadcn/alert";
import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";

const documentChecks = ["SRS", "SDD", "SPMP", "STD"];

export function SubmissionReviewStep() {
  return (
    <div className="grid gap-4">
      <Card className="shadow-none">
        <CardHeader>
          <CardTitle className="text-base">Project Summary</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-3 text-sm">
          <div className="flex justify-between gap-4">
            <span className="text-muted-foreground">Project</span>
            <span className="text-right font-medium">Campus Clinic Appointment System</span>
          </div>
          <div className="flex justify-between gap-4">
            <span className="text-muted-foreground">Submission Type</span>
            <Badge variant="secondary">Initial Evaluation</Badge>
          </div>
        </CardContent>
      </Card>
      <Card className="shadow-none">
        <CardHeader>
          <CardTitle className="text-base">Documents Checklist</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-3">
          {documentChecks.map((document) => (
            <div key={document} className="flex items-center justify-between gap-3 text-sm">
              <span>{document}</span>
              <Badge variant="outline" className="border-emerald-200 bg-emerald-50 text-emerald-700">
                Ready
              </Badge>
            </div>
          ))}
        </CardContent>
      </Card>
      <Card className="shadow-none">
        <CardContent className="grid gap-3 p-6 text-sm">
          <div className="flex items-center justify-between gap-4">
            <span className="text-muted-foreground">Repository</span>
            <span className="flex items-center gap-1 text-right font-medium">
              github.com/sample/campus-clinic
              <ExternalLink className="h-3.5 w-3.5" aria-hidden="true" />
            </span>
          </div>
          <Separator />
          <div className="flex items-center justify-between gap-4">
            <span className="text-muted-foreground">Deployment</span>
            <span className="flex items-center gap-1 text-right font-medium">
              campus-clinic-demo.vercel.app
              <ExternalLink className="h-3.5 w-3.5" aria-hidden="true" />
            </span>
          </div>
        </CardContent>
      </Card>
      <Alert className="border-amber-200 bg-amber-50 text-amber-900">
        <AlertCircle className="h-4 w-4" aria-hidden="true" />
        <AlertTitle>Final reminder</AlertTitle>
        <AlertDescription>Confirm that all uploaded files match the current project version before submitting.</AlertDescription>
      </Alert>
    </div>
  );
}
