package com.blissfuljuan.aiprojecteval.document.validation;

import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import com.blissfuljuan.aiprojecteval.document.model.DocumentSourceType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
class DocumentLinkValidationServiceImpl implements DocumentLinkValidationService {

	private static final String GOOGLE_DOCS_MIME_TYPE = "application/vnd.google-apps.document";
	private static final String GOOGLE_DRIVE_FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";

	private final GoogleDriveLinkParser linkParser;
	private final GoogleDriveMetadataClient metadataClient;
	private final GoogleDriveProperties properties;

	DocumentLinkValidationServiceImpl(
			GoogleDriveLinkParser linkParser,
			GoogleDriveMetadataClient metadataClient,
			GoogleDriveProperties properties) {
		this.linkParser = linkParser;
		this.metadataClient = metadataClient;
		this.properties = properties;
	}

	@Override
	public DocumentValidationResult validate(DocumentVersion version) {
		if (version.getSourceType() != DocumentSourceType.EXTERNAL_LINK) {
			return DocumentValidationResult.failure(
					DocumentValidationStatus.FAILED,
					"Only external link document versions can be validated",
					version.getProvider(),
					version.getExternalFileId());
		}

		ParsedDocumentLink parsedLink = linkParser.parse(version.getOriginalUrl());
		if (!parsedLink.isValid()) {
			return DocumentValidationResult.failure(
					parsedLink.validationStatus(),
					parsedLink.message(),
					parsedLink.provider(),
					parsedLink.fileId());
		}

		GoogleDriveMetadataResult metadataResult = metadataClient.fetchMetadata(parsedLink.fileId());
		if (!metadataResult.isSuccess()) {
			return DocumentValidationResult.failure(
					metadataResult.status(),
					metadataResult.message(),
					parsedLink.provider(),
					parsedLink.fileId());
		}

		GoogleDriveFileMetadata metadata = metadataResult.metadata();
		DocumentValidationResult failure = validateMetadata(metadata, parsedLink.provider(), parsedLink.fileId());
		if (failure != null) {
			return failure;
		}

		return DocumentValidationResult.valid(
				"Google Drive link validated successfully",
				parsedLink.provider(),
				parsedLink.fileId(),
				metadata);
	}

	private DocumentValidationResult validateMetadata(
			GoogleDriveFileMetadata metadata,
			DocumentProvider provider,
			String fileId) {
		if (metadata == null) {
			return DocumentValidationResult.failure(
					DocumentValidationStatus.FAILED,
					"Google Drive metadata response was empty",
					provider,
					fileId);
		}

		if (GOOGLE_DRIVE_FOLDER_MIME_TYPE.equals(metadata.mimeType())) {
			return DocumentValidationResult.failure(
					DocumentValidationStatus.UNSUPPORTED_FILE_TYPE,
					"Google Drive folders are not supported documents",
					provider,
					fileId);
		}

		Set<String> allowedMimeTypes = Set.copyOf(properties.getAllowedMimeTypes());
		if (!allowedMimeTypes.contains(metadata.mimeType())) {
			return DocumentValidationResult.failure(
					DocumentValidationStatus.UNSUPPORTED_FILE_TYPE,
					"Unsupported Google Drive file type: " + metadata.mimeType(),
					provider,
					fileId);
		}

		if (!Boolean.TRUE.equals(metadata.canDownload())) {
			return DocumentValidationResult.failure(
					DocumentValidationStatus.INACCESSIBLE,
					"Google Drive file is not downloadable or exportable by the configured service account",
					provider,
					fileId);
		}

		Long maxFileSizeBytes = maxFileSizeBytes();
		if (metadata.size() != null && maxFileSizeBytes != null && metadata.size() > maxFileSizeBytes) {
			return DocumentValidationResult.failure(
					DocumentValidationStatus.TOO_LARGE,
					"Google Drive file exceeds the maximum allowed size of " + properties.getMaxFileSizeMb() + " MB",
					provider,
					fileId);
		}

		return null;
	}

	private Long maxFileSizeBytes() {
		if (properties.getMaxFileSizeMb() <= 0) {
			return null;
		}
		return properties.getMaxFileSizeMb() * 1024L * 1024L;
	}
}
