package com.blissfuljuan.aiprojecteval.ai.provider;

import org.springframework.stereotype.Component;

@Component
public class OllamaProvider implements AIProvider {

	@Override
	public String providerName() {
		return "ollama";
	}

	@Override
	public String generate(String prompt) {
		return "";
	}
}
