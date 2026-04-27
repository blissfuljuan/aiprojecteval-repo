import { Link } from "react-router";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { paths } from "@/routes/paths";

export function NotFoundPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-muted/30 p-6">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>Page not found</CardTitle>
          <CardDescription>The page you are looking for does not exist or has moved.</CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-3 sm:flex-row">
          <Button asChild>
            <Link to={paths.dashboard}>Back to dashboard</Link>
          </Button>
          <Button asChild variant="outline">
            <Link to={paths.home}>Go home</Link>
          </Button>
        </CardContent>
      </Card>
    </main>
  );
}
