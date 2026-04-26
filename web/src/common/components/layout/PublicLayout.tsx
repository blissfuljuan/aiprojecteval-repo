import { Link, Outlet } from "react-router";
import { Button } from "@/common/ui/shadcn/button";
import { Separator } from "@/common/ui/shadcn/separator";

const navLinks = [
  { label: "Features", href: "#features" },
  { label: "About", href: "#about" },
];

export function PublicLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-background text-foreground">
      <header className="sticky top-0 z-40 border-b bg-background/95 backdrop-blur">
        <div className="mx-auto flex h-16 w-full max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
          <Link to="/" className="flex items-center gap-3 font-semibold text-slate-950">
            <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary text-sm font-bold text-primary-foreground">
              AI
            </span>
            <span className="text-base sm:text-lg">ProjectEval</span>
          </Link>

          <nav aria-label="Primary navigation" className="hidden items-center gap-8 md:flex">
            {navLinks.map((link) => (
              <a key={link.label} href={link.href} className="text-sm font-medium text-muted-foreground hover:text-primary">
                {link.label}
              </a>
            ))}
            <Link to="/login" className="text-sm font-medium text-muted-foreground hover:text-primary">
              Login
            </Link>
          </nav>

          <div className="flex items-center gap-2">
            <Button asChild variant="ghost" size="sm" className="md:hidden">
              <Link to="/login">Login</Link>
            </Button>
            <Button asChild size="sm">
              <Link to="/register">Get Started</Link>
            </Button>
          </div>
        </div>
      </header>

      <main className="flex-1">
        <Outlet />
      </main>

      <footer className="border-t bg-slate-50">
        <div className="mx-auto grid w-full max-w-7xl gap-8 px-4 py-10 sm:px-6 md:grid-cols-[1.2fr_1fr_1fr] lg:px-8">
          <div>
            <div className="flex items-center gap-3 font-semibold text-slate-950">
              <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary text-sm font-bold text-primary-foreground">
                AI
              </span>
              <span>ProjectEval</span>
            </div>
            <p className="mt-4 max-w-md text-sm leading-6 text-muted-foreground">
              Public preview for AI-assisted software project evaluation, repository review, and documentation compliance
              analysis.
            </p>
          </div>

          <div>
            <h2 className="text-sm font-semibold text-slate-950">Platform</h2>
            <div className="mt-4 flex flex-col gap-3 text-sm text-muted-foreground">
              <a href="#features" className="hover:text-primary">
                Features
              </a>
              <a href="#about" className="hover:text-primary">
                About
              </a>
              <Link to="/login" className="hover:text-primary">
                Login
              </Link>
            </div>
          </div>

          <div>
            <h2 className="text-sm font-semibold text-slate-950">Access</h2>
            <p className="mt-4 text-sm leading-6 text-muted-foreground">
              Designed for academic evaluators, project advisers, and student software teams.
            </p>
          </div>
        </div>
        <Separator />
        <div className="mx-auto flex w-full max-w-7xl flex-col gap-2 px-4 py-5 text-xs text-muted-foreground sm:flex-row sm:items-center sm:justify-between sm:px-6 lg:px-8">
          <span>© 2026 ProjectEval. All rights reserved.</span>
          <span>AI-powered evaluation support for academic software projects.</span>
        </div>
      </footer>
    </div>
  );
}
