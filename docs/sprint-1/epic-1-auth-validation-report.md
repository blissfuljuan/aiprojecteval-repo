# Epic 1 Authentication Stabilization Validation Report

## Summary

Epic 1 was validated against the expected authentication stabilization scope: standardized auth routes, role enforcement, logout behavior, current-user lookup, frontend auth state management, protected routing, and centralized API authorization handling.

Backend authentication is mostly complete and verified by automated tests. Public registration correctly excludes ADMIN at the service layer, JWT-protected endpoints reject missing and invalid tokens, and the expected `/api/auth` routes are present.

Frontend authentication has the expected basic route protection, session restore, token persistence, logout cleanup, and public registration role filtering. However, role-based route restrictions are not enforced because `RequireRole` currently passes all users through, and the centralized API 401 handler clears only the stored token, not the in-memory auth user or navigation state.

Epic 1 Verification Status: PARTIALLY PASSED

## Files Inspected

### Backend

- `backend/src/main/java/com/blissfuljuan/aiprojecteval/identity/controller/AuthController.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/identity/service/AuthService.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/identity/service/AuthServiceImpl.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/identity/dto/RegisterRequest.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/identity/dto/LoginRequest.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/identity/dto/AuthResponse.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/identity/dto/UserResponse.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/identity/model/Role.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/identity/model/User.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/identity/repository/UserRepository.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/common/security/SecurityConfig.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/common/security/JwtAuthenticationFilter.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/common/security/JwtService.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/common/security/CustomUserDetailsService.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/common/exception/GlobalExceptionHandler.java`
- `backend/src/main/java/com/blissfuljuan/aiprojecteval/common/response/ApiResponse.java`

### Frontend

- `web/src/common/lib/api.ts`
- `web/src/common/lib/auth.ts`
- `web/src/common/guards/RequireAuth.tsx`
- `web/src/common/guards/RequireRole.tsx`
- `web/src/routes/ProtectedRoute.tsx`
- `web/src/routes/PublicOnlyRoute.tsx`
- `web/src/app/router.tsx`
- `web/src/modules/dashboard/routes.tsx`
- `web/src/modules/identity/context/AuthContext.tsx`
- `web/src/modules/identity/services/identity.service.ts`
- `web/src/modules/identity/pages/LoginPage.tsx`
- `web/src/modules/identity/pages/RegisterPage.tsx`
- `web/src/modules/identity/types/index.ts`
- `web/package.json`

## Backend Findings

- `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/logout`, and `GET /api/auth/me` are implemented under `/api/auth`.
- `RegisterRequest` and `LoginRequest` use Jakarta validation for required fields, email format, and minimum password length.
- Expected roles are defined: ADMIN, INSTRUCTOR, EVALUATOR, ADVISER, STUDENT.
- Public registration rejects ADMIN in `AuthServiceImpl.register`, so manually submitted ADMIN payloads are blocked by the backend.
- Duplicate email registration is rejected before saving.
- Login delegates credential verification to Spring Security `AuthenticationManager`.
- JWT authentication reads `Authorization: Bearer <token>`, extracts the email, loads user details, validates token expiry/signature, and populates the security context.
- Invalid JWTs are ignored by the filter and then rejected by Spring Security as unauthorized for protected routes.
- `/api/auth/me` returns the authenticated user through the standardized `ApiResponse` envelope.
- `/api/auth/logout` returns a stable success response. It is stateless and does not blacklist JWTs.
- Error responses are generally standardized through `ApiResponse`; Spring Security entry point responses use the same JSON field shape but hard-code `timestamp` as `null`.

## Frontend Findings

