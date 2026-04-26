import { Badge } from "@/common/ui/shadcn/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const submissions = [
  {
    project: "Campus Clinic System",
    activity: "SRS submitted",
    status: "Pending Review",
  },
  {
    project: "Library Management App",
    activity: "Repository linked",
    status: "In Progress",
  },
  {
    project: "Event RSVP System",
    activity: "Evaluation completed",
    status: "Completed",
  },
];

export function RecentSubmissionsTable() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Recent Submissions</CardTitle>
        <CardDescription>Latest static project submission activity</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {submissions.map((submission) => (
          <div key={submission.project} className="flex items-start justify-between gap-4">
            <div className="space-y-1">
              <p className="text-sm font-medium">{submission.project}</p>
              <p className="text-xs text-muted-foreground">{submission.activity}</p>
            </div>
            <Badge variant="outline">{submission.status}</Badge>
          </div>
        ))}
      </CardContent>
    </Card>
  );
}
