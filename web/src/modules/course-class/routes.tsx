import type { RouteObject } from "react-router";
import { CourseClassCreatePage } from "@/modules/course-class/pages/CourseClassCreatePage";
import { CourseClassEditPage } from "@/modules/course-class/pages/CourseClassEditPage";
import { CourseClassListPage } from "@/modules/course-class/pages/CourseClassListPage";
import { paths } from "@/routes/paths";

export const courseClassRoutes: RouteObject[] = [
  {
    path: paths.courseClasses,
    element: <CourseClassListPage />,
  },
  {
    path: paths.courseClassCreate,
    element: <CourseClassCreatePage />,
  },
  {
    path: paths.courseClassEdit(":courseClassId"),
    element: <CourseClassEditPage />,
  },
];
