import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { accessTokenKey } from "@/common/lib/api";
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

  const persistSession = useCallback((response: AuthResponse) => {
    window.localStorage.setItem(accessTokenKey, response.token);
    setToken(response.token);
    setUser(response.user);
  }, []);

  const clearSession = useCallback(() => {
    identityService.clearLocalSession();
    setToken(null);
    setUser(null);
  }, []);

  const logout = useCallback(async () => {
    try {
      await identityService.logout();
    } catch {
      // Expired or invalid tokens should not block local logout.
    } finally {
      clearSession();
      window.location.assign("/login");
    }
  }, [clearSession]);

  const loadCurrentUser = useCallback(async () => {
    const storedToken = window.localStorage.getItem(accessTokenKey);

    if (!storedToken) {
      setToken(null);
      setUser(null);
      setIsLoading(false);
      return;
    }

    setIsLoading(true);

    try {
      const currentUser = await identityService.getCurrentUser();
      setToken(storedToken);
      setUser(currentUser);
    } catch {
      clearSession();
    } finally {
      setIsLoading(false);
    }
  }, [clearSession]);

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
      persistSession(response);
      return response;
    },
    [persistSession],
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
