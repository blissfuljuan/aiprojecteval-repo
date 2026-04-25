package com.blissfuljuan.aiprojecteval.ai.provider;

public interface AIProvider {

	String providerName();

	String generate(String prompt);
}
