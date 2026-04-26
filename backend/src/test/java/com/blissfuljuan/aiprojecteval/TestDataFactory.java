package com.blissfuljuan.aiprojecteval;

import com.blissfuljuan.aiprojecteval.identity.dto.AuthResponse;
import com.blissfuljuan.aiprojecteval.identity.dto.LoginRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.RegisterRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectRequest;
import com.blissfuljuan.aiprojecteval.project.dto.ProjectResponse;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import java.time.LocalDateTime;

public final class TestDataFactory {

	private TestDataFactory() {
	}

	public static User createUser(Role role) {
		User user = new User("Admin", null, "User", "admin@example.com", "encoded-password", role);
		user.setId(1L);
		return user;
	}

	public static RegisterRequest createRegisterRequest(Role role) {
		return new RegisterRequest("Admin", null, "User", "admin@example.com", "password123", role);
	}

	public static LoginRequest createLoginRequest() {
		return new LoginRequest("admin@example.com", "password123");
	}

	public static UserResponse createUserResponse(Role role) {
		return new UserResponse(1L, "Admin", null, "User", "admin@example.com", role, true);
	}

	public static AuthResponse createAuthResponse(Role role) {
		return new AuthResponse("jwt-token", "Bearer", 86400000L, createUserResponse(role));
	}

	public static Project createProject(Long ownerUserId) {
		Project project = new Project(
				ownerUserId,
				"admin@example.com",
				"Capstone Portal",
				"AI-assisted project evaluation system",
				"https://github.com/example/capstone-portal"
		);
		project.setId(1L);
		project.setCreatedAt(LocalDateTime.of(2026, 4, 26, 10, 0));
		project.setUpdatedAt(LocalDateTime.of(2026, 4, 26, 10, 0));
		return project;
	}

	public static ProjectRequest createProjectRequest() {
		return new ProjectRequest(
				"Capstone Portal",
				"AI-assisted project evaluation system",
				"https://github.com/example/capstone-portal"
		);
	}

	public static ProjectResponse createProjectResponse() {
		return new ProjectResponse(
				1L,
				1L,
				"admin@example.com",
				"Capstone Portal",
				"AI-assisted project evaluation system",
				"https://github.com/example/capstone-portal",
				LocalDateTime.of(2026, 4, 26, 10, 0),
				LocalDateTime.of(2026, 4, 26, 10, 0)
		);
	}
}
