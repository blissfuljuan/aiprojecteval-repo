import { ProtectedRoute } from "@/routes/ProtectedRoute";

export function RequireAuth() {
  return <ProtectedRoute />;
}
