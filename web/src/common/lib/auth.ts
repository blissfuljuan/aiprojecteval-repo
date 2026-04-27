import type { Role, User } from "@/modules/identity/types";

export type AuthUser = User;

export function hasRole(user: User | null | undefined, role: Role) {
  return user?.role === role;
}

export function hasAnyRole(user: User | null | undefined, roles: Role[]) {
  return Boolean(user && roles.includes(user.role));
}
