package com.blissfuljuan.aiprojecteval.ai.config;

import com.blissfuljuan.aiprojecteval.ai.model.AIProviderType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.ai")
public class AIProperties {

	private AIProviderType provider = AIProviderType.MOCK;
	private String model = "mock-project-proposal-evaluator-v1";
	private String apiKey = "";
	private double temperature = 0.2;
	private int maxOutputTokens = 2048;
	private int timeoutSeconds = 30;

	public AIProviderType getProvider() {
		return provider;
	}

	public void setProvider(AIProviderType provider) {
		this.provider = provider;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public int getMaxOutputTokens() {
		return maxOutputTokens;
	}

	public void setMaxOutputTokens(int maxOutputTokens) {
		this.maxOutputTokens = maxOutputTokens;
	}

	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}
}
