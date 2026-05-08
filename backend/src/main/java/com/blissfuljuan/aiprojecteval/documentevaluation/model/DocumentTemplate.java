package com.blissfuljuan.aiprojecteval.documentevaluation.model;

import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "document_templates")
public class DocumentTemplate {

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
	private Long sourcePresetTemplateId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_instructor_id")
	private User ownerInstructor;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ConfigurationStatus status;

	@OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("sortOrder ASC")
	private List<TemplateSection> sections = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public DocumentTemplate() {
	}

	public DocumentTemplate(String name, ConfigurationStatus status) {
		this.name = name;
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

	public void addSection(TemplateSection section) {
		sections.add(section);
		section.setTemplate(this);
	}

	public void removeSection(TemplateSection section) {
		sections.remove(section);
		section.setTemplate(null);
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

	public Long getSourcePresetTemplateId() {
		return sourcePresetTemplateId;
	}

	public void setSourcePresetTemplateId(Long sourcePresetTemplateId) {
		this.sourcePresetTemplateId = sourcePresetTemplateId;
	}

	public User getOwnerInstructor() {
		return ownerInstructor;
	}

	public void setOwnerInstructor(User ownerInstructor) {
		this.ownerInstructor = ownerInstructor;
	}

	public ConfigurationStatus getStatus() {
		return status;
	}

	public void setStatus(ConfigurationStatus status) {
		this.status = status;
	}

	public List<TemplateSection> getSections() {
		return sections;
	}

	public void setSections(List<TemplateSection> sections) {
		this.sections = sections;
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
