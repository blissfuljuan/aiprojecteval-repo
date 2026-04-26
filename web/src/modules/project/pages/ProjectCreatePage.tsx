import { Link } from "react-router";
import { ArrowLeft } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Select } from "@/common/ui/shadcn/select";
import { Textarea } from "@/common/ui/shadcn/textarea";

export function ProjectCreatePage() {
  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Create Project</h1>
          <p className="text-sm text-muted-foreground">Add a new software project for evaluation tracking.</p>
        </div>
        <Button asChild variant="outline">
          <Link to="/projects">
            <ArrowLeft className="h-4 w-4" aria-hidden="true" />
            Back to Projects
          </Link>
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Project Information</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-5">
          <div className="grid gap-2">
            <Label htmlFor="project-title">Project Title</Label>
            <Input id="project-title" placeholder="Enter project title" />
          </div>

          <div className="grid gap-2">
            <Label htmlFor="project-description">Description</Label>
            <Textarea id="project-description" placeholder="Briefly describe the project scope and objectives." />
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <div className="grid gap-2">
              <Label htmlFor="course-code">Course Code</Label>
              <Input id="course-code" placeholder="CAP101" />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="section">Section</Label>
              <Input id="section" placeholder="MCS-1A" />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="semester">Semester</Label>
              <Select id="semester" defaultValue="first">
                <option value="first">First Semester</option>
                <option value="second">Second Semester</option>
                <option value="summer">Summer</option>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="school-year">School Year</Label>
              <Input id="school-year" placeholder="2025-2026" />
            </div>
            <div className="grid gap-2 md:col-span-2">
              <Label htmlFor="status">Status</Label>
              <Select id="status" defaultValue="draft">
                <option value="draft">Draft</option>
                <option value="active">Active</option>
                <option value="under-review">Under Review</option>
                <option value="completed">Completed</option>
                <option value="archived">Archived</option>
              </Select>
            </div>
          </div>
        </CardContent>
        <CardFooter className="justify-end gap-3">
          <Button asChild variant="outline">
            <Link to="/projects">Cancel</Link>
          </Button>
          <Button type="button" disabled>
            Save Project
          </Button>
        </CardFooter>
      </Card>
    </div>
  );
}
