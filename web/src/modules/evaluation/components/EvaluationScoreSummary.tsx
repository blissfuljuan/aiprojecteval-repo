import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Progress } from "@/common/ui/shadcn/progress";

const scores = [
  { label: "Document Completeness", value: 88 },
  { label: "Requirement Compliance", value: 82 },
  { label: "Repository Evidence", value: 79 },
  { label: "Deployment Readiness", value: 91 },
];

export function EvaluationScoreSummary() {
  return (
    <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
      {scores.map((score) => (
        <Card key={score.label}>
          <CardHeader className="pb-3">
            <CardTitle className="text-sm font-medium text-muted-foreground">{score.label}</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="text-2xl font-semibold">{score.value}%</div>
            <Progress value={score.value} />
          </CardContent>
        </Card>
      ))}
    </section>
  );
}
