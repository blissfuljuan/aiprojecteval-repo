package com.blissfuljuan.aiprojecteval.document.service;

import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class DocumentServiceImpl implements DocumentService {

	@Override
	public List<DocumentResponse> findAll() {
		return List.of();
	}
}
