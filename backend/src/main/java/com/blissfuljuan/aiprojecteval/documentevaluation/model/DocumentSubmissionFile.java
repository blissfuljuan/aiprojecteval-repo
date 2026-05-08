package com.blissfuljuan.aiprojecteval.documentevaluation.model;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionFileStatus;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_submission_files")
public class DocumentSubmissionFile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "submission_id", nullable = false)
	private DocumentSubmission submission;

	@NotBlank
	@Size(max = 255)
	@Column(nullable = false)
	private String originalFileName;

	@Size(max = 255)
	@Column
	private String storedFileName;

	@Size(max = 1000)
	@Column(length = 1000)
	private String fileUrl;

	@Size(max = 1000)
	@Column(length = 1000)
	private String storagePath;

	@Size(max = 150)
	@Column(length = 150)
	private String contentType;

	@Min(0)
	@Column
	private Long fileSize;

	@Size(max = 20)
	@Column(length = 20)
	private String fileExtension;

	@Size(max = 255)
	@Column
	private String checksum;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentSubmissionFileStatus fileStatus;

	@Column
	private LocalDateTime uploadedAt;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public DocumentSubmissionFile() {
	}

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

	public DocumentSubmission getSubmission() {
		return submission;
	}

	public void setSubmission(DocumentSubmission submission) {
		this.submission = submission;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getStoredFileName() {
		return storedFileName;
	}

	public void setStoredFileName(String storedFileName) {
		this.storedFileName = storedFileName;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public DocumentSubmissionFileStatus getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(DocumentSubmissionFileStatus fileStatus) {
		this.fileStatus = fileStatus;
	}

	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(LocalDateTime uploadedAt) {
		this.uploadedAt = uploadedAt;
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
