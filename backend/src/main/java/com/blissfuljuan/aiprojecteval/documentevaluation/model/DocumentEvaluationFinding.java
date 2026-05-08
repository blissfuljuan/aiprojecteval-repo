package com.blissfuljuan.aiprojecteval.documentevaluation.model;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentEvaluationFindingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "document_evaluation_findings",
		indexes = @Index(name = "idx_document_evaluation_findings_evaluation", columnList = "evaluation_id")
)
public class DocumentEvaluationFinding {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "evaluation_id", nullable = false)
	private DocumentEvaluation evaluation;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentEvaluationFindingType type;

	@NotBlank
	@Size(max = 150)
	@Column(nullable = false, length = 150)
	private String title;

	@Size(max = 4000)
	@Column(columnDefinition = "TEXT")
	private String description;

	@Size(max = 4000)
	@Column(columnDefinition = "TEXT")
	private String recommendation;

	@Min(0)
	@Max(5)
	@Column
	private Integer severity;

	@NotNull
	@Min(0)
	@Column(nullable = false)
	private Integer displayOrder;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public DocumentEvaluationFinding() {
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

	public DocumentEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(DocumentEvaluation evaluation) {
		this.evaluation = evaluation;
	}

	public DocumentEvaluationFindingType getType() {
		return type;
	}

	public void setType(DocumentEvaluationFindingType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public Integer getSeverity() {
		return severity;
	}

	public void setSeverity(Integer severity) {
		this.severity = severity;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
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
