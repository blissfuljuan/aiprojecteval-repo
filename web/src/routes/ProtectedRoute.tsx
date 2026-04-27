import { Navigate, Outlet, useLocation } from "react-router";
import { useAuth } from "@/modules/identity/context/AuthContext";
import { paths } from "@/routes/paths";

export function ProtectedRoute() {
  const location = useLocation();
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div className="flex min-h-screen items-center justify-center text-sm text-muted-foreground">Loading...</div>;
  }

  if (isAuthenticated) {
    return <Outlet />;
  }

  return <Navigate to={paths.login} replace state={{ from: location }} />;
}
