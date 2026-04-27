import { Navigate, Outlet, useLocation } from "react-router";
import { paths } from "@/routes/paths";

const temporaryAccessTokenKey = "accessToken";

function hasTemporaryAuthToken() {
  return Boolean(window.localStorage.getItem(temporaryAccessTokenKey));
}

export function ProtectedRoute() {
  const location = useLocation();

  if (hasTemporaryAuthToken()) {
    return <Outlet />;
  }

  return <Navigate to={paths.login} replace state={{ from: location }} />;
}
