package com.blissfuljuan.aiprojecteval.ai.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_evaluations")
public class AIEvaluation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AIEvaluationType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AIEvaluationStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AIProviderType provider;

	@Column(nullable = false)
	private String model;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String promptSnapshot;

	@Column(columnDefinition = "TEXT")
	private String rawResponse;

	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime startedAt;

	private LocalDateTime completedAt;

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

	public AIEvaluationType getType() {
		return type;
	}

	public void setType(AIEvaluationType type) {
		this.type = type;
	}

	public AIEvaluationStatus getStatus() {
		return status;
	}

	public void setStatus(AIEvaluationStatus status) {
		this.status = status;
	}

	public AIProviderType getProvider() {
		return provider;
	}

	public void setProvider(AIProviderType provider) {
		this.provider = provider;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPromptSnapshot() {
		return promptSnapshot;
	}

	public void setPromptSnapshot(String promptSnapshot) {
		this.promptSnapshot = promptSnapshot;
	}

	public String getRawResponse() {
		return rawResponse;
	}

	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}
}
