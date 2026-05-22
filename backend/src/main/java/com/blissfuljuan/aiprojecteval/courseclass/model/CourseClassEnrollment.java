package com.blissfuljuan.aiprojecteval.courseclass.model;

import com.blissfuljuan.aiprojecteval.identity.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "course_class_enrollments",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_course_class_enrollments_student_class",
						columnNames = {"student_user_id", "course_class_id"}
				)
		}
)
public class CourseClassEnrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_user_id", nullable = false)
	private User student;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "course_class_id", nullable = false)
	private CourseClass courseClass;

	@Column(nullable = false, updatable = false)
	private LocalDateTime enrolledAt;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public CourseClassEnrollment() {
	}

	public CourseClassEnrollment(User student, CourseClass courseClass) {
		this.student = student;
		this.courseClass = courseClass;
	}

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		enrolledAt = now;
		createdAt = now;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getStudent() {
		return student;
	}

	public void setStudent(User student) {
		this.student = student;
	}

	public CourseClass getCourseClass() {
		return courseClass;
	}

	public void setCourseClass(CourseClass courseClass) {
		this.courseClass = courseClass;
	}

	public LocalDateTime getEnrolledAt() {
		return enrolledAt;
	}

	public void setEnrolledAt(LocalDateTime enrolledAt) {
		this.enrolledAt = enrolledAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
