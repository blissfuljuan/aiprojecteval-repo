import type { Role, User } from "@/modules/identity/types";
import { paths } from "@/routes/paths";

export const accessTokenKey = "accessToken";
export const authSessionClearedEvent = "auth:session-cleared";

export type AuthCleanupReason = "logout" | "unauthorized" | "session-restore-failed";

type ClearAuthSessionOptions = {
  redirectToLogin?: boolean;
};

export type AuthSessionClearedDetail = {
  reason: AuthCleanupReason;
};

export type AuthUser = User;

const publicAuthPaths = new Set<string>([paths.home, paths.login, paths.register]);
const protectedPathRoots = [
  paths.dashboard,
  paths.projects,
  paths.submissions,
  paths.documents,
  paths.evaluations,
  paths.repositoryAnalysis,
  paths.deploymentValidation,
  paths.reports,
  paths.settings,
  paths.documentEvaluation,
];

export function isPublicAuthPath(pathname: string) {
  return publicAuthPaths.has(pathname);
}

export function shouldRedirectToLoginAfterAuthCleanup(pathname: string) {
  return protectedPathRoots.some((pathRoot) => pathname === pathRoot || pathname.startsWith(`${pathRoot}/`));
}

export function clearAuthSession(reason: AuthCleanupReason, options: ClearAuthSessionOptions = {}) {
  window.localStorage.removeItem(accessTokenKey);
  window.dispatchEvent(
    new CustomEvent<AuthSessionClearedDetail>(authSessionClearedEvent, {
      detail: { reason },
    }),
  );

  if (options.redirectToLogin && shouldRedirectToLoginAfterAuthCleanup(window.location.pathname)) {
    window.location.assign(paths.login);
  }
}

export function hasRole(user: User | null | undefined, role: Role) {
  return user?.role === role;
}

export function hasAnyRole(user: User | null | undefined, roles: Role[]) {
  return Boolean(user && roles.includes(user.role));
}
