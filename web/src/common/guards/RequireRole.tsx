import { Link, Navigate, Outlet, useLocation } from "react-router";
import { Button } from "@/common/ui/shadcn/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/common/ui/shadcn/card";
import { hasAnyRole } from "@/common/lib/auth";
import { getDefaultAuthenticatedPath } from "@/common/lib/roleAccess";
import { useAuth } from "@/modules/identity/context/AuthContext";
import type { Role } from "@/modules/identity/types";
import { paths } from "@/routes/paths";

type RequireRoleProps = {
  allowedRoles: Role[];
};

export function RequireRole({ allowedRoles }: RequireRoleProps) {
  const location = useLocation();
  const { user, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div className="flex min-h-screen items-center justify-center text-sm text-muted-foreground">Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to={paths.login} replace state={{ from: location }} />;
  }

  if (!hasAnyRole(user, allowedRoles)) {
    return <AccessDenied />;
  }

  return <Outlet />;
}

function AccessDenied() {
  const { user } = useAuth();
  const defaultPath = getDefaultAuthenticatedPath(user);

  return (
    <main className="flex min-h-screen items-center justify-center bg-muted/30 p-6">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>Access denied</CardTitle>
          <CardDescription>Your account does not have permission to view this page.</CardDescription>
        </CardHeader>
        <CardContent>
          <Button asChild>
            <Link to={defaultPath}>Back to workspace</Link>
          </Button>
        </CardContent>
      </Card>
    </main>
  );
}
