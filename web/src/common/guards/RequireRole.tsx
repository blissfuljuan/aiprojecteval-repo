import { Outlet } from "react-router";

type RequireRoleProps = {
  allowedRoles: string[];
};

export function RequireRole({ allowedRoles }: RequireRoleProps) {
  void allowedRoles;

  return <Outlet />;
}
