package com.blissfuljuan.aiprojecteval.document.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;
import org.junit.jupiter.api.Test;

class GoogleDriveMetadataClientTest {

	@Test
	void shouldReturnFailureWhenCredentialsAreMissing() {
		GoogleDriveMetadataClient client = new GoogleDriveMetadataClient(new GoogleDriveProperties());

		GoogleDriveMetadataResult result = client.fetchMetadata("abc123");

		assertThat(result.status()).isEqualTo(DocumentValidationStatus.FAILED);
		assertThat(result.message()).contains("credentials are not configured");
	}
}
