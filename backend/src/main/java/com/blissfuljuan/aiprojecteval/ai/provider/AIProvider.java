package com.blissfuljuan.aiprojecteval.ai.provider;

import com.blissfuljuan.aiprojecteval.ai.model.AIProviderType;

public interface AIProvider {

	AIProviderType providerType();

	String providerName();

	String generate(String prompt);
}
