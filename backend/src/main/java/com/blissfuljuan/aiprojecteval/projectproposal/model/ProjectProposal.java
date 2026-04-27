package com.blissfuljuan.aiprojecteval.projectproposal.model;

import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.identity.model.User;
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
@Table(name = "project_proposals")
public class ProjectProposal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String problemStatement;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String objectives;

	@Column(columnDefinition = "TEXT")
	private String targetUsers;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String proposedFeatures;

	@Column(columnDefinition = "TEXT")
	private String technologyStack;

	@Column(columnDefinition = "TEXT")
	private String expectedOutput;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProposalStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "course_class_id", nullable = false)
	private CourseClass courseClass;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "submitted_by_id", nullable = false)
	private User submittedBy;

	@Column(columnDefinition = "TEXT")
	private String adviserRemarks;

	@Column(columnDefinition = "TEXT")
	private String instructorRemarks;

	private LocalDateTime approvedAt;

	private LocalDateTime rejectedAt;

	private LocalDateTime revisionRequestedAt;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProblemStatement() {
		return problemStatement;
	}

	public void setProblemStatement(String problemStatement) {
		this.problemStatement = problemStatement;
	}

	public String getObjectives() {
		return objectives;
	}

	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}

	public String getTargetUsers() {
		return targetUsers;
	}

	public void setTargetUsers(String targetUsers) {
		this.targetUsers = targetUsers;
	}

	public String getProposedFeatures() {
		return proposedFeatures;
	}

	public void setProposedFeatures(String proposedFeatures) {
		this.proposedFeatures = proposedFeatures;
	}

	public String getTechnologyStack() {
		return technologyStack;
	}

	public void setTechnologyStack(String technologyStack) {
		this.technologyStack = technologyStack;
	}

	public String getExpectedOutput() {
		return expectedOutput;
	}

	public void setExpectedOutput(String expectedOutput) {
		this.expectedOutput = expectedOutput;
	}

	public ProposalStatus getStatus() {
		return status;
	}

	public void setStatus(ProposalStatus status) {
		this.status = status;
	}

	public CourseClass getCourseClass() {
		return courseClass;
	}

	public void setCourseClass(CourseClass courseClass) {
		this.courseClass = courseClass;
	}

	public User getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(User submittedBy) {
		this.submittedBy = submittedBy;
	}

	public String getAdviserRemarks() {
		return adviserRemarks;
	}

	public void setAdviserRemarks(String adviserRemarks) {
		this.adviserRemarks = adviserRemarks;
	}

	public String getInstructorRemarks() {
		return instructorRemarks;
	}

	public void setInstructorRemarks(String instructorRemarks) {
		this.instructorRemarks = instructorRemarks;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}

	public LocalDateTime getRejectedAt() {
		return rejectedAt;
	}

	public void setRejectedAt(LocalDateTime rejectedAt) {
		this.rejectedAt = rejectedAt;
	}

	public LocalDateTime getRevisionRequestedAt() {
		return revisionRequestedAt;
	}

	public void setRevisionRequestedAt(LocalDateTime revisionRequestedAt) {
		this.revisionRequestedAt = revisionRequestedAt;
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
