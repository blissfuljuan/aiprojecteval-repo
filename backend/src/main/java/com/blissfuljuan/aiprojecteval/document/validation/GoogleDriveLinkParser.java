package com.blissfuljuan.aiprojecteval.document.validation;

import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class GoogleDriveLinkParser {

	public ParsedDocumentLink parse(String url) {
		if (!StringUtils.hasText(url)) {
			return ParsedDocumentLink.invalid("Document URL is required");
		}

		URI uri;
		try {
			uri = URI.create(url.trim());
		} catch (IllegalArgumentException exception) {
			return ParsedDocumentLink.invalid("Document URL is malformed");
		}

		String host = uri.getHost();
		if (!StringUtils.hasText(host)) {
			return ParsedDocumentLink.invalid("Document URL must include a Google host");
		}

		host = host.toLowerCase();
		if ("drive.google.com".equals(host)) {
			return parseDriveUrl(uri);
		}
		if ("docs.google.com".equals(host)) {
			return parseDocsUrl(uri);
		}

		return ParsedDocumentLink.invalid("Only Google Drive and Google Docs links are supported");
	}

	private ParsedDocumentLink parseDriveUrl(URI uri) {
		String path = uri.getPath();
		if (!StringUtils.hasText(path)) {
			return ParsedDocumentLink.invalid("Google Drive URL is missing a file path");
		}

		String[] segments = splitPath(path);
		if (segments.length >= 3 && "file".equals(segments[0]) && "d".equals(segments[1])) {
			return validFileId(DocumentProvider.GOOGLE_DRIVE, segments[2]);
		}

		if (segments.length == 1 && ("open".equals(segments[0]) || "uc".equals(segments[0]))) {
			return queryParameter(uri, "id")
					.map(fileId -> validFileId(DocumentProvider.GOOGLE_DRIVE, fileId))
					.orElseGet(() -> ParsedDocumentLink.invalid("Google Drive URL is missing the id query parameter"));
		}

		return ParsedDocumentLink.invalid("Unsupported Google Drive URL format");
	}

	private ParsedDocumentLink parseDocsUrl(URI uri) {
		String path = uri.getPath();
		if (!StringUtils.hasText(path)) {
			return ParsedDocumentLink.invalid("Google Docs URL is missing a document path");
		}

		String[] segments = splitPath(path);
		if (segments.length >= 3 && "document".equals(segments[0]) && "d".equals(segments[1])) {
			return validFileId(DocumentProvider.GOOGLE_DOCS, segments[2]);
		}

		return ParsedDocumentLink.invalid("Only Google Docs document links are supported");
	}

	private ParsedDocumentLink validFileId(DocumentProvider provider, String fileId) {
		if (!StringUtils.hasText(fileId)) {
			return ParsedDocumentLink.invalid("Google file id is missing");
		}
		return ParsedDocumentLink.valid(provider, fileId);
	}

	private String[] splitPath(String path) {
		return Arrays.stream(path.split("/"))
				.filter(StringUtils::hasText)
				.toArray(String[]::new);
	}

	private Optional<String> queryParameter(URI uri, String parameterName) {
		if (!StringUtils.hasText(uri.getRawQuery())) {
			return Optional.empty();
		}

		return Arrays.stream(uri.getRawQuery().split("&"))
				.map(pair -> pair.split("=", 2))
				.filter(parts -> parts.length == 2 && parameterName.equals(parts[0]))
				.map(parts -> URLDecoder.decode(parts[1], StandardCharsets.UTF_8))
				.filter(StringUtils::hasText)
				.findFirst();
	}
}
