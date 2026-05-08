package com.blissfuljuan.aiprojecteval.identity.service;

import com.blissfuljuan.aiprojecteval.identity.model.User;

public interface IdentityQueryService {

	User getUserByEmail(String email);

	User getUserById(Long id);
}
