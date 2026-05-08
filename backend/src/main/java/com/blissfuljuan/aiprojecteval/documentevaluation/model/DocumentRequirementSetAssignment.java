package com.blissfuljuan.aiprojecteval.documentevaluation.model;

import com.blissfuljuan.aiprojecteval.courseclass.model.CourseClass;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.project.model.Project;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_requirement_set_assignments")
public class DocumentRequirementSetAssignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "requirement_set_id", nullable = false)
	private DocumentRequirementSet requirementSet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_class_id")
	private CourseClass courseClass;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_by_id")
	private User assignedBy;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RequirementSetAssignmentType assignmentType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RequirementSetAssignmentStatus status;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime assignedAt;

	@Column
	private LocalDateTime deactivatedAt;

	@Size(max = 1000)
	@Column(columnDefinition = "TEXT")
	private String notes;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public DocumentRequirementSetAssignment() {
	}

	public DocumentRequirementSetAssignment(
			DocumentRequirementSet requirementSet,
			RequirementSetAssignmentType assignmentType,
			RequirementSetAssignmentStatus status,
			LocalDateTime assignedAt) {
		this.requirementSet = requirementSet;
		this.assignmentType = assignmentType;
		this.status = status;
		this.assignedAt = assignedAt;
	}

	@PrePersist
	void onCreate() {
		validateTarget();
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (assignedAt == null) {
			assignedAt = now;
		}
	}

	@PreUpdate
	void onUpdate() {
		validateTarget();
		updatedAt = LocalDateTime.now();
	}

	private void validateTarget() {
		boolean hasCourseClass = courseClass != null;
		boolean hasProject = project != null;
		if (hasCourseClass == hasProject) {
			throw new IllegalStateException("Assignment must target exactly one course class or project");
		}
		if (hasCourseClass && assignmentType != RequirementSetAssignmentType.COURSE_CLASS) {
			throw new IllegalStateException("Course class assignments must use COURSE_CLASS assignment type");
		}
		if (hasProject && assignmentType != RequirementSetAssignmentType.PROJECT) {
			throw new IllegalStateException("Project assignments must use PROJECT assignment type");
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DocumentRequirementSet getRequirementSet() {
		return requirementSet;
	}

	public void setRequirementSet(DocumentRequirementSet requirementSet) {
		this.requirementSet = requirementSet;
	}

	public CourseClass getCourseClass() {
		return courseClass;
	}

	public void setCourseClass(CourseClass courseClass) {
		this.courseClass = courseClass;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public User getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(User assignedBy) {
		this.assignedBy = assignedBy;
	}

	public RequirementSetAssignmentType getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(RequirementSetAssignmentType assignmentType) {
		this.assignmentType = assignmentType;
	}

	public RequirementSetAssignmentStatus getStatus() {
		return status;
	}

	public void setStatus(RequirementSetAssignmentStatus status) {
		this.status = status;
	}

	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(LocalDateTime assignedAt) {
		this.assignedAt = assignedAt;
	}

	public LocalDateTime getDeactivatedAt() {
		return deactivatedAt;
	}

	public void setDeactivatedAt(LocalDateTime deactivatedAt) {
		this.deactivatedAt = deactivatedAt;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
