import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const activities = [
  "AI analysis generated for Campus Clinic System",
  "Instructor reviewed Library Management App",
  "Deployment URL validated for Event RSVP System",
];

export function RecentActivityCard() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Recent Activity</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {activities.map((activity) => (
            <div key={activity} className="flex items-start gap-3">
              <span className="mt-1.5 h-2 w-2 rounded-full bg-primary" />
              <div>
                <p className="text-sm font-medium">{activity}</p>
                <p className="text-xs text-muted-foreground">Recent activity placeholder</p>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
