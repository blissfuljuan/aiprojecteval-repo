import { env } from "@/app/env";

export const appConfig = {
  name: env.appName,
  environment: env.appEnv,
  apiBaseUrl: env.apiBaseUrl,
} as const;
