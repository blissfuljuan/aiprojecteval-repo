package com.blissfuljuan.aiprojecteval.ai.provider;

import org.springframework.stereotype.Component;

@Component
public class OpenAIProvider implements AIProvider {

	@Override
	public String providerName() {
		return "openai";
	}

	@Override
	public String generate(String prompt) {
		return "";
	}
}
