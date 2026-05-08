package com.blissfuljuan.aiprojecteval.documentevaluation.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

public final class FileNameSanitizer {

	private FileNameSanitizer() {
	}

	public static String sanitizeOriginalFileName(String originalFileName) {
		if (originalFileName == null || originalFileName.isBlank()) {
			return "document";
		}

		String filename = originalFileName.replace('\\', '/');
		int slashIndex = filename.lastIndexOf('/');
		if (slashIndex >= 0) {
			filename = filename.substring(slashIndex + 1);
		}

		String normalized = Normalizer.normalize(filename, Normalizer.Form.NFKD)
				.replaceAll("[^\\p{ASCII}]", "")
				.replaceAll("[^A-Za-z0-9._-]", "_")
				.replaceAll("_+", "_")
				.replaceAll("^\\.+", "")
				.replaceAll("\\.+$", "");

		if (normalized.isBlank()) {
			return "document";
		}
		if (normalized.length() > 120) {
			return normalized.substring(0, 120);
		}
		return normalized;
	}

	public static String storedFileName(String originalFileName) {
		return "file-" + UUID.randomUUID() + "-" + sanitizeOriginalFileName(originalFileName);
	}

	public static String extension(String originalFileName) {
		String sanitized = sanitizeOriginalFileName(originalFileName);
		int dotIndex = sanitized.lastIndexOf('.');
		if (dotIndex < 0 || dotIndex == sanitized.length() - 1) {
			return "";
		}
		return sanitized.substring(dotIndex + 1).toUpperCase(Locale.ROOT);
	}
}
