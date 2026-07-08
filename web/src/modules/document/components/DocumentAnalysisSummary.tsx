import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Progress } from "@/common/ui/shadcn/progress";
import { Separator } from "@/common/ui/shadcn/separator";
import type { AnalysisResult, GenericDocument } from "@/modules/document/types";

type DocumentAnalysisSummaryProps = {
  analysis: AnalysisResult;
  document: GenericDocument;
};

export function DocumentAnalysisSummary({ analysis, document }: DocumentAnalysisSummaryProps) {
  const version = document.currentVersion;
  const structureStatus = analysis.structuralAccuracy >= 85 ? "Passed with minor notes" : "Needs reviewer attention";
  const aiStatus = version?.extractionStatus === "EXTRACTED" ? "Ready for AI evaluation" : "Text extraction required";

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Analysis Summary</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-2">
          <div className="flex items-center justify-between text-sm">
            <span className="text-muted-foreground">Completeness Score</span>
            <span className="font-medium">{analysis.completenessScore}%</span>
          </div>
          <Progress value={analysis.completenessScore} />
        </div>
        <Separator />
        <div className="grid gap-3">
          <div className="flex items-center justify-between gap-4">
            <span className="text-sm text-muted-foreground">Structure Check</span>
            <span className="text-right text-sm font-medium">{structureStatus}</span>
          </div>
          <div className="flex items-center justify-between gap-4">
            <span className="text-sm text-muted-foreground">AI Analysis Status</span>
            <span className="text-right text-sm font-medium">{aiStatus}</span>
          </div>
          <div className="flex items-center justify-between gap-4">
            <span className="text-sm text-muted-foreground">Last Extracted</span>
            <span className="text-right text-sm font-medium">
              {version?.extractedAt ? new Date(version.extractedAt).toLocaleDateString() : "Not yet extracted"}
            </span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
