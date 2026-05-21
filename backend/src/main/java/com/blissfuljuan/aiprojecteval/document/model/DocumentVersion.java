package com.blissfuljuan.aiprojecteval.document.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_versions")
public class DocumentVersion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "document_id", nullable = false)
	private Document document;

	@Column(nullable = false)
	private Integer versionNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentSourceType sourceType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentProvider provider;

	@Column(columnDefinition = "TEXT")
	private String originalUrl;

	private String externalFileId;

	private String storageProvider;

	private String storageKey;

	private String fileName;

	private String mimeType;

	private Long fileSizeBytes;

	private String checksum;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentValidationStatus validationStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentExtractionStatus extractionStatus;

	@Column(columnDefinition = "TEXT")
	private String extractedText;

	private Integer wordCount;

	private Long submittedByUserId;

	@Column(nullable = false)
	private LocalDateTime submittedAt;

	private LocalDateTime validatedAt;

	private LocalDateTime extractedAt;

	@Column(columnDefinition = "TEXT")
	private String validationMessage;

	@Column(columnDefinition = "TEXT")
	private String extractionErrorMessage;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Integer getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
	}

	public DocumentSourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(DocumentSourceType sourceType) {
		this.sourceType = sourceType;
	}

	public DocumentProvider getProvider() {
		return provider;
	}

	public void setProvider(DocumentProvider provider) {
		this.provider = provider;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getExternalFileId() {
		return externalFileId;
	}

	public void setExternalFileId(String externalFileId) {
		this.externalFileId = externalFileId;
	}

	public String getStorageProvider() {
		return storageProvider;
	}

	public void setStorageProvider(String storageProvider) {
		this.storageProvider = storageProvider;
	}

	public String getStorageKey() {
		return storageKey;
	}

	public void setStorageKey(String storageKey) {
		this.storageKey = storageKey;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Long getFileSizeBytes() {
		return fileSizeBytes;
	}

	public void setFileSizeBytes(Long fileSizeBytes) {
		this.fileSizeBytes = fileSizeBytes;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public DocumentValidationStatus getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(DocumentValidationStatus validationStatus) {
		this.validationStatus = validationStatus;
	}

	public DocumentExtractionStatus getExtractionStatus() {
		return extractionStatus;
	}

	public void setExtractionStatus(DocumentExtractionStatus extractionStatus) {
		this.extractionStatus = extractionStatus;
	}

	public String getExtractedText() {
		return extractedText;
	}

	public void setExtractedText(String extractedText) {
		this.extractedText = extractedText;
	}

	public Integer getWordCount() {
		return wordCount;
	}

	public void setWordCount(Integer wordCount) {
		this.wordCount = wordCount;
	}

	public Long getSubmittedByUserId() {
		return submittedByUserId;
	}

	public void setSubmittedByUserId(Long submittedByUserId) {
		this.submittedByUserId = submittedByUserId;
	}

	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}

	public LocalDateTime getValidatedAt() {
		return validatedAt;
	}

	public void setValidatedAt(LocalDateTime validatedAt) {
		this.validatedAt = validatedAt;
	}

	public LocalDateTime getExtractedAt() {
		return extractedAt;
	}

	public void setExtractedAt(LocalDateTime extractedAt) {
		this.extractedAt = extractedAt;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}

	public String getExtractionErrorMessage() {
		return extractionErrorMessage;
	}

	public void setExtractionErrorMessage(String extractionErrorMessage) {
		this.extractionErrorMessage = extractionErrorMessage;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
