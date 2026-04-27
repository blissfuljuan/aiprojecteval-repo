package com.blissfuljuan.aiprojecteval.courseclass.dto;

import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import java.time.LocalDateTime;

public record CourseClassResponse(
		Long id,
		String name,
		String code,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
	public static CourseClassResponse fromEntity(CourseClass entity) {
		return new CourseClassResponse(
				entity.getId(),
				entity.getName(),
				entity.getCode(),
				entity.getCreatedAt(),
				entity.getUpdatedAt()
		);
	}
}
