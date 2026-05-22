import { useEffect, useState } from "react";
import { courseClassService } from "@/modules/course-class/services/courseClass.service";
import type { CourseClass } from "@/modules/course-class/types";

export function useCourseClass(courseClassId: number) {
  const [courseClass, setCourseClass] = useState<CourseClass | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!courseClassId) {
      setCourseClass(null);
      setIsLoading(false);
      setError("Course class not found.");
      return;
    }

    const fetch = async () => {
      try {
        setIsLoading(true);
        setError(null);
        setCourseClass(await courseClassService.findById(courseClassId));
      } catch (err) {
        setError(courseClassService.getErrorMessage(err));
      } finally {
        setIsLoading(false);
      }
    };

    void fetch();
  }, [courseClassId]);

  return { courseClass, isLoading, error };
}
