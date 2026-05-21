package com.blissfuljuan.aiprojecteval.ai.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AICriteriaScore {

	@Column(nullable = false)
	private String criterion;

	@Column(nullable = false)
	private Integer score;

	@Column(nullable = false)
	private Integer maxScore;

	@Column(columnDefinition = "TEXT")
	private String rationale;

	public String getCriterion() {
		return criterion;
	}

	public void setCriterion(String criterion) {
		this.criterion = criterion;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}

	public String getRationale() {
		return rationale;
	}

	public void setRationale(String rationale) {
		this.rationale = rationale;
	}
}
