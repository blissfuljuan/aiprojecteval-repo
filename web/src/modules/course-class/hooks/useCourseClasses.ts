import { useCallback, useEffect, useState } from "react";
import { courseClassService } from "@/modules/course-class/services/courseClass.service";
import type { CourseClass } from "@/modules/course-class/types";

export function useCourseClasses() {
  const [courseClasses, setCourseClasses] = useState<CourseClass[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refetch = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      setCourseClasses(await courseClassService.findAll());
    } catch (err) {
      setError(courseClassService.getErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    void refetch();
  }, [refetch]);

  return { courseClasses, isLoading, error, refetch };
}
