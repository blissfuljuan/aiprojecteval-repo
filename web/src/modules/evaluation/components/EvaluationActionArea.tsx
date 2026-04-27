import { CheckCircle2, FileText, RotateCcw } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

export function EvaluationActionArea() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Evaluation Actions</CardTitle>
        <CardDescription>Static action controls for the evaluation workflow shell.</CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col gap-3 sm:flex-row">
        <Button type="button">
          <CheckCircle2 className="h-4 w-4" aria-hidden="true" />
          Approve
        </Button>
        <Button type="button" variant="outline">
          <RotateCcw className="h-4 w-4" aria-hidden="true" />
          Request Revision
        </Button>
        <Button type="button" variant="secondary">
          <FileText className="h-4 w-4" aria-hidden="true" />
          Generate Report
        </Button>
      </CardContent>
    </Card>
  );
}
