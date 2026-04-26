import axios from "axios";
import { env } from "@/app/env";

export const api = axios.create({
  baseURL: env.apiBaseUrl,
});
