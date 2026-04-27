import axios from "axios";
import { env } from "@/app/env";
import { accessTokenKey, clearAuthSession } from "@/common/lib/auth";

export const api = axios.create({
  baseURL: env.apiBaseUrl,
});

api.interceptors.request.use((config) => {
  const token = window.localStorage.getItem(accessTokenKey);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearAuthSession("unauthorized", { redirectToLogin: true });
    }

    return Promise.reject(error);
  },
);
