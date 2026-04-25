package com.blissfuljuan.aiprojecteval.identity.service;

import com.blissfuljuan.aiprojecteval.identity.dto.IdentityResponse;
import java.util.List;

public interface IdentityService {

	List<IdentityResponse> findAll();
}
