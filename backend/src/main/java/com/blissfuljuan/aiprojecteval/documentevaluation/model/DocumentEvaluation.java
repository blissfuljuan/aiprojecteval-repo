package com.blissfuljuan.aiprojecteval.documentevaluation.model;

import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentEvaluationStatus;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.project.model.Project;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
		name = "document_evaluations",
		indexes = {
				@Index(name = "idx_document_evaluations_submission", columnList = "submission_id"),
				@Index(name = "idx_document_evaluations_assignment", columnList = "assignment_id"),
				@Index(name = "idx_document_evaluations_requirement", columnList = "document_requirement_id"),
				@Index(name = "idx_document_evaluations_evaluated_by", columnList = "evaluated_by_id"),
				@Index(name = "idx_document_evaluations_submitted_by", columnList = "submitted_by_id"),
				@Index(name = "idx_document_evaluations_project", columnList = "project_id"),
				@Index(name = "idx_document_evaluations_course_class", columnList = "course_class_id"),
				@Index(name = "idx_document_evaluations_status", columnList = "status")
		}
)
public class DocumentEvaluation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "submission_id", nullable = false)
	private DocumentSubmission submission;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "assignment_id", nullable = false)
	private DocumentRequirementSetAssignment assignment;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "document_requirement_id", nullable = false)
	private DocumentRequirement documentRequirement;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "requirement_set_id", nullable = false)
	private DocumentRequirementSet requirementSet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_class_id")
	private CourseClass courseClass;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "evaluated_by_id", nullable = false)
	private User evaluatedBy;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "submitted_by_id", nullable = false)
	private User submittedBy;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentEvaluationStatus status;

	@NotNull
	@DecimalMin("0.0")
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalScore = BigDecimal.ZERO;

	@NotNull
	@DecimalMin("0.0")
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal maxScore = BigDecimal.ZERO;

	@NotNull
	@DecimalMin("0.0")
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal percentageScore = BigDecimal.ZERO;

	@Size(max = 4000)
	@Column(columnDefinition = "TEXT")
	private String generalFeedback;

	@Size(max = 4000)
	@Column(columnDefinition = "TEXT")
	private String evaluatorRemarks;

	@Size(max = 4000)
	@Column(columnDefinition = "TEXT")
	private String revisionReason;

	@Column(nullable = false)
	private LocalDateTime startedAt;

	@Column
	private LocalDateTime finalizedAt;

	@Column
	private LocalDateTime returnedAt;

	@OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DocumentEvaluationCriterionScore> criterionScores = new ArrayList<>();

	@OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DocumentEvaluationFinding> findings = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public DocumentEvaluation() {
	}

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (startedAt == null) {
			startedAt = now;
		}
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void addCriterionScore(DocumentEvaluationCriterionScore criterionScore) {
		criterionScores.add(criterionScore);
		criterionScore.setEvaluation(this);
	}

	public void addFinding(DocumentEvaluationFinding finding) {
		findings.add(finding);
		finding.setEvaluation(this);
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

	public DocumentRequirementSetAssignment getAssignment() {
		return assignment;
	}

	public void setAssignment(DocumentRequirementSetAssignment assignment) {
		this.assignment = assignment;
	}

	public DocumentRequirement getDocumentRequirement() {
		return documentRequirement;
	}

	public void setDocumentRequirement(DocumentRequirement documentRequirement) {
		this.documentRequirement = documentRequirement;
	}

	public DocumentRequirementSet getRequirementSet() {
		return requirementSet;
	}

	public void setRequirementSet(DocumentRequirementSet requirementSet) {
		this.requirementSet = requirementSet;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public CourseClass getCourseClass() {
		return courseClass;
	}

	public void setCourseClass(CourseClass courseClass) {
		this.courseClass = courseClass;
	}

	public User getEvaluatedBy() {
		return evaluatedBy;
	}

	public void setEvaluatedBy(User evaluatedBy) {
		this.evaluatedBy = evaluatedBy;
	}

	public User getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(User submittedBy) {
		this.submittedBy = submittedBy;
	}

	public DocumentEvaluationStatus getStatus() {
		return status;
	}

	public void setStatus(DocumentEvaluationStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(BigDecimal totalScore) {
		this.totalScore = totalScore;
	}

	public BigDecimal getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(BigDecimal maxScore) {
		this.maxScore = maxScore;
	}

	public BigDecimal getPercentageScore() {
		return percentageScore;
	}

	public void setPercentageScore(BigDecimal percentageScore) {
		this.percentageScore = percentageScore;
	}

	public String getGeneralFeedback() {
		return generalFeedback;
	}

	public void setGeneralFeedback(String generalFeedback) {
		this.generalFeedback = generalFeedback;
	}

	public String getEvaluatorRemarks() {
		return evaluatorRemarks;
	}

	public void setEvaluatorRemarks(String evaluatorRemarks) {
		this.evaluatorRemarks = evaluatorRemarks;
	}

	public String getRevisionReason() {
		return revisionReason;
	}

	public void setRevisionReason(String revisionReason) {
		this.revisionReason = revisionReason;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public LocalDateTime getFinalizedAt() {
		return finalizedAt;
	}

	public void setFinalizedAt(LocalDateTime finalizedAt) {
		this.finalizedAt = finalizedAt;
	}

	public LocalDateTime getReturnedAt() {
		return returnedAt;
	}

	public void setReturnedAt(LocalDateTime returnedAt) {
		this.returnedAt = returnedAt;
	}

	public List<DocumentEvaluationCriterionScore> getCriterionScores() {
		return criterionScores;
	}

	public void setCriterionScores(List<DocumentEvaluationCriterionScore> criterionScores) {
		this.criterionScores = criterionScores;
	}

	public List<DocumentEvaluationFinding> getFindings() {
		return findings;
	}

	public void setFindings(List<DocumentEvaluationFinding> findings) {
		this.findings = findings;
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
