import { useMemo, useState } from "react";
import { Link } from "react-router";
import { Edit, Plus, Search, Trash2 } from "lucide-react";
import { Alert, AlertDescription } from "@/common/ui/shadcn/alert";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Separator } from "@/common/ui/shadcn/separator";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/common/ui/shadcn/table";
import { useCourseClasses } from "@/modules/course-class/hooks/useCourseClasses";
import { courseClassService } from "@/modules/course-class/services/courseClass.service";
import type { CourseClass } from "@/modules/course-class/types";
import { useAuth } from "@/modules/identity/context/AuthContext";
import { paths } from "@/routes/paths";

export function CourseClassListPage() {
  const { user } = useAuth();
  const { courseClasses, isLoading, error, refetch } = useCourseClasses();
  const [search, setSearch] = useState("");
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const canDelete = user?.role === "ADMIN";
  const filteredCourseClasses = useMemo(() => {
    const query = search.trim().toLowerCase();
    if (!query) return courseClasses;

    return courseClasses.filter((courseClass) =>
      [courseClass.name, courseClass.code].some((value) => value.toLowerCase().includes(query)),
    );
  }, [courseClasses, search]);

  async function handleDelete(courseClass: CourseClass) {
    const confirmed = window.confirm(`Delete ${courseClass.name}? This action cannot be undone.`);
    if (!confirmed) return;

    try {
      setDeletingId(courseClass.id);
      setDeleteError(null);
      await courseClassService.delete(courseClass.id);
      await refetch();
    } catch (err) {
      setDeleteError(courseClassService.getErrorMessage(err));
    } finally {
      setDeletingId(null);
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Course Classes</h1>
          <p className="text-sm text-muted-foreground">Manage course class sections used across project workflows.</p>
        </div>
        <Button asChild>
          <Link to={paths.courseClassCreate}>
            <Plus className="h-4 w-4" aria-hidden="true" />
            New Course Class
          </Link>
        </Button>
      </div>

      <Card>
        <CardHeader className="pb-4">
          <CardTitle className="text-lg">Classes</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <Separator />

          <div className="relative max-w-sm">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              className="pl-9"
              placeholder="Search by name or code"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
            />
          </div>

          {(error || deleteError) && (
            <Alert variant="destructive">
              <AlertDescription>{deleteError ?? error}</AlertDescription>
            </Alert>
          )}

          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading course classes...</p>
          ) : filteredCourseClasses.length === 0 ? (
            <p className="text-sm text-muted-foreground">
              {search.trim() ? "No course classes match your search." : "No course classes found."}
            </p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>
                  <TableHead>Code</TableHead>
                  <TableHead>Created At</TableHead>
                  <TableHead>Updated At</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredCourseClasses.map((courseClass) => (
                  <TableRow key={courseClass.id}>
                    <TableCell className="font-medium">{courseClass.name}</TableCell>
                    <TableCell>{courseClass.code}</TableCell>
                    <TableCell>{formatDate(courseClass.createdAt)}</TableCell>
                    <TableCell>{formatDate(courseClass.updatedAt)}</TableCell>
                    <TableCell>
                      <div className="flex justify-end gap-2">
                        <Button size="sm" variant="outline" asChild>
                          <Link to={paths.courseClassEdit(courseClass.id)}>
                            <Edit className="h-3.5 w-3.5" aria-hidden="true" />
                            Edit
                          </Link>
                        </Button>
                        {canDelete && (
                          <Button
                            size="sm"
                            variant="destructive"
                            onClick={() => void handleDelete(courseClass)}
                            disabled={deletingId === courseClass.id}
                          >
                            <Trash2 className="h-3.5 w-3.5" aria-hidden="true" />
                            {deletingId === courseClass.id ? "Deleting..." : "Delete"}
                          </Button>
                        )}
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

function formatDate(value: string) {
  return new Date(value).toLocaleString();
}
