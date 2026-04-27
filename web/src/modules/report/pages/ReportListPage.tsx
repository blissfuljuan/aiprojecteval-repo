import { FileText, Search } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Select } from "@/common/ui/shadcn/select";
import { ReportsTable } from "@/modules/report/components/ReportsTable";

export function ReportListPage() {
  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Reports</h1>
          <p className="text-sm text-muted-foreground">View generated evaluation and compliance reports.</p>
        </div>
        <Button>
          <FileText className="h-4 w-4" aria-hidden="true" />
          Generate Report
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Filters</CardTitle>
          <CardDescription>Narrow the report list by name, type, or generated date.</CardDescription>
        </CardHeader>
        <CardContent className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <div className="relative md:col-span-2 xl:col-span-1">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input placeholder="Search reports..." className="pl-9" />
          </div>
          <Select defaultValue="all-types" aria-label="Report type filter">
            <option value="all-types">All Types</option>
            <option value="evaluation-report">Evaluation Report</option>
            <option value="compliance-report">Compliance Report</option>
            <option value="repository-analysis-report">Repository Analysis Report</option>
            <option value="deployment-validation-report">Deployment Validation Report</option>
          </Select>
          <Input type="text" placeholder="From" aria-label="Generated from date" />
          <Input type="text" placeholder="To" aria-label="Generated to date" />
        </CardContent>
      </Card>

      <ReportsTable />
    </div>
  );
}
