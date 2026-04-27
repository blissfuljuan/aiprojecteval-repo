import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router";
import { ArrowLeft } from "lucide-react";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { Input } from "@/common/ui/shadcn/input";
import { Label } from "@/common/ui/shadcn/label";
import { Select } from "@/common/ui/shadcn/select";
import { Textarea } from "@/common/ui/shadcn/textarea";
import { courseClassService } from "@/modules/project-proposal/services/courseClass.service";
import { projectProposalService } from "@/modules/project-proposal/services/projectProposal.service";
import type { CourseClass, ProposalCreateRequest } from "@/modules/project-proposal/types";
import { paths } from "@/routes/paths";

const emptyForm: ProposalCreateRequest = {
  title: "",
  problemStatement: "",
  objectives: "",
  targetUsers: "",
  proposedFeatures: "",
  technologyStack: "",
  expectedOutput: "",
  courseClassId: 0,
};

export function ProposalCreatePage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<ProposalCreateRequest>(emptyForm);
  const [courseClasses, setCourseClasses] = useState<CourseClass[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    courseClassService.findAll().then(setCourseClasses).catch(() => {});
  }, []);

  function set(field: keyof ProposalCreateRequest, value: string | number) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!form.courseClassId) {
      setError("Please select a course class.");
      return;
    }
    try {
      setIsSubmitting(true);
      setError(null);
      const proposal = await projectProposalService.createProposal(form);
      void navigate(paths.proposalDetails(proposal.id));
    } catch (err) {
      setError(projectProposalService.getErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="flex w-full flex-col gap-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div className="space-y-1">
          <h1 className="text-3xl font-semibold tracking-normal">Submit Proposal</h1>
          <p className="text-sm text-muted-foreground">Describe your software project idea for adviser and instructor review.</p>
        </div>
        <Button asChild variant="outline">
          <Link to={paths.proposals}>
            <ArrowLeft className="h-4 w-4" aria-hidden="true" />
            Back to Proposals
          </Link>
        </Button>
      </div>

      <form onSubmit={handleSubmit}>
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Proposal Details</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-5">
            {error && <p className="text-sm text-destructive">{error}</p>}

            <div className="grid gap-2">
              <Label htmlFor="course-class">Course Class <span aria-hidden="true">*</span></Label>
              <Select
                id="course-class"
                value={form.courseClassId || ""}
                onChange={(e) => set("courseClassId", Number(e.target.value))}
                required
              >
                <option value="">Select a course class</option>
                {courseClasses.map((cc) => (
                  <option key={cc.id} value={cc.id}>
                    {cc.code ? `${cc.code} — ` : ""}{cc.name}
                  </option>
                ))}
              </Select>
            </div>

            <div className="grid gap-2">
              <Label htmlFor="title">Project Title <span aria-hidden="true">*</span></Label>
              <Input
                id="title"
                placeholder="Enter a clear and concise project title"
                value={form.title}
                onChange={(e) => set("title", e.target.value)}
                required
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="problem-statement">Problem Statement <span aria-hidden="true">*</span></Label>
              <Textarea
                id="problem-statement"
                placeholder="Describe the problem your project addresses"
                rows={4}
                value={form.problemStatement}
                onChange={(e) => set("problemStatement", e.target.value)}
                required
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="objectives">Objectives <span aria-hidden="true">*</span></Label>
              <Textarea
                id="objectives"
                placeholder="List the specific goals of your project"
                rows={3}
                value={form.objectives}
                onChange={(e) => set("objectives", e.target.value)}
                required
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="proposed-features">Proposed Features <span aria-hidden="true">*</span></Label>
              <Textarea
                id="proposed-features"
                placeholder="Describe the main features of your system"
                rows={3}
                value={form.proposedFeatures}
                onChange={(e) => set("proposedFeatures", e.target.value)}
                required
              />
            </div>

            <div className="grid gap-5 md:grid-cols-2">
              <div className="grid gap-2">
                <Label htmlFor="target-users">Target Users</Label>
                <Input
                  id="target-users"
                  placeholder="Who will use this system?"
                  value={form.targetUsers}
                  onChange={(e) => set("targetUsers", e.target.value)}
                />
              </div>

              <div className="grid gap-2">
                <Label htmlFor="tech-stack">Technology Stack</Label>
                <Input
                  id="tech-stack"
                  placeholder="e.g. React, Spring Boot, PostgreSQL"
                  value={form.technologyStack}
                  onChange={(e) => set("technologyStack", e.target.value)}
                />
              </div>
            </div>

            <div className="grid gap-2">
              <Label htmlFor="expected-output">Expected Output</Label>
              <Textarea
                id="expected-output"
                placeholder="What will the final deliverable be?"
                rows={3}
                value={form.expectedOutput}
                onChange={(e) => set("expectedOutput", e.target.value)}
              />
            </div>
          </CardContent>
          <CardFooter className="justify-end gap-3">
            <Button asChild variant="outline">
              <Link to={paths.proposals}>Cancel</Link>
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Submitting..." : "Submit Proposal"}
            </Button>
          </CardFooter>
        </Card>
      </form>
    </div>
  );
}
