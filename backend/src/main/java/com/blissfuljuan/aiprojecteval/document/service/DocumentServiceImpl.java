package com.blissfuljuan.aiprojecteval.document.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentLinkSubmitRequest;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentSummaryResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentVersionResponse;
import com.blissfuljuan.aiprojecteval.document.extraction.DocumentTextExtractionResult;
import com.blissfuljuan.aiprojecteval.document.extraction.DocumentTextExtractionService;
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
import com.blissfuljuan.aiprojecteval.document.validation.DocumentLinkValidationService;
import com.blissfuljuan.aiprojecteval.document.validation.DocumentValidationResult;
import com.blissfuljuan.aiprojecteval.document.validation.GoogleDriveProperties;
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
	private final DocumentLinkValidationService documentLinkValidationService;
	private final GoogleDriveProperties googleDriveProperties;
	private final DocumentTextExtractionService documentTextExtractionService;

	DocumentServiceImpl(
			DocumentRepository documentRepository,
			DocumentVersionRepository documentVersionRepository,
			DocumentMapper documentMapper,
			DocumentLinkValidationService documentLinkValidationService,
			GoogleDriveProperties googleDriveProperties,
			DocumentTextExtractionService documentTextExtractionService) {
		this.documentRepository = documentRepository;
		this.documentVersionRepository = documentVersionRepository;
		this.documentMapper = documentMapper;
		this.documentLinkValidationService = documentLinkValidationService;
		this.googleDriveProperties = googleDriveProperties;
		this.documentTextExtractionService = documentTextExtractionService;
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
		if (googleDriveProperties.isValidationEnabled()) {
			applyValidationResult(version, documentLinkValidationService.validate(version));
			version = documentVersionRepository.save(version);
		}

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
	public DocumentVersionResponse validateVersion(Long documentId, Long versionId) {
		Document document = findDocument(documentId);
		DocumentVersion version = findVersion(versionId);
		ensureVersionBelongsToDocument(document, version);

		applyValidationResult(version, documentLinkValidationService.validate(version));
		version = documentVersionRepository.save(version);

		return documentMapper.toVersionResponse(version);
	}

	@Override
	@Transactional
	public DocumentVersionResponse extractVersionText(Long documentId, Long versionId) {
		Document document = findDocument(documentId);
		DocumentVersion version = findVersion(versionId);
		ensureVersionBelongsToDocument(document, version);
		if (version.getValidationStatus() != DocumentValidationStatus.VALID) {
			throw new BadRequestException("Only VALID document versions can be extracted");
		}

		markExtractionProcessing(version);
		version = documentVersionRepository.saveAndFlush(version);

		try {
			DocumentTextExtractionResult result = documentTextExtractionService.extract(version);
			version.setExtractedText(result.extractedText());
			version.setWordCount(result.wordCount());
			version.setExtractedAt(LocalDateTime.now());
			version.setExtractionErrorMessage(null);
			version.setExtractionStatus(DocumentExtractionStatus.EXTRACTED);
		} catch (Exception exception) {
			version.setExtractedText(null);
			version.setWordCount(null);
			version.setExtractedAt(null);
			version.setExtractionErrorMessage(messageOf(exception));
			version.setExtractionStatus(DocumentExtractionStatus.FAILED);
		}

		version = documentVersionRepository.save(version);
		return documentMapper.toVersionResponse(version);
	}

	@Override
	@Transactional
	public DocumentResponse validateCurrentVersion(Long documentId) {
		Document document = findDocument(documentId);
		DocumentVersion currentVersion = findCurrentVersion(document);
		if (currentVersion == null) {
			throw new ResourceNotFoundException("Document version not found");
		}

		applyValidationResult(currentVersion, documentLinkValidationService.validate(currentVersion));
		currentVersion = documentVersionRepository.save(currentVersion);

		return documentMapper.toResponse(document, currentVersion);
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

	private DocumentVersion findVersion(Long versionId) {
		return documentVersionRepository.findById(versionId)
				.orElseThrow(() -> new ResourceNotFoundException("Document version not found"));
	}

	private void ensureVersionBelongsToDocument(Document document, DocumentVersion version) {
		if (!document.getId().equals(version.getDocument().getId())) {
			throw new ResourceNotFoundException("Document version not found");
		}
	}

	private void applyValidationResult(DocumentVersion version, DocumentValidationResult result) {
		version.setProvider(result.provider());
		version.setExternalFileId(result.externalFileId());
		version.setFileName(result.fileName());
		version.setMimeType(result.mimeType());
		version.setFileSizeBytes(result.fileSizeBytes());
		version.setValidationStatus(result.status());
		version.setValidationMessage(result.message());
		version.setValidatedAt(LocalDateTime.now());
	}

	private void markExtractionProcessing(DocumentVersion version) {
		version.setExtractionStatus(DocumentExtractionStatus.PROCESSING);
		version.setExtractedText(null);
		version.setWordCount(null);
		version.setExtractedAt(null);
		version.setExtractionErrorMessage(null);
	}

	private String messageOf(Exception exception) {
		if (exception.getMessage() == null || exception.getMessage().isBlank()) {
			return "Document text extraction failed";
		}
		return exception.getMessage();
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
