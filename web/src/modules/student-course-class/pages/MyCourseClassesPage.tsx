import { useEffect, useState } from "react";
import { LogOut, Plus } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Separator } from "@/common/ui/shadcn/separator";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/common/ui/shadcn/table";
import { studentCourseClassService } from "@/modules/student-course-class/services/studentCourseClass.service";
import type { CourseClass } from "@/modules/course-class/types";

function uniqueCourseClasses(classes: CourseClass[]) {
  return Array.from(new Map(classes.map((courseClass) => [courseClass.id, courseClass])).values());
}

export function MyCourseClassesPage() {
  const [code, setCode] = useState("");
  const [courseClasses, setCourseClasses] = useState<CourseClass[]>([]);
  const [isEnrollOpen, setIsEnrollOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [leavingId, setLeavingId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [enrollError, setEnrollError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  async function loadCourseClasses() {
    try {
      setIsLoading(true);
      setError(null);
      setCourseClasses(uniqueCourseClasses(await studentCourseClassService.findMyCourseClasses()));
    } catch (err) {
      setError(studentCourseClassService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    void loadCourseClasses();
  }, []);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    const trimmedCode = code.trim();

    if (!trimmedCode) {
      setEnrollError("Class code is required");
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
      setEnrollError(null);
      setSuccess(null);
      const enrolled = await studentCourseClassService.enrollByCode(trimmedCode);
      setSuccess(`Enrolled in ${enrolled.name}.`);
      setCode("");
      setIsEnrollOpen(false);
      await loadCourseClasses();
    } catch (err) {
      setEnrollError(studentCourseClassService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  function openEnrollModal() {
    setCode("");
    setEnrollError(null);
    setSuccess(null);
    setIsEnrollOpen(true);
  }

  function closeEnrollModal() {
    if (isSubmitting) return;
    setIsEnrollOpen(false);
    setCode("");
    setEnrollError(null);
  }

  async function handleUnenroll(courseClass: CourseClass) {
    const confirmed = window.confirm(`Leave ${courseClass.name}?`);
    if (!confirmed) return;

    try {
      setLeavingId(courseClass.id);
      setError(null);
      setSuccess(null);
      await studentCourseClassService.unenroll(courseClass.id);
      setSuccess(`Left ${courseClass.name}.`);
      await loadCourseClasses();
    } catch (err) {
      setError(studentCourseClassService.getErrorMessage(err));
    } finally {
      setLeavingId(null);
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">My Classes</h1>
          <p className="text-sm text-muted-foreground">Enroll using the class code provided by your instructor.</p>
        </div>
        <Button onClick={openEnrollModal}>
          <Plus className="h-4 w-4" aria-hidden="true" />
          Enroll in a Class
        </Button>
      </div>

      <Card>
        <CardContent className="space-y-4">
          <Separator />

          {error && (
            <Alert variant="destructive">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
          {success && (
            <Alert>
              <AlertDescription>{success}</AlertDescription>
            </Alert>
          )}

          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading classes...</p>
          ) : courseClasses.length === 0 ? (
            <p className="text-sm text-muted-foreground">You are not enrolled in any classes yet.</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>
                  <TableHead>Code</TableHead>
                  <TableHead>Enrolled Class Created</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {courseClasses.map((courseClass) => (
                  <TableRow key={courseClass.id}>
                    <TableCell className="font-medium">{courseClass.name}</TableCell>
                    <TableCell>{courseClass.code}</TableCell>
                    <TableCell>{new Date(courseClass.createdAt).toLocaleDateString()}</TableCell>
                    <TableCell>
                      <div className="flex justify-end">
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => void handleUnenroll(courseClass)}
                          disabled={leavingId === courseClass.id}
                        >
                          <LogOut className="h-3.5 w-3.5" aria-hidden="true" />
                          {leavingId === courseClass.id ? "Leaving..." : "Leave"}
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {isEnrollOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-background/80 p-4 backdrop-blur-sm"
          role="presentation"
          onClick={closeEnrollModal}
        >
          <Card
            className="w-full max-w-md shadow-lg"
            role="dialog"
            aria-modal="true"
            aria-labelledby="enroll-dialog-title"
            onClick={(event) => event.stopPropagation()}
          >
            <CardHeader>
              <CardTitle id="enroll-dialog-title" className="text-lg">Enroll in a Class</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {enrollError && (
                <Alert variant="destructive">
                  <AlertDescription>{enrollError}</AlertDescription>
                </Alert>
              )}

              <form className="grid gap-4" onSubmit={(event) => void handleSubmit(event)}>
                <div className="grid gap-2">
                  <Label htmlFor="class-code">Class Code</Label>
                  <Input
                    id="class-code"
                    placeholder="Enter class code"
                    value={code}
                    onChange={(event) => setCode(event.target.value)}
                    autoFocus
                    required
                  />
                </div>

                <div className="flex justify-end gap-3">
                  <Button type="button" variant="outline" onClick={closeEnrollModal} disabled={isSubmitting}>
                    Cancel
                  </Button>
                  <Button type="submit" disabled={isSubmitting || !code.trim()}>
                    <Plus className="h-4 w-4" aria-hidden="true" />
                    {isSubmitting ? "Enrolling..." : "Enroll"}
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
