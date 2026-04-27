import { AlertTriangle, CheckCircle } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/common/ui/shadcn/table";

type ValidationStatus = "Passed" | "Warning" | "Failed";

const validationLogs: Array<{
  check: string;
  target: string;
  result: string;
  status: ValidationStatus;
  checkedAt: string;
}> = [
  {
    check: "URL Reachability",
    target: "https://student-project.vercel.app",
    result: "Deployment is reachable",
    status: "Passed",
    checkedAt: "Today, 09:30 AM",
  },
  {
    check: "HTTP Status",
    target: "/",
    result: "Returned 200 OK",
    status: "Passed",
    checkedAt: "Today, 09:30 AM",
  },
  {
    check: "Login Endpoint",
    target: "/api/auth/login",
    result: "Endpoint responded successfully",
    status: "Passed",
    checkedAt: "Today, 09:31 AM",
  },
  {
    check: "Projects Endpoint",
    target: "/api/projects",
    result: "Requires authentication",
    status: "Warning",
    checkedAt: "Today, 09:31 AM",
  },
  {
    check: "HTTPS Check",
    target: "https://student-project.vercel.app",
    result: "SSL/TLS enabled",
    status: "Passed",
    checkedAt: "Today, 09:32 AM",
  },
];

const statusClassNames: Record<ValidationStatus, string> = {
  Passed: "border-emerald-200 bg-emerald-50 text-emerald-700",
  Warning: "border-amber-200 bg-amber-50 text-amber-700",
  Failed: "border-red-200 bg-red-50 text-red-700",
};

export function ValidationLogsTable() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Validation Logs</CardTitle>
        <CardDescription>Recent validation checks for the submitted deployment.</CardDescription>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Check</TableHead>
              <TableHead>Target</TableHead>
              <TableHead>Result</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Checked At</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {validationLogs.map((log) => {
              const StatusIcon = log.status === "Warning" ? AlertTriangle : CheckCircle;

              return (
                <TableRow key={`${log.check}-${log.checkedAt}`}>
                  <TableCell className="font-medium">{log.check}</TableCell>
                  <TableCell className="min-w-48 font-mono text-xs text-muted-foreground">{log.target}</TableCell>
                  <TableCell>{log.result}</TableCell>
                  <TableCell>
                    <Badge variant="outline" className={statusClassNames[log.status]}>
                      <StatusIcon className="mr-1 h-3 w-3" aria-hidden="true" />
                      {log.status}
                    </Badge>
                  </TableCell>
                  <TableCell className="whitespace-nowrap text-muted-foreground">{log.checkedAt}</TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
