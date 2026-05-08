package com.blissfuljuan.aiprojecteval.documentevaluation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "document_evaluation_criterion_scores",
		indexes = {
				@Index(name = "idx_document_evaluation_scores_evaluation", columnList = "evaluation_id"),
				@Index(name = "idx_document_evaluation_scores_criterion", columnList = "criterion_id")
		}
)
public class DocumentEvaluationCriterionScore {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "evaluation_id", nullable = false)
	private DocumentEvaluation evaluation;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "criterion_id", nullable = false)
	private RubricCriterion criterion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "selected_level_id")
	private RubricLevel selectedLevel;

	@DecimalMin("0.0")
	@Column(precision = 10, scale = 2)
	private BigDecimal score;

	@NotNull
	@DecimalMin("0.0")
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal maxScore = BigDecimal.ZERO;

	@Size(max = 4000)
	@Column(columnDefinition = "TEXT")
	private String comment;

	@Size(max = 4000)
	@Column(columnDefinition = "TEXT")
	private String finding;

	@NotNull
	@Min(0)
	@Column(nullable = false)
	private Integer displayOrder;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public DocumentEvaluationCriterionScore() {
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

	public RubricCriterion getCriterion() {
		return criterion;
	}

	public void setCriterion(RubricCriterion criterion) {
		this.criterion = criterion;
	}

	public RubricLevel getSelectedLevel() {
		return selectedLevel;
	}

	public void setSelectedLevel(RubricLevel selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public BigDecimal getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(BigDecimal maxScore) {
		this.maxScore = maxScore;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFinding() {
		return finding;
	}

	public void setFinding(String finding) {
		this.finding = finding;
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
