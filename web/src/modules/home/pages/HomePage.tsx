import { Link } from "react-router";
import { Badge } from "@/common/ui/shadcn/badge";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";

const problemItems = [
  "Project evidence is scattered across repositories, documents, and deployment links.",
  "Manual reviews make it hard to keep compliance checks consistent across teams.",
  "Evaluation notes often need extra cleanup before they are useful for reporting.",
];

const solutionSteps = [
  "Collect project materials",
  "Analyze quality and compliance",
  "Produce evaluation-ready findings",
];

const features = [
  {
    title: "Repository Analysis",
    description: "Review code structure, project artifacts, and implementation evidence from a single evaluation surface.",
  },
  {
    title: "Documentation Compliance",
    description: "Check required documentation sections against academic project submission expectations.",
  },
  {
    title: "Rubric Support",
    description: "Organize findings around evaluator criteria without replacing human academic judgment.",
  },
  {
    title: "Deployment Validation",
    description: "Reserve space for deployment readiness checks and project delivery evidence.",
  },
  {
    title: "Report Preparation",
    description: "Shape review outputs into clear findings that are easier to discuss, verify, and archive.",
  },
  {
    title: "Role-Aware Workflow",
    description: "Prepare the interface for students, evaluators, advisers, and administrators in later phases.",
  },
];

export function HomePage() {
  return (
    <div className="bg-background">
      <section className="mx-auto grid w-full max-w-7xl items-center gap-12 px-4 py-20 sm:px-6 lg:grid-cols-[1.05fr_0.95fr] lg:px-8 lg:py-28">
        <div>
          <Badge variant="secondary" className="border border-blue-100 bg-blue-50 text-blue-700">
            AI-assisted academic evaluation
          </Badge>
          <h1 className="mt-6 max-w-4xl text-4xl font-semibold tracking-normal text-slate-950 sm:text-5xl lg:text-6xl">
            Evaluate software projects with clearer evidence and documentation checks.
          </h1>
          <p className="mt-6 max-w-2xl text-lg leading-8 text-muted-foreground">
            ProjectEval helps academic teams review software project submissions, inspect compliance signals, and prepare
            structured evaluation findings from one professional workspace.
          </p>
          <div className="mt-8 flex flex-col gap-3 sm:flex-row">
            <Button asChild size="lg">
              <Link to="/register">Get Started</Link>
            </Button>
            <Button asChild variant="outline" size="lg">
              <a href="#features">View Features</a>
            </Button>
          </div>
        </div>

        <div className="rounded-2xl border bg-white p-4 shadow-xl shadow-blue-100/60">
          <div className="rounded-xl border bg-slate-50 p-4">
            <div className="flex items-center justify-between gap-4 border-b pb-4">
              <div>
                <p className="text-sm font-medium text-slate-950">Evaluation Snapshot</p>
                <p className="text-xs text-muted-foreground">Static interface preview</p>
              </div>
              <Badge>Ready</Badge>
            </div>
            <div className="grid gap-3 py-4 sm:grid-cols-3">
              {["Repository", "Documents", "Deployment"].map((item) => (
                <div key={item} className="rounded-lg border bg-white p-3">
                  <p className="text-xs text-muted-foreground">{item}</p>
                  <p className="mt-2 text-xl font-semibold text-slate-950">92%</p>
                </div>
              ))}
            </div>
            <div className="space-y-3">
              {solutionSteps.map((step, index) => (
                <div key={step} className="flex items-center gap-3 rounded-lg border bg-white p-3">
                  <span className="flex h-7 w-7 items-center justify-center rounded-full bg-blue-50 text-xs font-semibold text-blue-700">
                    {index + 1}
                  </span>
                  <span className="text-sm font-medium text-slate-700">{step}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      <section id="about" className="border-y bg-slate-50">
        <div className="mx-auto grid w-full max-w-7xl gap-10 px-4 py-16 sm:px-6 lg:grid-cols-[0.8fr_1.2fr] lg:px-8">
          <div>
            <Badge variant="outline">The Problem</Badge>
            <h2 className="mt-4 text-3xl font-semibold tracking-normal text-slate-950">
              Software project evaluation needs structure without slowing reviewers down.
            </h2>
          </div>
          <div className="grid gap-4">
            {problemItems.map((item) => (
              <Card key={item}>
                <CardContent className="p-5 text-sm leading-6 text-slate-700">{item}</CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      <section className="mx-auto grid w-full max-w-7xl gap-10 px-4 py-16 sm:px-6 lg:grid-cols-[1fr_1fr] lg:px-8">
        <div>
          <Badge variant="secondary" className="border border-blue-100 bg-blue-50 text-blue-700">
            The Solution
          </Badge>
          <h2 className="mt-4 text-3xl font-semibold tracking-normal text-slate-950">
            A dedicated review shell for project evidence, compliance checks, and evaluator outputs.
          </h2>
          <p className="mt-5 text-base leading-7 text-muted-foreground">
            The platform is designed to make later evaluation workflows feel coherent from the first screen: submit
            materials, analyze evidence, and prepare findings for academic review.
          </p>
        </div>
        <div className="grid gap-4">
          {solutionSteps.map((step, index) => (
            <div key={step} className="flex gap-4 rounded-xl border bg-white p-5 shadow-sm">
              <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary text-sm font-semibold text-primary-foreground">
                {index + 1}
              </span>
              <div>
                <h3 className="font-semibold text-slate-950">{step}</h3>
                <p className="mt-1 text-sm leading-6 text-muted-foreground">
                  Static placeholder content for the public-facing product workflow.
                </p>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section id="features" className="border-y bg-slate-50">
        <div className="mx-auto w-full max-w-7xl px-4 py-16 sm:px-6 lg:px-8">
          <div className="max-w-3xl">
            <Badge variant="outline">Features</Badge>
            <h2 className="mt-4 text-3xl font-semibold tracking-normal text-slate-950">
              Built for academic software project review.
            </h2>
            <p className="mt-4 text-base leading-7 text-muted-foreground">
              These placeholders define the public story for the system while leaving implementation details for later
              development phases.
            </p>
          </div>
          <div className="mt-10 grid gap-5 md:grid-cols-2 lg:grid-cols-3">
            {features.map((feature) => (
              <Card key={feature.title} className="h-full">
                <CardHeader>
                  <CardTitle className="text-lg">{feature.title}</CardTitle>
                  <CardDescription className="leading-6">{feature.description}</CardDescription>
                </CardHeader>
              </Card>
            ))}
          </div>
        </div>
      </section>

      <section className="mx-auto w-full max-w-7xl px-4 py-16 sm:px-6 lg:px-8">
        <div className="rounded-2xl border bg-primary px-6 py-10 text-primary-foreground shadow-lg sm:px-10">
          <div className="flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
            <div>
              <h2 className="text-3xl font-semibold tracking-normal">Prepare a cleaner project evaluation workflow.</h2>
              <p className="mt-3 max-w-2xl text-sm leading-6 text-primary-foreground/85">
                Start with the static public shell now, then connect project submissions, analysis, and reporting in later
                implementation phases.
              </p>
            </div>
            <div className="flex flex-col gap-3 sm:flex-row">
              <Button asChild variant="secondary" size="lg">
                <Link to="/register">Create Account</Link>
              </Button>
              <Button asChild variant="outline" size="lg" className="border-primary-foreground/30 bg-transparent text-primary-foreground hover:bg-primary-foreground/10 hover:text-primary-foreground">
                <Link to="/login">Sign In</Link>
              </Button>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
