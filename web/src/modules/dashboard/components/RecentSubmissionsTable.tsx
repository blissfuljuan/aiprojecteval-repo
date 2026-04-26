import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/common/ui/shadcn/table";

const submissions = [
  {
    project: "Campus Clinic System",
    group: "Group 1",
    submissionType: "SRS",
    status: "Pending",
    submittedAt: "Apr 25, 2026",
  },
  {
    project: "Inventory Tracker",
    group: "Group 2",
    submissionType: "SDD",
    status: "Under Review",
    submittedAt: "Apr 24, 2026",
  },
  {
    project: "Appointment App",
    group: "Group 3",
    submissionType: "Deployment",
    status: "Completed",
    submittedAt: "Apr 23, 2026",
  },
];

export function RecentSubmissionsTable() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Recent Submissions</CardTitle>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Project</TableHead>
              <TableHead>Group</TableHead>
              <TableHead>Submission Type</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Submitted At</TableHead>
              <TableHead className="text-right">Action</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {submissions.map((submission) => (
              <TableRow key={`${submission.project}-${submission.submissionType}`}>
                <TableCell className="font-medium">{submission.project}</TableCell>
                <TableCell>{submission.group}</TableCell>
                <TableCell>{submission.submissionType}</TableCell>
                <TableCell>
                  <Badge variant="outline">{submission.status}</Badge>
                </TableCell>
                <TableCell>{submission.submittedAt}</TableCell>
                <TableCell className="text-right">
                  <Button variant="outline" size="sm">
                    View
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
