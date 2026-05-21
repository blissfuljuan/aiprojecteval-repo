package com.blissfuljuan.aiprojecteval.document.extraction;

import com.blissfuljuan.aiprojecteval.document.validation.GoogleDriveProperties;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
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
public class GoogleDriveDocumentContentClient {

	private final GoogleDriveProperties properties;
	private Drive drive;

	public GoogleDriveDocumentContentClient(GoogleDriveProperties properties) {
		this.properties = properties;
	}

	public InputStream download(String fileId) {
		try {
			return drive().files()
					.get(fileId)
					.setSupportsAllDrives(true)
					.executeMediaAsInputStream();
		} catch (IOException | GeneralSecurityException exception) {
			throw new DocumentTextExtractionException("Google Drive download failed: " + exception.getMessage(), exception);
		}
	}

	public InputStream export(String fileId, String exportMimeType) {
		try {
			return drive().files()
					.export(fileId, exportMimeType)
					.executeMediaAsInputStream();
		} catch (IOException | GeneralSecurityException exception) {
			throw new DocumentTextExtractionException("Google Docs export failed: " + exception.getMessage(), exception);
		}
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
					.createScoped(List.of(DriveScopes.DRIVE_READONLY));
			return new Drive.Builder(
					GoogleNetHttpTransport.newTrustedTransport(),
					GsonFactory.getDefaultInstance(),
					new HttpCredentialsAdapter(credentials))
					.setApplicationName("aiprojecteval")
					.build();
		}
	}
}
