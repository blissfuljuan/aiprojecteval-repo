import { isAxiosError } from "axios";
import { api } from "@/common/lib/api";
import type { ApiResponse } from "@/modules/identity/types";
import type { Project, ProjectRequest } from "@/modules/project/types";

async function findAll(): Promise<Project[]> {
  const response = await api.get<ApiResponse<Project[]>>("/api/projects");
  return response.data.data;
}

async function findById(id: number): Promise<Project> {
  const response = await api.get<ApiResponse<Project>>(`/api/projects/${id}`);
  return response.data.data;
}

async function create(request: ProjectRequest): Promise<Project> {
  const response = await api.post<ApiResponse<Project>>("/api/projects", request);
  return response.data.data;
}

function getErrorMessage(error: unknown): string {
  if (isAxiosError<ApiResponse<unknown>>(error)) {
    const data = error.response?.data;
    return data?.errors?.[0] ?? data?.message ?? "Request failed. Please try again.";
  }
  if (error instanceof Error) return error.message;
  return "Request failed. Please try again.";
}

export const projectService = {
  findAll,
  findById,
  create,
  getErrorMessage,
};
