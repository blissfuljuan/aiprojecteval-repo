package com.blissfuljuan.aiprojecteval.document.validation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "document.google-drive")
public class GoogleDriveProperties {

	private boolean validationEnabled;
	private int maxFileSizeMb = 10;
	private String serviceAccountJsonPath;
	private List<String> allowedMimeTypes = new ArrayList<>(List.of(
			"application/pdf",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"application/vnd.google-apps.document"
	));

	public boolean isValidationEnabled() {
		return validationEnabled;
	}

	public void setValidationEnabled(boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
	}

	public int getMaxFileSizeMb() {
		return maxFileSizeMb;
	}

	public void setMaxFileSizeMb(int maxFileSizeMb) {
		this.maxFileSizeMb = maxFileSizeMb;
	}

	public String getServiceAccountJsonPath() {
		return serviceAccountJsonPath;
	}

	public void setServiceAccountJsonPath(String serviceAccountJsonPath) {
		this.serviceAccountJsonPath = serviceAccountJsonPath;
	}

	public List<String> getAllowedMimeTypes() {
		return allowedMimeTypes;
	}

	public void setAllowedMimeTypes(List<String> allowedMimeTypes) {
		this.allowedMimeTypes = allowedMimeTypes;
	}
}
