package com.blissfuljuan.aiprojecteval.document.service;

import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentLinkSubmitRequest;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentSummaryResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentVersionResponse;
import com.blissfuljuan.aiprojecteval.document.mapper.DocumentMapper;
import com.blissfuljuan.aiprojecteval.document.model.Document;
import com.blissfuljuan.aiprojecteval.document.model.DocumentContextType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentExtractionStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentProvider;
import com.blissfuljuan.aiprojecteval.document.model.DocumentSourceType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentValidationStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import com.blissfuljuan.aiprojecteval.document.repository.DocumentRepository;
import com.blissfuljuan.aiprojecteval.document.repository.DocumentVersionRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DocumentServiceImpl implements DocumentService {

	private final DocumentRepository documentRepository;
	private final DocumentVersionRepository documentVersionRepository;
	private final DocumentMapper documentMapper;

	DocumentServiceImpl(
			DocumentRepository documentRepository,
			DocumentVersionRepository documentVersionRepository,
			DocumentMapper documentMapper) {
		this.documentRepository = documentRepository;
		this.documentVersionRepository = documentVersionRepository;
		this.documentMapper = documentMapper;
	}

	@Override
	@Transactional
	public DocumentResponse submitExternalLink(DocumentLinkSubmitRequest request) {
		Long currentUserId = resolveCurrentUserId();
		Document document = documentRepository.findByContextTypeAndContextIdAndDocumentType(
						request.contextType(),
						request.contextId(),
						request.documentType())
				.orElseGet(() -> createDocument(request, currentUserId));

		document.setTitle(request.title());
		document.setDescription(request.description());
		document.setStatus(DocumentStatus.SUBMITTED);
		document = documentRepository.save(document);

		DocumentVersion latestVersion = documentVersionRepository.findTopByDocumentIdOrderByVersionNumberDesc(document.getId())
				.orElse(null);
		DocumentVersion version = createExternalLinkVersion(document, request, latestVersion, currentUserId);
		version = documentVersionRepository.save(version);

		document.setCurrentVersionId(version.getId());
		document = documentRepository.save(document);

		return documentMapper.toResponse(document, version);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentResponse findById(Long documentId) {
		Document document = findDocument(documentId);
		DocumentVersion currentVersion = findCurrentVersion(document);

		return documentMapper.toResponse(document, currentVersion);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentSummaryResponse> findByContext(DocumentContextType contextType, Long contextId) {
		return documentRepository.findByContextTypeAndContextId(contextType, contextId)
				.stream()
				.map(document -> documentMapper.toSummaryResponse(document, findCurrentVersion(document)))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentVersionResponse> findVersions(Long documentId) {
		if (!documentRepository.existsById(documentId)) {
			throw new ResourceNotFoundException("Document not found");
		}

		return documentVersionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId)
				.stream()
				.map(documentMapper::toVersionResponse)
				.toList();
	}

	@Override
	@Transactional
	public void delete(Long documentId) {
		Document document = findDocument(documentId);
		documentVersionRepository.deleteByDocumentId(document.getId());
		documentRepository.delete(document);
	}

	private Document createDocument(DocumentLinkSubmitRequest request, Long currentUserId) {
		Document document = new Document();
		document.setContextType(request.contextType());
		document.setContextId(request.contextId());
		document.setDocumentType(request.documentType());
		document.setTitle(request.title());
		document.setDescription(request.description());
		document.setStatus(DocumentStatus.SUBMITTED);
		document.setCreatedByUserId(currentUserId);
		return document;
	}

	private DocumentVersion createExternalLinkVersion(
			Document document,
			DocumentLinkSubmitRequest request,
			DocumentVersion latestVersion,
			Long currentUserId) {
		DocumentVersion version = new DocumentVersion();
		version.setDocument(document);
		version.setVersionNumber(latestVersion == null ? 1 : latestVersion.getVersionNumber() + 1);
		version.setSourceType(DocumentSourceType.EXTERNAL_LINK);
		version.setProvider(DocumentProvider.UNKNOWN);
		version.setOriginalUrl(request.documentUrl());
		version.setValidationStatus(DocumentValidationStatus.PENDING);
		version.setExtractionStatus(DocumentExtractionStatus.NOT_STARTED);
		version.setSubmittedByUserId(currentUserId);
		version.setSubmittedAt(LocalDateTime.now());
		return version;
	}

	private Document findDocument(Long documentId) {
		return documentRepository.findById(documentId)
				.orElseThrow(() -> new ResourceNotFoundException("Document not found"));
	}

	private DocumentVersion findCurrentVersion(Document document) {
		if (document.getCurrentVersionId() != null) {
			return documentVersionRepository.findById(document.getCurrentVersionId())
					.orElse(null);
		}

		return documentVersionRepository.findTopByDocumentIdOrderByVersionNumberDesc(document.getId())
				.orElse(null);
	}

	private Long resolveCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getName() == null) {
			return null;
		}
		try {
			return Long.valueOf(authentication.getName());
		} catch (NumberFormatException exception) {
			// Existing authentication names are emails; keep the field nullable until identity exposes an ID principal.
			return null;
		}
	}
}
