import { Bot, ClipboardCheck } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Progress } from "@/common/ui/shadcn/progress";
import { Separator } from "@/common/ui/shadcn/separator";

export function SubmissionEvaluationPreview() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2 text-lg">
          <ClipboardCheck className="h-5 w-5" aria-hidden="true" />
          Evaluation Preview
        </CardTitle>
        <CardDescription>Readiness snapshot before AI compliance analysis.</CardDescription>
      </CardHeader>
      <CardContent className="grid gap-5">
        <div className="space-y-2">
          <div className="flex items-center justify-between text-sm">
            <span className="font-medium">Completeness Score</span>
            <span className="text-muted-foreground">92%</span>
          </div>
          <Progress value={92} />
        </div>
        <Separator />
        <div className="grid gap-3 text-sm">
          <div className="flex items-center justify-between gap-3">
            <span className="flex items-center gap-2 text-muted-foreground">
              <Bot className="h-4 w-4" aria-hidden="true" />
              AI Analysis
            </span>
            <Badge variant="secondary">Pending</Badge>
          </div>
          <div className="flex items-center justify-between gap-3">
            <span className="text-muted-foreground">Compliance Status</span>
            <Badge variant="outline">Not started</Badge>
          </div>
        </div>
        <Button type="button" variant="outline" className="w-full">
          View Evaluation
        </Button>
      </CardContent>
    </Card>
  );
}
