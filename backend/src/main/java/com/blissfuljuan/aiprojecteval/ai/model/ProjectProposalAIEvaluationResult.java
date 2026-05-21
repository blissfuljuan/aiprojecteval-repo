package com.blissfuljuan.aiprojecteval.ai.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project_proposal_ai_evaluation_results")
public class ProjectProposalAIEvaluationResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "evaluation_id", nullable = false, unique = true)
	private AIEvaluation evaluation;

	@Column(nullable = false)
	private Long proposalId;

	@Column(nullable = false)
	private Long documentVersionId;

	private Integer overallScore;

	@Column(nullable = false)
	private Integer maxScore;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProposalReadinessLevel readinessLevel;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProposalAIRecommendation recommendation;

	@Column(columnDefinition = "TEXT")
	private String summary;

	@ElementCollection
	@CollectionTable(name = "project_proposal_ai_strengths", joinColumns = @JoinColumn(name = "result_id"))
	@OrderColumn(name = "sort_order")
	@Column(name = "strength", columnDefinition = "TEXT")
	private List<String> strengths = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "project_proposal_ai_weaknesses", joinColumns = @JoinColumn(name = "result_id"))
	@OrderColumn(name = "sort_order")
	@Column(name = "weakness", columnDefinition = "TEXT")
	private List<String> weaknesses = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "project_proposal_ai_missing_sections", joinColumns = @JoinColumn(name = "result_id"))
	@OrderColumn(name = "sort_order")
	@Column(name = "missing_section", columnDefinition = "TEXT")
	private List<String> missingSections = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "project_proposal_ai_risk_notes", joinColumns = @JoinColumn(name = "result_id"))
	@OrderColumn(name = "sort_order")
	@Column(name = "risk_note", columnDefinition = "TEXT")
	private List<String> riskNotes = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "project_proposal_ai_suggested_revisions", joinColumns = @JoinColumn(name = "result_id"))
	@OrderColumn(name = "sort_order")
	@Column(name = "suggested_revision", columnDefinition = "TEXT")
	private List<String> suggestedRevisions = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "project_proposal_ai_criteria_scores", joinColumns = @JoinColumn(name = "result_id"))
	@OrderColumn(name = "sort_order")
	private List<AICriteriaScore> criteriaScores = new ArrayList<>();

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

	public AIEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(AIEvaluation evaluation) {
		this.evaluation = evaluation;
	}

	public Long getProposalId() {
		return proposalId;
	}

	public void setProposalId(Long proposalId) {
		this.proposalId = proposalId;
	}

	public Long getDocumentVersionId() {
		return documentVersionId;
	}

	public void setDocumentVersionId(Long documentVersionId) {
		this.documentVersionId = documentVersionId;
	}

	public Integer getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(Integer overallScore) {
		this.overallScore = overallScore;
	}

	public Integer getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}

	public ProposalReadinessLevel getReadinessLevel() {
		return readinessLevel;
	}

	public void setReadinessLevel(ProposalReadinessLevel readinessLevel) {
		this.readinessLevel = readinessLevel;
	}

	public ProposalAIRecommendation getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(ProposalAIRecommendation recommendation) {
		this.recommendation = recommendation;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<String> getStrengths() {
		return strengths;
	}

	public void setStrengths(List<String> strengths) {
		this.strengths = strengths;
	}

	public List<String> getWeaknesses() {
		return weaknesses;
	}

	public void setWeaknesses(List<String> weaknesses) {
		this.weaknesses = weaknesses;
	}

	public List<String> getMissingSections() {
		return missingSections;
	}

	public void setMissingSections(List<String> missingSections) {
		this.missingSections = missingSections;
	}

	public List<String> getRiskNotes() {
		return riskNotes;
	}

	public void setRiskNotes(List<String> riskNotes) {
		this.riskNotes = riskNotes;
	}

	public List<String> getSuggestedRevisions() {
		return suggestedRevisions;
	}

	public void setSuggestedRevisions(List<String> suggestedRevisions) {
		this.suggestedRevisions = suggestedRevisions;
	}

	public List<AICriteriaScore> getCriteriaScores() {
		return criteriaScores;
	}

	public void setCriteriaScores(List<AICriteriaScore> criteriaScores) {
		this.criteriaScores = criteriaScores;
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
