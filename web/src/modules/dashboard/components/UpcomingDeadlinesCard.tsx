import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const deadlines = [
  { title: "SDD Submission", date: "Apr 30" },
  { title: "Prototype Review", date: "May 07" },
  { title: "Final Evaluation", date: "May 20" },
];

export function UpcomingDeadlinesCard() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Upcoming Deadlines</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {deadlines.map((deadline) => (
            <div key={deadline.title} className="flex items-center justify-between gap-4">
              <div>
                <p className="text-sm font-medium">{deadline.title}</p>
                <p className="text-xs text-muted-foreground">Deadline placeholder</p>
              </div>
              <span className="rounded-md bg-muted px-2 py-1 text-xs text-muted-foreground">{deadline.date}</span>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
