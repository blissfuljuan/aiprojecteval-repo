import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

type ReportSummaryCardProps = {
  label: string;
  value: string;
  description: string;
};

export function ReportSummaryCard({ label, value, description }: ReportSummaryCardProps) {
  return (
    <Card>
      <CardHeader className="pb-2">
        <CardTitle className="text-sm font-medium text-muted-foreground">{label}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-semibold">{value}</div>
        <p className="mt-1 text-sm text-muted-foreground">{description}</p>
      </CardContent>
    </Card>
  );
}
