package com.blissfuljuan.aiprojecteval.ai.provider;

import com.blissfuljuan.aiprojecteval.ai.model.AIProviderType;
import org.springframework.stereotype.Component;

@Component
public class OllamaProvider implements AIProvider {

	@Override
	public AIProviderType providerType() {
		return AIProviderType.OLLAMA;
	}

	@Override
	public String providerName() {
		return "ollama";
	}

	@Override
	public String generate(String prompt) {
		return "";
	}
}
