import { Navigate, Outlet } from "react-router";
import { useAuth } from "@/modules/identity/context/AuthContext";
import { paths } from "@/routes/paths";

export function PublicOnlyRoute() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div className="flex min-h-screen items-center justify-center text-sm text-muted-foreground">Loading...</div>;
  }

  if (isAuthenticated) {
    return <Navigate to={paths.dashboard} replace />;
  }

  return <Outlet />;
}
