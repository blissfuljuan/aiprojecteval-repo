package com.blissfuljuan.aiprojecteval.documentevaluation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "document-evaluation.storage")
public class DocumentEvaluationStorageProperties {

	private String provider = "local";

	private String localRoot = "uploads/document-evaluation";

	private long maxFileSize = 10485760L;

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getLocalRoot() {
		return localRoot;
	}

	public void setLocalRoot(String localRoot) {
		this.localRoot = localRoot;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
}
