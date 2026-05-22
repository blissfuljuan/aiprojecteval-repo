import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { ArrowLeft } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { courseClassService } from "@/modules/course-class/services/courseClass.service";
import type { CourseClassRequest } from "@/modules/course-class/types";
import { paths } from "@/routes/paths";

const emptyForm: CourseClassRequest = {
  name: "",
  code: "",
};

export function CourseClassCreatePage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<CourseClassRequest>(emptyForm);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function set(field: keyof CourseClassRequest, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();

    try {
      setIsSubmitting(true);
      setError(null);
      await courseClassService.create({
        name: form.name.trim(),
        code: form.code.trim(),
      });
      void navigate(paths.courseClasses);
    } catch (err) {
      setError(courseClassService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Create Course Class</h1>
          <p className="text-sm text-muted-foreground">Add a course class for proposals, submissions, and evaluations.</p>
        </div>
        <Button asChild variant="outline">
          <Link to={paths.courseClasses}>
            <ArrowLeft className="h-4 w-4" aria-hidden="true" />
            Back to Course Classes
          </Link>
        </Button>
      </div>

      <form onSubmit={(event) => void handleSubmit(event)}>
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Class Details</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-5">
            {error && (
              <Alert variant="destructive">
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            <div className="grid gap-2">
              <Label htmlFor="name">Name <span aria-hidden="true">*</span></Label>
              <Input
                id="name"
                placeholder="e.g. Capstone Project 1"
                value={form.name}
                onChange={(event) => set("name", event.target.value)}
                required
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="code">Code <span aria-hidden="true">*</span></Label>
              <Input
                id="code"
                placeholder="e.g. CS401-A"
                value={form.code}
                onChange={(event) => set("code", event.target.value)}
                required
              />
            </div>
          </CardContent>
          <CardFooter className="justify-end gap-3">
            <Button asChild variant="outline">
              <Link to={paths.courseClasses}>Cancel</Link>
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Creating..." : "Create Course Class"}
            </Button>
          </CardFooter>
        </Card>
      </form>
    </div>
  );
}
