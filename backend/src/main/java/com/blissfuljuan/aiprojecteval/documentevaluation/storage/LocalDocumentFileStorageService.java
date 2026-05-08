package com.blissfuljuan.aiprojecteval.documentevaluation.storage;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.documentevaluation.config.DocumentEvaluationStorageProperties;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmission;
import com.blissfuljuan.aiprojecteval.documentevaluation.util.FileNameSanitizer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalDocumentFileStorageService implements DocumentFileStorageService {

	private final Path rootDirectory;

	public LocalDocumentFileStorageService(DocumentEvaluationStorageProperties properties) {
		this.rootDirectory = Path.of(properties.getLocalRoot()).toAbsolutePath().normalize();
	}

	@Override
	public StoredDocumentFile store(MultipartFile file, DocumentSubmission submission) {
		String storedFileName = FileNameSanitizer.storedFileName(file.getOriginalFilename());
		Path submissionDirectory = submissionDirectory(submission);
		Path destination = submissionDirectory.resolve(storedFileName).normalize();
		if (!destination.startsWith(rootDirectory)) {
			throw new BadRequestException("Invalid file storage path");
		}

		try {
			Files.createDirectories(submissionDirectory);
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			try (InputStream inputStream = file.getInputStream();
					DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest)) {
				Files.copy(digestInputStream, destination, StandardCopyOption.REPLACE_EXISTING);
			}

			String storagePath = rootDirectory.relativize(destination).toString().replace('\\', '/');
			return new StoredDocumentFile(
					FileNameSanitizer.sanitizeOriginalFileName(file.getOriginalFilename()),
					storedFileName,
					storagePath,
					file.getContentType(),
					file.getSize(),
					FileNameSanitizer.extension(file.getOriginalFilename()),
					HexFormat.of().formatHex(digest.digest()));
		}
		catch (IOException | NoSuchAlgorithmException exception) {
			throw new BadRequestException("Could not store uploaded file");
		}
	}

	@Override
	public Resource loadAsResource(String storagePath) {
		try {
			Path filePath = resolveStoragePath(storagePath);
			Resource resource = new UrlResource(filePath.toUri());
			if (!resource.exists() || !resource.isReadable()) {
				throw new BadRequestException("Stored file is not available");
			}
			return resource;
		}
		catch (IOException exception) {
			throw new BadRequestException("Stored file is not available");
		}
	}

	@Override
	public void delete(String storagePath) {
		try {
			Files.deleteIfExists(resolveStoragePath(storagePath));
		}
		catch (IOException exception) {
			throw new BadRequestException("Could not delete stored file");
		}
	}

	@Override
	public boolean exists(String storagePath) {
		return Files.exists(resolveStoragePath(storagePath));
	}

	private Path resolveStoragePath(String storagePath) {
		if (storagePath == null || storagePath.isBlank()) {
			throw new BadRequestException("Stored file path is missing");
		}
		Path resolvedPath = rootDirectory.resolve(storagePath).normalize();
		if (!resolvedPath.startsWith(rootDirectory)) {
			throw new BadRequestException("Invalid file storage path");
		}
		return resolvedPath;
	}

	private Path submissionDirectory(DocumentSubmission submission) {
		DocumentRequirementSetAssignment assignment = submission.getAssignment();
		Long requirementSetId = assignment == null || assignment.getRequirementSet() == null
				? 0L : assignment.getRequirementSet().getId();
		Long assignmentId = assignment == null ? 0L : assignment.getId();
		Long submissionId = submission.getId() == null ? 0L : submission.getId();
		return rootDirectory
				.resolve("requirement-set-" + requirementSetId)
				.resolve("assignment-" + assignmentId)
				.resolve("submission-" + submissionId)
				.normalize();
	}
}
