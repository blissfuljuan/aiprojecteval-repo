package com.blissfuljuan.aiprojecteval.courseclass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseClassEnrollmentRequest(
		@NotBlank(message = "Class code is required")
		@Size(max = 50, message = "Class code must not exceed 50 characters")
		String code
) {
}
