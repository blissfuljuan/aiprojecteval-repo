import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { ArrowLeft } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { useCourseClass } from "@/modules/course-class/hooks/useCourseClass";
import { courseClassService } from "@/modules/course-class/services/courseClass.service";
import type { CourseClassRequest } from "@/modules/course-class/types";
import { paths } from "@/routes/paths";

export function CourseClassEditPage() {
  const { courseClassId } = useParams<{ courseClassId: string }>();
  const navigate = useNavigate();
  const { courseClass, isLoading, error } = useCourseClass(Number(courseClassId));
  const [form, setForm] = useState<CourseClassRequest>({
    name: "",
    code: "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  useEffect(() => {
    if (!courseClass) return;
    setForm({
      name: courseClass.name,
      code: courseClass.code,
    });
  }, [courseClass]);

  function set(field: keyof CourseClassRequest, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (!courseClass) return;

    try {
      setIsSubmitting(true);
      setSubmitError(null);
      await courseClassService.update(courseClass.id, {
        name: form.name.trim(),
        code: form.code.trim(),
      });
      void navigate(paths.courseClasses);
    } catch (err) {
      setSubmitError(courseClassService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  if (isLoading) return <p className="text-sm text-muted-foreground">Loading course class...</p>;
  if (error || !courseClass) {
    return <p className="text-sm text-destructive">{error ?? "Course class not found."}</p>;
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Edit Course Class</h1>
          <p className="text-sm text-muted-foreground">Update class details for project workflows.</p>
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
            {submitError && (
              <Alert variant="destructive">
                <AlertDescription>{submitError}</AlertDescription>
              </Alert>
            )}

            <div className="grid gap-2">
              <Label htmlFor="name">Name <span aria-hidden="true">*</span></Label>
              <Input
                id="name"
                value={form.name}
                onChange={(event) => set("name", event.target.value)}
                required
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="code">Code <span aria-hidden="true">*</span></Label>
              <Input
                id="code"
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
              {isSubmitting ? "Saving..." : "Save Changes"}
            </Button>
          </CardFooter>
        </Card>
      </form>
    </div>
  );
}
