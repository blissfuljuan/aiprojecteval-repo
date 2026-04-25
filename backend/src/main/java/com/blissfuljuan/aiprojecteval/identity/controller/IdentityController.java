package com.blissfuljuan.aiprojecteval.identity.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.identity.dto.IdentityResponse;
import com.blissfuljuan.aiprojecteval.identity.service.IdentityService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/identity")
public class IdentityController {

	private final IdentityService identityService;

	public IdentityController(IdentityService identityService) {
		this.identityService = identityService;
	}

	@GetMapping
	public ApiResponse<List<IdentityResponse>> findAll() {
		return ApiResponse.ok(identityService.findAll());
	}
}
