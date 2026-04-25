package com.blissfuljuan.aiprojecteval.common.response;

import java.time.Instant;
import java.util.List;

public record ApiResponse<T>(
		boolean success,
		String message,
		T data,
		List<String> errors,
		Instant timestamp
) {

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(true, "OK", data, List.of(), Instant.now());
	}

	public static <T> ApiResponse<T> ok(String message, T data) {
		return new ApiResponse<>(true, message, data, List.of(), Instant.now());
	}

	public static <T> ApiResponse<T> fail(String message, List<String> errors) {
		return new ApiResponse<>(false, message, null, errors, Instant.now());
	}
}
