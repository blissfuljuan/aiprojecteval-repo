import { Navigate, Outlet } from "react-router";
import { paths } from "@/routes/paths";

const temporaryAccessTokenKey = "accessToken";

function hasTemporaryAuthToken() {
  return Boolean(window.localStorage.getItem(temporaryAccessTokenKey));
}

export function PublicOnlyRoute() {
  if (hasTemporaryAuthToken()) {
    return <Navigate to={paths.dashboard} replace />;
  }

  return <Outlet />;
}
