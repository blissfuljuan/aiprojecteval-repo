package com.blissfuljuan.aiprojecteval.documentevaluation.storage;

import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmission;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentFileStorageService {

	StoredDocumentFile store(MultipartFile file, DocumentSubmission submission);

	Resource loadAsResource(String storagePath);

	void delete(String storagePath);

	boolean exists(String storagePath);
}
