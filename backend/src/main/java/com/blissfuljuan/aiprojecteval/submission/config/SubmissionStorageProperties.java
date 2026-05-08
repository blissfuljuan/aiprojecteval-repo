package com.blissfuljuan.aiprojecteval.submission.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "submission.storage")
public class SubmissionStorageProperties {

	private String provider = "local";

	private String localRoot = "uploads/submissions";

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
