package com.blissfuljuan.aiprojecteval.document.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.document.service.DocumentService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@GetMapping
	public ApiResponse<List<DocumentResponse>> findAll() {
		return ApiResponse.ok(documentService.findAll());
	}
}
