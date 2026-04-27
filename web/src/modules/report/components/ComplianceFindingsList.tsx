import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Separator } from "@/common/ui/shadcn/separator";

const findings = [
  {
    severity: "Low",
    text: "Missing traceability entry for password reset requirement",
  },
  {
    severity: "Medium",
    text: "SDD architecture section is partially aligned with implementation",
  },
  {
    severity: "High",
    text: "Test cases exist but lack expected output for two features",
  },
];

function severityClassName(severity: string) {
  if (severity === "High") {
    return "border-red-200 bg-red-50 text-red-700";
  }

  if (severity === "Medium") {
    return "border-amber-200 bg-amber-50 text-amber-700";
  }

  return "border-emerald-200 bg-emerald-50 text-emerald-700";
}

export function ComplianceFindingsList() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Compliance Findings</CardTitle>
        <CardDescription>Documentation and implementation alignment items for review.</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {findings.map((finding, index) => (
            <div key={finding.text}>
              <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
                <p className="text-sm font-medium">{finding.text}</p>
                <Badge variant="outline" className={severityClassName(finding.severity)}>
                  {finding.severity}
                </Badge>
              </div>
              {index < findings.length - 1 ? <Separator className="mt-4" /> : null}
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
