package com.blissfuljuan.aiprojecteval.ai.service;

import com.blissfuljuan.aiprojecteval.ai.dto.AIResponse;
import com.blissfuljuan.aiprojecteval.ai.provider.AIProvider;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class AIServiceImpl implements AIService {

	private final List<AIProvider> providers;

	AIServiceImpl(List<AIProvider> providers) {
		this.providers = providers;
	}

	@Override
	public List<AIResponse> availableProviders() {
		return providers.stream()
				.map(provider -> new AIResponse(provider.providerName()))
				.toList();
	}
}
