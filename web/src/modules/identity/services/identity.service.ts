import { isAxiosError } from "axios";
import { api } from "@/common/lib/api";
import type { ApiResponse, AuthResponse, LoginRequest, RegisterRequest, User } from "@/modules/identity/types";

async function register(payload: RegisterRequest) {
  const response = await api.post<ApiResponse<AuthResponse>>("/api/auth/register", payload);
  return response.data.data;
}

async function login(payload: LoginRequest) {
  const response = await api.post<ApiResponse<AuthResponse>>("/api/auth/login", payload);
  return response.data.data;
}

async function getCurrentUser() {
  const response = await api.get<ApiResponse<User>>("/api/auth/me");
  return response.data.data;
}

async function logout() {
  await api.post<ApiResponse<null>>("/api/auth/logout");
}

function getErrorMessage(error: unknown) {
  if (isAxiosError<ApiResponse<unknown>>(error)) {
    const data = error.response?.data;
    const firstError = data?.errors?.[0];

    return firstError || data?.message || "Request failed. Please try again.";
  }

  if (error instanceof Error) {
    return error.message;
  }

  return "Request failed. Please try again.";
}

export const identityService = {
  register,
  login,
  getCurrentUser,
  logout,
  getErrorMessage,
};
