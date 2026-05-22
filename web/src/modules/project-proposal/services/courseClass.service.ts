import { api } from "@/common/lib/api";
import type { ApiResponse } from "@/modules/identity/types";
import type { CourseClass } from "@/modules/project-proposal/types";

async function findAll(): Promise<CourseClass[]> {
  const response = await api.get<ApiResponse<CourseClass[]>>("/api/course-classes");
  return response.data.data;
}

async function findMyCourseClasses(): Promise<CourseClass[]> {
  const response = await api.get<ApiResponse<CourseClass[]>>("/api/course-classes/my");
  return response.data.data;
}

export const courseClassService = {
  findAll,
  findMyCourseClasses,
};
