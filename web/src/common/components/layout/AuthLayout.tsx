import { Link, Outlet } from "react-router";
import { Badge } from "@/common/ui/shadcn/badge";

const benefits = [
  "Evaluate repositories, documents, and deployment readiness in one workflow.",
  "Support rubric-based reviews with traceable project evidence.",
  "Prepare clear compliance findings for advisers and evaluators.",
];

export function AuthLayout() {
  return (
    <main className="min-h-screen bg-slate-50">
      <div className="mx-auto grid min-h-screen w-full max-w-7xl gap-0 px-4 py-6 sm:px-6 lg:grid-cols-[1fr_0.9fr] lg:px-8">
        <section className="flex flex-col justify-between rounded-t-2xl border border-b-0 bg-white p-6 shadow-sm lg:rounded-l-2xl lg:rounded-tr-none lg:border-b lg:border-r-0 lg:p-10">
          <div>
            <Link to="/" className="flex items-center gap-3 font-semibold text-slate-950">
              <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary text-sm font-bold text-primary-foreground">
                AI
              </span>
              <span className="text-lg">ProjectEval</span>
            </Link>

            <div className="mt-12 max-w-xl">
              <Badge variant="secondary" className="border border-blue-100 bg-blue-50 text-blue-700">
                Academic SaaS Evaluation
              </Badge>
              <h1 className="mt-6 text-3xl font-semibold tracking-normal text-slate-950 sm:text-4xl">
                AI-powered software project evaluation and documentation compliance analysis.
              </h1>
              <p className="mt-5 text-base leading-7 text-muted-foreground">
                A focused workspace for reviewing student software projects, checking required documentation, and preparing
                evaluation-ready findings.
              </p>
            </div>
          </div>

          <div className="mt-10 grid gap-4">
            {benefits.map((benefit) => (
              <div key={benefit} className="flex gap-3 rounded-lg border bg-slate-50 p-4">
                <span className="mt-1 h-2.5 w-2.5 rounded-full bg-primary" />
                <p className="text-sm leading-6 text-slate-700">{benefit}</p>
              </div>
            ))}
          </div>
        </section>

        <section className="flex items-center justify-center rounded-b-2xl border bg-background p-4 shadow-sm sm:p-8 lg:rounded-r-2xl lg:rounded-bl-none lg:p-10">
          <div className="w-full max-w-md">
            <Outlet />
          </div>
        </section>
      </div>
    </main>
  );
}
