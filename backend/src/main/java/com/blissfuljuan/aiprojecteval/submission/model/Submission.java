package com.blissfuljuan.aiprojecteval.submission.model;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.identity.model.User;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "submissions")
public class Submission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SubmissionType type;

	@NotNull
	@Column(nullable = false)
	private Long assignmentId;

	@Column(name = "requirement_id")
	private Long requirementId;

	@Column
	private Long projectId;

	@Column
	private Long courseClassId;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "submitted_by_id", nullable = false)
	private User submittedBy;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SubmissionStatus status;

	@NotBlank
	@Size(max = 150)
	@Column(nullable = false, length = 150)
	private String submissionTitle;

	@Size(max = 2000)
	@Column(columnDefinition = "TEXT")
	private String submissionNotes;

	@NotNull
	@Min(1)
	@Column(nullable = false)
	private Integer attemptNumber;

	@Column
	private LocalDateTime submittedAt;

	@Column(nullable = false)
	private LocalDateTime lastUpdatedAt;

	@OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SubmissionFile> files = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public Submission() {
	}

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (lastUpdatedAt == null) {
			lastUpdatedAt = now;
		}
	}

	@PreUpdate
	void onUpdate() {
		LocalDateTime now = LocalDateTime.now();
		updatedAt = now;
		lastUpdatedAt = now;
	}

	public void addFile(SubmissionFile file) {
		files.add(file);
		file.setSubmission(this);
	}

	public void removeFile(SubmissionFile file) {
		files.remove(file);
		file.setSubmission(null);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SubmissionType getType() {
		return type;
	}

	public void setType(SubmissionType type) {
		this.type = type;
	}

	public Long getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Long assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Long getRequirementId() {
		return requirementId;
	}

	public void setRequirementId(Long requirementId) {
		this.requirementId = requirementId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getCourseClassId() {
		return courseClassId;
	}

	public void setCourseClassId(Long courseClassId) {
		this.courseClassId = courseClassId;
	}

	public User getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(User submittedBy) {
		this.submittedBy = submittedBy;
	}

	public SubmissionStatus getStatus() {
		return status;
	}

	public void setStatus(SubmissionStatus status) {
		this.status = status;
	}

	public String getSubmissionTitle() {
		return submissionTitle;
	}

	public void setSubmissionTitle(String submissionTitle) {
		this.submissionTitle = submissionTitle;
	}

	public String getSubmissionNotes() {
		return submissionNotes;
	}

	public void setSubmissionNotes(String submissionNotes) {
		this.submissionNotes = submissionNotes;
	}

	public Integer getAttemptNumber() {
		return attemptNumber;
	}

	public void setAttemptNumber(Integer attemptNumber) {
		this.attemptNumber = attemptNumber;
	}

	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}

	public LocalDateTime getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public List<SubmissionFile> getFiles() {
		return files;
	}

	public void setFiles(List<SubmissionFile> files) {
		this.files = files;
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
