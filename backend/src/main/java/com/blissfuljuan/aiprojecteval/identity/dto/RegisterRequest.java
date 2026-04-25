package com.blissfuljuan.aiprojecteval.identity.dto;

import com.blissfuljuan.aiprojecteval.identity.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank String firstName,
		String middleName,
		@NotBlank String lastName,
		@NotBlank @Email String email,
		@NotBlank @Size(min = 8) String password,
		@NotNull Role role
) {
}
