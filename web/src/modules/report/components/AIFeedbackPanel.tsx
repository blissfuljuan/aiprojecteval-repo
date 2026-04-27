import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

export function AIFeedbackPanel() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">AI Feedback</CardTitle>
        <CardDescription>Generated review notes for instructor validation.</CardDescription>
      </CardHeader>
      <CardContent>
        <p className="text-sm leading-6 text-muted-foreground">
          The submitted project demonstrates strong documentation coverage. However, some functional requirements are
          not fully traceable to implementation evidence. The instructor should review authentication-related
          requirements and deployment validation results before final scoring.
        </p>
      </CardContent>
    </Card>
  );
}
