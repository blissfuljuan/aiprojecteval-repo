import { Navigate, Outlet } from "react-router";
import { getDefaultAuthenticatedPath } from "@/common/lib/roleAccess";
import { useAuth } from "@/modules/identity/context/AuthContext";

export function PublicOnlyRoute() {
  const { user, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div className="flex min-h-screen items-center justify-center text-sm text-muted-foreground">Loading...</div>;
  }

  if (isAuthenticated) {
    return <Navigate to={getDefaultAuthenticatedPath(user)} replace />;
  }

  return <Outlet />;
}
