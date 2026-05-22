import { isAxiosError } from "axios";
import { api } from "@/common/lib/api";
import type { ApiResponse } from "@/modules/identity/types";
import type { CourseClass, CourseClassRequest } from "@/modules/course-class/types";

const endpoint = "/api/course-classes";

async function findAll(): Promise<CourseClass[]> {
  const response = await api.get<ApiResponse<CourseClass[]>>(endpoint);
  return response.data.data;
}

async function findById(id: number): Promise<CourseClass> {
  const response = await api.get<ApiResponse<CourseClass>>(`${endpoint}/${id}`);
  return response.data.data;
}

async function create(request: CourseClassRequest): Promise<CourseClass> {
  const response = await api.post<ApiResponse<CourseClass>>(endpoint, request);
  return response.data.data;
}

async function update(id: number, request: CourseClassRequest): Promise<CourseClass> {
  const response = await api.put<ApiResponse<CourseClass>>(`${endpoint}/${id}`, request);
  return response.data.data;
}

async function deleteCourseClass(id: number): Promise<void> {
  await api.delete(`${endpoint}/${id}`);
}

function getErrorMessage(error: unknown): string {
  if (isAxiosError<ApiResponse<unknown>>(error)) {
    const data = error.response?.data;
    return data?.errors?.[0] ?? data?.message ?? "Request failed. Please try again.";
  }
  if (error instanceof Error) return error.message;
  return "Request failed. Please try again.";
}

export const courseClassService = {
  findAll,
  findById,
  create,
  update,
  delete: deleteCourseClass,
  getErrorMessage,
};
