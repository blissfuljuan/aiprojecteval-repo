export type CourseClass = {
  id: number;
  name: string;
  code: string;
  createdAt: string;
  updatedAt: string;
};

export type CourseClassRequest = {
  name: string;
  code: string;
};
