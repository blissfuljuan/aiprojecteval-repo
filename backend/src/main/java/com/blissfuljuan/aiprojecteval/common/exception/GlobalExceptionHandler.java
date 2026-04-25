package com.blissfuljuan.aiprojecteval.common.exception;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AppException.class)
	public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
		return ResponseEntity
				.status(exception.getStatus())
				.body(ApiResponse.fail(exception.getMessage(), List.of(exception.getMessage())));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationException(
			MethodArgumentNotValidException exception) {
		List<String> errors = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();

		return ResponseEntity
				.badRequest()
				.body(ApiResponse.fail("Validation failed", errors));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleUnhandledException(
			Exception exception,
			HttpServletRequest request) {
		String error = request.getRequestURI() + ": " + exception.getMessage();

		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.fail("Unexpected server error", List.of(error)));
	}
}
