import { isAxiosError } from "axios";
import { api } from "@/common/lib/api";
import type { ApiResponse } from "@/modules/identity/types";
import type { CourseClass } from "@/modules/course-class/types";

async function enrollByCode(code: string): Promise<CourseClass> {
  const response = await api.post<ApiResponse<CourseClass>>("/api/course-classes/enroll", { code });
  return response.data.data;
}

async function findMyCourseClasses(): Promise<CourseClass[]> {
  const response = await api.get<ApiResponse<CourseClass[]>>("/api/course-classes/my");
  return response.data.data;
}

async function unenroll(courseClassId: number): Promise<void> {
  await api.delete(`/api/course-classes/my/${courseClassId}`);
}

function getErrorMessage(error: unknown): string {
  if (isAxiosError<ApiResponse<unknown>>(error)) {
    const data = error.response?.data;
    return data?.errors?.[0] ?? data?.message ?? "Request failed. Please try again.";
  }
  if (error instanceof Error) return error.message;
  return "Request failed. Please try again.";
}

export const studentCourseClassService = {
  enrollByCode,
  findMyCourseClasses,
  unenroll,
  getErrorMessage,
};
