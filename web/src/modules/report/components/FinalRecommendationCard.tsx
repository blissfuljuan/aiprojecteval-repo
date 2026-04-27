import { CheckCircle2, Download, RotateCcw } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

export function FinalRecommendationCard() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Final Recommendation</CardTitle>
        <CardDescription>Summary decision prepared from the static report preview.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-5">
        <div className="grid gap-4 md:grid-cols-2">
          <div>
            <p className="text-sm text-muted-foreground">Recommendation</p>
            <p className="mt-1 text-base font-semibold">Accept with Minor Revisions</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Suggested Action</p>
            <p className="mt-1 text-base font-semibold">Review missing traceability items before final grade release.</p>
          </div>
        </div>
        <div className="flex flex-col gap-2 sm:flex-row sm:flex-wrap">
          <Button type="button">
            <CheckCircle2 className="h-4 w-4" aria-hidden="true" />
            Approve Report
          </Button>
          <Button type="button" variant="outline">
            <RotateCcw className="h-4 w-4" aria-hidden="true" />
            Request Revision
          </Button>
          <Button type="button" variant="outline">
            <Download className="h-4 w-4" aria-hidden="true" />
            Export PDF
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
