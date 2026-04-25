package com.blissfuljuan.aiprojecteval.document.mapper;

import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.document.model.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

	public DocumentResponse toResponse(Document document) {
		return new DocumentResponse(document.getId(), null);
	}
}
