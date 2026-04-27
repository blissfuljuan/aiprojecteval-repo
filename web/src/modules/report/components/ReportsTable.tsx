import { Link } from "react-router";
import { Download, Eye } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/common/ui/shadcn/table";

const reports = [
  {
    id: "1",
    name: "SRS Compliance Report",
    project: "Campus Clinic System",
    type: "Compliance Report",
    generatedAt: "Apr 24, 2026",
  },
  {
    id: "2",
    name: "Final Evaluation Report",
    project: "Expense Tracker App",
    type: "Evaluation Report",
    generatedAt: "Apr 22, 2026",
  },
  {
    id: "3",
    name: "Repository Analysis Report",
    project: "Voting System",
    type: "Repository Analysis Report",
    generatedAt: "Apr 20, 2026",
  },
  {
    id: "4",
    name: "Deployment Validation Report",
    project: "Booking System",
    type: "Deployment Validation Report",
    generatedAt: "Apr 18, 2026",
  },
];

export function ReportsTable() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Generated Reports</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Report Name</TableHead>
                <TableHead>Project</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Generated At</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {reports.map((report) => (
                <TableRow key={report.id}>
                  <TableCell className="min-w-[220px] font-medium">{report.name}</TableCell>
                  <TableCell className="min-w-[180px]">{report.project}</TableCell>
                  <TableCell className="min-w-[180px]">
                    <Badge variant="secondary">{report.type}</Badge>
                  </TableCell>
                  <TableCell className="min-w-[140px]">{report.generatedAt}</TableCell>
                  <TableCell>
                    <div className="flex justify-end gap-2">
                      <Button asChild variant="ghost" size="sm">
                        <Link to={`/reports/${report.id}`}>
                          <Eye className="h-4 w-4" aria-hidden="true" />
                          View
                        </Link>
                      </Button>
                      <Button variant="ghost" size="sm" type="button">
                        <Download className="h-4 w-4" aria-hidden="true" />
                        Download
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      </CardContent>
    </Card>
  );
}
