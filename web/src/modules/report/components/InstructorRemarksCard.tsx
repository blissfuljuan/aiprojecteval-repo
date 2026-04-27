import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Textarea } from "@/common/ui/shadcn/textarea";

export function InstructorRemarksCard() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Instructor Remarks</CardTitle>
        <CardDescription>Add manual review notes before report approval.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <Textarea placeholder="Enter instructor remarks here..." className="min-h-32" />
        <div className="flex justify-end">
          <Button type="button">Save Remarks</Button>
        </div>
      </CardContent>
    </Card>
  );
}
