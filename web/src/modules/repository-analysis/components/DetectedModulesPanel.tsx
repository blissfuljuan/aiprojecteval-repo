import { Badge } from "@/common/ui/shadcn/badge";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/common/ui/shadcn/table";

const modules = [
  {
    name: "Authentication Module",
    evidence: "Login page, identity service, auth guard",
    status: "Detected",
  },
  {
    name: "Project Management Module",
    evidence: "Project pages, project service, status badge",
    status: "Detected",
  },
  {
    name: "Submission Module",
    evidence: "Submission stepper, upload workflow, submission service",
    status: "Detected",
  },
  {
    name: "Evaluation Module",
    evidence: "Evaluation dashboard, findings tabs, score summary",
    status: "Review",
  },
];

export function DetectedModulesPanel() {
  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Module name</TableHead>
          <TableHead>Evidence found</TableHead>
          <TableHead className="w-[120px]">Status</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {modules.map((module) => (
          <TableRow key={module.name}>
            <TableCell className="font-medium">{module.name}</TableCell>
            <TableCell className="text-muted-foreground">{module.evidence}</TableCell>
            <TableCell>
              <Badge
                variant="outline"
                className={
                  module.status === "Detected"
                    ? "border-emerald-200 bg-emerald-50 text-emerald-700"
                    : "border-amber-200 bg-amber-50 text-amber-700"
                }
              >
                {module.status}
              </Badge>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
