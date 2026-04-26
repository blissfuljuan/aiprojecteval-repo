import { Link } from "react-router";
import { Eye, FileCheck2, FileClock, FileWarning, Plus, Search } from "lucide-react";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Select } from "@/common/ui/shadcn/select";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/common/ui/shadcn/table";
import { SubmissionStatusBadge } from "@/modules/submission/components/SubmissionStatusBadge";
import type { SubmissionStatus } from "@/modules/submission/types";

const summaryCards = [
  { label: "Total Submissions", value: "34", icon: FileCheck2 },
  { label: "Pending Review", value: "8", icon: FileClock },
  { label: "Needs Revision", value: "3", icon: FileWarning },
  { label: "Evaluated", value: "23", icon: FileCheck2 },
];

const submissions: Array<{
  project: string;
  title: string;
  submittedBy: string;
  status: SubmissionStatus;
  submittedAt: string;
  evaluationStatus: string;
}> = [
  {
    project: "Campus Clinic Appointment System",
    title: "Final Documentation Package",
    submittedBy: "Maria Santos",
    status: "Submitted",
    submittedAt: "Apr 25, 2026",
    evaluationStatus: "Pending Review",
  },
  {
    project: "Library Kiosk Reservation System",
    title: "Revision 2 Compliance Set",
    submittedBy: "Jon Reyes",
    status: "Needs Revision",
    submittedAt: "Apr 23, 2026",
    evaluationStatus: "Returned",
  },
  {
    project: "Inventory Audit Tracker",
    title: "Initial Evaluation Submission",
    submittedBy: "Ana Cruz",
    status: "Under Review",
    submittedAt: "Apr 21, 2026",
    evaluationStatus: "AI Analysis Pending",
  },
  {
    project: "Event RSVP System",
    title: "Completed Project Review",
    submittedBy: "Luis Garcia",
    status: "Evaluated",
    submittedAt: "Apr 18, 2026",
    evaluationStatus: "Evaluated",
  },
];

export function SubmissionListPage() {
  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Submissions</h1>
          <p className="text-sm text-muted-foreground">View and manage submitted project artifacts.</p>
        </div>
        <Button asChild>
          <Link to="/submissions/create">
            <Plus className="h-4 w-4" aria-hidden="true" />
            New Submission
          </Link>
        </Button>
      </div>

      <Card>
        <CardContent className="grid gap-4 p-4 md:grid-cols-2 xl:grid-cols-4">
          <div className="relative">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input placeholder="Search submissions" className="pl-9" />
          </div>
          <Select defaultValue="all-statuses" aria-label="Status filter">
            <option value="all-statuses">All statuses</option>
            <option value="submitted">Submitted</option>
            <option value="under-review">Under Review</option>
            <option value="needs-revision">Needs Revision</option>
            <option value="evaluated">Evaluated</option>
          </Select>
          <Select defaultValue="all-projects" aria-label="Project filter">
            <option value="all-projects">All projects</option>
            <option value="campus-clinic">Campus Clinic Appointment System</option>
            <option value="library-kiosk">Library Kiosk Reservation System</option>
            <option value="inventory-audit">Inventory Audit Tracker</option>
          </Select>
          <Select defaultValue="last-30-days" aria-label="Date filter">
            <option value="last-30-days">Last 30 days</option>
            <option value="this-week">This week</option>
            <option value="this-month">This month</option>
            <option value="semester">Current semester</option>
          </Select>
        </CardContent>
      </Card>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {summaryCards.map((item) => {
          const Icon = item.icon;
          return (
            <Card key={item.label}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium text-muted-foreground">{item.label}</CardTitle>
                <Icon className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-semibold">{item.value}</div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Submissions Table</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Project</TableHead>
                <TableHead>Submission Title</TableHead>
                <TableHead>Submitted By</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Submitted At</TableHead>
                <TableHead>Evaluation Status</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {submissions.map((submission) => (
                <TableRow key={`${submission.project}-${submission.title}`}>
                  <TableCell className="font-medium">{submission.project}</TableCell>
                  <TableCell>{submission.title}</TableCell>
                  <TableCell>{submission.submittedBy}</TableCell>
                  <TableCell>
                    <SubmissionStatusBadge status={submission.status} />
                  </TableCell>
                  <TableCell>{submission.submittedAt}</TableCell>
                  <TableCell>
                    <Badge variant="outline">{submission.evaluationStatus}</Badge>
                  </TableCell>
                  <TableCell className="text-right">
                    <Button asChild variant="ghost" size="sm">
                      <Link to="/submissions/sample-submission-id">
                        <Eye className="h-4 w-4" aria-hidden="true" />
                        View
                      </Link>
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
