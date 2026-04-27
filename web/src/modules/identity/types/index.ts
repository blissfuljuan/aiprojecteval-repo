export type Role = "ADMIN" | "INSTRUCTOR" | "EVALUATOR" | "ADVISER" | "STUDENT";

export type PublicRegistrationRole = Exclude<Role, "ADMIN">;

export type User = {
  id: number;
  firstName: string;
  middleName: string | null;
  lastName: string;
  email: string;
  role: Role;
  enabled: boolean;
};

export type RegisterRequest = {
  firstName: string;
  middleName: string | null;
  lastName: string;
  email: string;
  password: string;
  role: PublicRegistrationRole;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type AuthResponse = {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: User;
};

export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  errors: string[];
  timestamp: string | null;
};
