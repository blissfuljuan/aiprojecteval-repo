package com.blissfuljuan.aiprojecteval.identity.service;

import com.blissfuljuan.aiprojecteval.identity.dto.IdentityResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class IdentityServiceImpl implements IdentityService {

	@Override
	public List<IdentityResponse> findAll() {
		return List.of();
	}
}
