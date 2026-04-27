package com.blissfuljuan.aiprojecteval.courseclass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseClassRequest(
		@NotBlank(message = "Name is required")
		@Size(max = 150, message = "Name must not exceed 150 characters")
		String name,

		@Size(max = 50, message = "Code must not exceed 50 characters")
		String code
) {
}