- The centralized Axios client attaches `Authorization: Bearer <token>` from local storage.
- Login persists token and user in `AuthContext`.
- Logout calls the backend, clears local auth state, removes the token, and redirects to `/login`.
- Session restore reads the stored token and loads `/api/auth/me`; failure clears local session.
- `ProtectedRoute` redirects unauthenticated users to `/login`.
- `PublicOnlyRoute` redirects authenticated users away from login/register pages.
- Public registration role options are limited to INSTRUCTOR, EVALUATOR, ADVISER, and STUDENT. ADMIN is not selectable.
- Type-level public registration uses `PublicRegistrationRole = Exclude<Role, "ADMIN">`.
- `RequireRole` does not enforce role-based route restrictions; it ignores `allowedRoles` and always renders the nested route.
- The API 401 interceptor removes `accessToken` from local storage, but it does not clear the in-memory `AuthContext.user` value or redirect immediately.
- No frontend unit/integration test framework or `npm test` script is configured, so frontend checks were limited to source inspection, TypeScript build, and lint.

## Tests Added Or Updated

- Updated `AuthControllerTest.shouldRegisterSuccessfully` to verify successful registration with an allowed public role instead of ADMIN.
- Added `AuthServiceImplTest.shouldRejectInvalidCredentials`.
- Added `SecurityIntegrationTest.shouldRejectProtectedEndpointWithInvalidToken`.

Existing backend tests already covered:

- Register success with allowed role.
- Register rejects ADMIN role.
- Register rejects duplicate email.
- Login success.
- `/api/auth/me` returns current user when authenticated.
- `/api/auth/me` rejects unauthenticated request.
- Logout endpoint returns expected success response.
- Protected endpoint rejects missing token.

## Tests Executed

- Backend: `.\mvnw.cmd test`
- Frontend: `npm run build`
- Frontend: `npm run lint`

## Passing Results

- Backend Maven test suite passed: 51 tests, 0 failures, 0 errors, 0 skipped.
- Frontend production build passed.
- Frontend ESLint passed.

## Failed Results

- No automated command failed.

## Remaining Risks

- Frontend role-based restrictions are not actually enforced because `RequireRole` is a no-op.
- API-level 401 handling can leave stale in-memory auth state until another auth-aware operation clears it.
- Logout is stateless; previously issued JWTs remain usable until expiration unless a future blacklist/revocation mechanism is added.
- Spring Security unauthorized/forbidden responses set `timestamp` to `null`, while normal `ApiResponse` errors include a real timestamp.
- Frontend lacks automated tests for auth behavior, so regression coverage is weaker than backend coverage.

## Recommended Fixes

- Implement `RequireRole` using the current `AuthContext.user` and redirect or render an access-denied route for disallowed roles.
- Connect the Axios 401 interceptor to a centralized auth cleanup event or auth store so both token and in-memory user state are cleared consistently.
- Add frontend test tooling and auth-focused tests for registration roles, login persistence, logout cleanup, protected redirects, Authorization headers, and 401 cleanup.
- Consider issuing consistent timestamps in Spring Security entry point responses.
- Decide whether stateless logout is acceptable for Sprint 1 or whether token revocation/short-lived access tokens are required.

## Applied Fix: Centralized 401 Auth Cleanup

Centralized frontend auth cleanup was added through `clearAuthSession`, which removes the persisted access token, dispatches an auth session cleared event, and redirects to `/login` only when the current path matches a protected route root. `AuthProvider` now listens for that cleanup event and resets its in-memory `user`, `token`, loading state, and authenticated status. The Axios 401 interceptor now calls the centralized cleanup helper instead of only removing local storage, and regular logout uses the same cleanup path.

Files changed:

- `web/src/common/lib/auth.ts`
- `web/src/common/lib/api.ts`
- `web/src/modules/identity/context/AuthContext.tsx`
- `web/src/modules/identity/services/identity.service.ts`
- `docs/sprint-1/epic-1-auth-validation-report.md`

Verification:

- Frontend production build: `npm run build`
- Frontend lint: `npm run lint`

Remaining risks:

- No frontend test framework or `npm test` script is configured yet, so the 401 cleanup behavior is verified by source inspection and existing build/lint checks rather than automated frontend tests.
- Logout remains stateless; previously issued JWTs remain usable until expiration unless backend token revocation is added later.

## Final Verification Status

Epic 1 Verification Status: PARTIALLY PASSED
