import type { RouteObject } from "react-router";
import { MyCourseClassesPage } from "@/modules/student-course-class/pages/MyCourseClassesPage";
import { paths } from "@/routes/paths";

export const studentCourseClassRoutes: RouteObject[] = [
  {
    path: paths.myCourseClasses,
    element: <MyCourseClassesPage />,
  },
];
