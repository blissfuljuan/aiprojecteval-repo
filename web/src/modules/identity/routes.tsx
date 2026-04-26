import type { RouteObject } from "react-router";
import { LoginPage } from "@/modules/identity/pages/LoginPage";
import { ProfilePage } from "@/modules/identity/pages/ProfilePage";
import { RegisterPage } from "@/modules/identity/pages/RegisterPage";

export const identityRoutes: RouteObject[] = [
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/register",
    element: <RegisterPage />,
  },
  {
    path: "/profile",
    element: <ProfilePage />,
  },
];
