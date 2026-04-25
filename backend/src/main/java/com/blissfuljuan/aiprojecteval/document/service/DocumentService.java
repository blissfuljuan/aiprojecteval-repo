package com.blissfuljuan.aiprojecteval.document.service;

import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import java.util.List;

public interface DocumentService {

	List<DocumentResponse> findAll();
}
