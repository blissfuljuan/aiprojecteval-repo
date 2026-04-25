package com.blissfuljuan.aiprojecteval.identity.mapper;

import com.blissfuljuan.aiprojecteval.identity.dto.IdentityResponse;
import com.blissfuljuan.aiprojecteval.identity.model.Identity;
import org.springframework.stereotype.Component;

@Component
public class IdentityMapper {

	public IdentityResponse toResponse(Identity identity) {
		return new IdentityResponse(identity.getId(), null);
	}
}
