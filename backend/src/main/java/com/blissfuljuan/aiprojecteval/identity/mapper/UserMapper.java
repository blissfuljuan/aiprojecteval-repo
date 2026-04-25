package com.blissfuljuan.aiprojecteval.identity.mapper;

import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;
import com.blissfuljuan.aiprojecteval.identity.model.User;

public final class UserMapper {

	private UserMapper() {
	}

	public static UserResponse toUserResponse(User user) {
		return new UserResponse(
				user.getId(),
				user.getFirstName(),
				user.getMiddleName(),
				user.getLastName(),
				user.getEmail(),
				user.getRole(),
				user.isEnabled()
		);
	}
}
