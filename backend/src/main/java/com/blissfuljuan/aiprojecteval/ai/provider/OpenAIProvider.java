package com.blissfuljuan.aiprojecteval.ai.provider;

import com.blissfuljuan.aiprojecteval.ai.model.AIProviderType;
import org.springframework.stereotype.Component;

@Component
public class OpenAIProvider implements AIProvider {

	@Override
	public AIProviderType providerType() {
		return AIProviderType.OPENAI;
	}

	@Override
	public String providerName() {
		return "openai";
	}

	@Override
	public String generate(String prompt) {
		return "";
	}
}
