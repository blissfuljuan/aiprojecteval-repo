import type { Role, User } from "@/modules/identity/types";
import { paths } from "@/routes/paths";

export const unrestrictedRoles: Role[] = ["ADMIN", "INSTRUCTOR", "EVALUATOR", "ADVISER"];

export const studentModuleRoles: Role[] = [...unrestrictedRoles, "STUDENT"];

export function getDefaultAuthenticatedPath(user: User | null | undefined) {
  return user?.role === "STUDENT" ? paths.projects : paths.dashboard;
}
