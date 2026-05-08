package com.blissfuljuan.aiprojecteval.submission.storage;

import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

	StoredFile store(MultipartFile file, Submission submission);

	Resource loadAsResource(String storagePath);

	void delete(String storagePath);

	boolean exists(String storagePath);
}
