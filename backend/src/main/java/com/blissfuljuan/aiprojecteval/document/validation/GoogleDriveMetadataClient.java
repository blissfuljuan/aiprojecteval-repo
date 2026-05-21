package com.blissfuljuan.aiprojecteval.document.validation;

import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class GoogleDriveMetadataClient {

	private static final String METADATA_FIELDS = "id,name,mimeType,size,webViewLink,capabilities/canDownload";

	private final GoogleDriveProperties properties;
	private Drive drive;

	public GoogleDriveMetadataClient(GoogleDriveProperties properties) {
		this.properties = properties;
	}

	public GoogleDriveMetadataResult fetchMetadata(String fileId) {
		try {
			File file = drive().files()
					.get(fileId)
					.setFields(METADATA_FIELDS)
					.setSupportsAllDrives(true)
					.execute();

			Boolean canDownload = file.getCapabilities() == null ? null : file.getCapabilities().getCanDownload();
			return GoogleDriveMetadataResult.success(new GoogleDriveFileMetadata(
					file.getId(),
					file.getName(),
					file.getMimeType(),
					file.getSize(),
					file.getWebViewLink(),
					canDownload));
		} catch (GoogleJsonResponseException exception) {
			return mapGoogleApiException(exception);
		} catch (IllegalStateException exception) {
			return GoogleDriveMetadataResult.failure(DocumentValidationStatus.FAILED, exception.getMessage());
		} catch (IOException exception) {
			return GoogleDriveMetadataResult.failure(
					DocumentValidationStatus.FAILED,
					"Google Drive metadata request failed: " + exception.getMessage());
		} catch (GeneralSecurityException exception) {
			return GoogleDriveMetadataResult.failure(
					DocumentValidationStatus.FAILED,
					"Google Drive client could not be initialized: " + exception.getMessage());
		}
	}

	private GoogleDriveMetadataResult mapGoogleApiException(GoogleJsonResponseException exception) {
		int statusCode = exception.getStatusCode();
		if (statusCode == 403 || statusCode == 404) {
			return GoogleDriveMetadataResult.failure(
					DocumentValidationStatus.INACCESSIBLE,
					"Google Drive file is not accessible to the configured service account");
		}
		if (statusCode == 400 || statusCode == 401) {
			return GoogleDriveMetadataResult.failure(
					DocumentValidationStatus.FAILED,
					"Google Drive credentials or request configuration is invalid");
		}
		return GoogleDriveMetadataResult.failure(
				DocumentValidationStatus.FAILED,
				"Google Drive API error: " + exception.getStatusMessage());
	}

	private Drive drive() throws GeneralSecurityException, IOException {
		if (drive == null) {
			drive = createDriveClient();
		}
		return drive;
	}

	private Drive createDriveClient() throws GeneralSecurityException, IOException {
		if (!StringUtils.hasText(properties.getServiceAccountJsonPath())) {
			throw new IllegalStateException("Google Drive service account credentials are not configured");
		}

		Path credentialsPath = Path.of(properties.getServiceAccountJsonPath());
		if (!Files.exists(credentialsPath)) {
			throw new IllegalStateException("Google Drive service account credentials file was not found");
		}

		try (InputStream credentialsStream = Files.newInputStream(credentialsPath)) {
			GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
					.createScoped(List.of(DriveScopes.DRIVE_METADATA_READONLY));
			return new Drive.Builder(
					GoogleNetHttpTransport.newTrustedTransport(),
					GsonFactory.getDefaultInstance(),
					new HttpCredentialsAdapter(credentials))
					.setApplicationName("aiprojecteval")
					.build();
		}
	}
}
