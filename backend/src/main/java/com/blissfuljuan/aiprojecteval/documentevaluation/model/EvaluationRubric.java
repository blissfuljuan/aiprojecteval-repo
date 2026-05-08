package com.blissfuljuan.aiprojecteval.documentevaluation.model;

import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RubricScoringType;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evaluation_rubrics")
public class EvaluationRubric {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 150)
	@Column(nullable = false, length = 150)
	private String name;

	@Size(max = 1000)
	@Column(columnDefinition = "TEXT")
	private String description;

	@Column
	private Long sourcePresetRubricId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_instructor_id")
	private User ownerInstructor;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalPoints;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RubricScoringType scoringType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ConfigurationStatus status;

	@OneToMany(mappedBy = "rubric", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("sortOrder ASC")
	private List<RubricCriterion> criteria = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public EvaluationRubric() {
	}

	public EvaluationRubric(String name, BigDecimal totalPoints, RubricScoringType scoringType,
			ConfigurationStatus status) {
		this.name = name;
		this.totalPoints = totalPoints;
		this.scoringType = scoringType;
		this.status = status;
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

	public void addCriterion(RubricCriterion criterion) {
		criteria.add(criterion);
		criterion.setRubric(this);
	}

	public void removeCriterion(RubricCriterion criterion) {
		criteria.remove(criterion);
		criterion.setRubric(null);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getSourcePresetRubricId() {
		return sourcePresetRubricId;
	}

	public void setSourcePresetRubricId(Long sourcePresetRubricId) {
		this.sourcePresetRubricId = sourcePresetRubricId;
	}

	public User getOwnerInstructor() {
		return ownerInstructor;
	}

	public void setOwnerInstructor(User ownerInstructor) {
		this.ownerInstructor = ownerInstructor;
	}

	public BigDecimal getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(BigDecimal totalPoints) {
		this.totalPoints = totalPoints;
	}

	public RubricScoringType getScoringType() {
		return scoringType;
	}

	public void setScoringType(RubricScoringType scoringType) {
		this.scoringType = scoringType;
	}

	public ConfigurationStatus getStatus() {
		return status;
	}

	public void setStatus(ConfigurationStatus status) {
		this.status = status;
	}

	public List<RubricCriterion> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<RubricCriterion> criteria) {
		this.criteria = criteria;
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
