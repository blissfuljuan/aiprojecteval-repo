import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { accessTokenKey, authSessionClearedEvent, clearAuthSession, type AuthCleanupReason } from "@/common/lib/auth";
import { identityService } from "@/modules/identity/services/identity.service";
import type { AuthResponse, LoginRequest, RegisterRequest, User } from "@/modules/identity/types";

type AuthContextValue = {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (payload: LoginRequest) => Promise<AuthResponse>;
  register: (payload: RegisterRequest) => Promise<AuthResponse>;
  logout: () => Promise<void>;
  loadCurrentUser: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

type AuthProviderProps = {
  children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(() => window.localStorage.getItem(accessTokenKey));
  const [isLoading, setIsLoading] = useState(true);

  const resetAuthState = useCallback(() => {
    setToken(null);
    setUser(null);
    setIsLoading(false);
  }, []);

  const persistSession = useCallback((response: AuthResponse) => {
    window.localStorage.setItem(accessTokenKey, response.token);
    setToken(response.token);
    setUser(response.user);
  }, []);

  const clearSession = useCallback(
    (reason: AuthCleanupReason, options?: Parameters<typeof clearAuthSession>[1]) => {
      clearAuthSession(reason, options);
      resetAuthState();
    },
    [resetAuthState],
  );

  const logout = useCallback(async () => {
    try {
      await identityService.logout();
    } catch {
      // Expired or invalid tokens should not block local logout.
    } finally {
      clearSession("logout", { redirectToLogin: true });
    }
  }, [clearSession]);

  const loadCurrentUser = useCallback(async () => {
    const storedToken = window.localStorage.getItem(accessTokenKey);

    if (!storedToken) {
      resetAuthState();
      return;
    }

    setIsLoading(true);

    try {
      const currentUser = await identityService.getCurrentUser();
      setToken(storedToken);
      setUser(currentUser);
    } catch {
      clearSession("session-restore-failed");
    } finally {
      setIsLoading(false);
    }
  }, [clearSession, resetAuthState]);

  useEffect(() => {
    window.addEventListener(authSessionClearedEvent, resetAuthState);

    return () => {
      window.removeEventListener(authSessionClearedEvent, resetAuthState);
    };
  }, [resetAuthState]);

  useEffect(() => {
    void loadCurrentUser();
  }, [loadCurrentUser]);

  const login = useCallback(
    async (payload: LoginRequest) => {
      const response = await identityService.login(payload);
      persistSession(response);
      return response;
    },
    [persistSession],
  );

  const register = useCallback(
    async (payload: RegisterRequest) => {
      const response = await identityService.register(payload);
      return response;
    },
    [],
  );

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      token,
      isAuthenticated: Boolean(user && token),
      isLoading,
      login,
      register,
      logout,
      loadCurrentUser,
    }),
    [isLoading, loadCurrentUser, login, logout, register, token, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }

  return context;
}
