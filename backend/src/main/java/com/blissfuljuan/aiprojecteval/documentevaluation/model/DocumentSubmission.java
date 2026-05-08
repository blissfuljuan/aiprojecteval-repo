package com.blissfuljuan.aiprojecteval.documentevaluation.model;

import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionStatus;
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
@Table(name = "document_submissions")
public class DocumentSubmission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "assignment_id", nullable = false)
	private DocumentRequirementSetAssignment assignment;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "document_requirement_id", nullable = false)
	private DocumentRequirement documentRequirement;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_class_id")
	private CourseClass courseClass;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "submitted_by_id", nullable = false)
	private User submittedBy;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentSubmissionStatus status;

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
	private List<DocumentSubmissionFile> files = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public DocumentSubmission() {
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

	public void addFile(DocumentSubmissionFile file) {
		files.add(file);
		file.setSubmission(this);
	}

	public void removeFile(DocumentSubmissionFile file) {
		files.remove(file);
		file.setSubmission(null);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public User getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(User submittedBy) {
		this.submittedBy = submittedBy;
	}

	public DocumentSubmissionStatus getStatus() {
		return status;
	}

	public void setStatus(DocumentSubmissionStatus status) {
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

	public List<DocumentSubmissionFile> getFiles() {
		return files;
	}

	public void setFiles(List<DocumentSubmissionFile> files) {
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
