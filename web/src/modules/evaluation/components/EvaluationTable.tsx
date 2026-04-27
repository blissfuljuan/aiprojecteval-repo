import { Link } from "react-router";
import { Eye } from "lucide-react";
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
import { EvaluationStatusBadge, type EvaluationStatus } from "@/modules/evaluation/components/EvaluationStatusBadge";

const evaluations: Array<{
  id: string;
  project: string;
  submission: string;
  status: EvaluationStatus;
  score: string;
  evaluatedAt: string;
}> = [
  {
    id: "campus-clinic-system",
    project: "Campus Clinic System",
    submission: "Final Submission",
    status: "Completed",
    score: "86%",
    evaluatedAt: "Apr 25, 2026",
  },
  {
    id: "inventory-tracking-app",
    project: "Inventory Tracking App",
    submission: "Sprint 2 Submission",
    status: "In Progress",
    score: "64%",
    evaluatedAt: "Apr 24, 2026",
  },
  {
    id: "event-rsvp-system",
    project: "Event RSVP System",
    submission: "Initial Submission",
    status: "Needs Review",
    score: "72%",
    evaluatedAt: "Apr 23, 2026",
  },
  {
    id: "expense-tracker-app",
    project: "Expense Tracker App",
    submission: "Final Submission",
    status: "Pending",
    score: "--",
    evaluatedAt: "Not yet evaluated",
  },
];

export function EvaluationTable() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Evaluation Queue</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Project</TableHead>
                <TableHead>Submission</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Score</TableHead>
                <TableHead>Evaluated At</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {evaluations.map((evaluation) => (
                <TableRow key={evaluation.id}>
                  <TableCell className="font-medium">{evaluation.project}</TableCell>
                  <TableCell>{evaluation.submission}</TableCell>
                  <TableCell>
                    <EvaluationStatusBadge status={evaluation.status} />
                  </TableCell>
                  <TableCell>{evaluation.score}</TableCell>
                  <TableCell>{evaluation.evaluatedAt}</TableCell>
                  <TableCell className="text-right">
                    <Button asChild variant="ghost" size="sm">
                      <Link to={`/evaluations/${evaluation.id}`}>
                        <Eye className="h-4 w-4" aria-hidden="true" />
                        View
                      </Link>
                    </Button>
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
