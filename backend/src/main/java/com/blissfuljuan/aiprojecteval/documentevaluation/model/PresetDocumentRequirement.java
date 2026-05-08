package com.blissfuljuan.aiprojecteval.documentevaluation.model;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "preset_document_requirements")
public class PresetDocumentRequirement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "preset_id", nullable = false)
	private DocumentRequirementPreset preset;

	@NotBlank
	@Size(max = 150)
	@Column(nullable = false, length = 150)
	private String name;

	@Size(max = 1000)
	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private boolean required = true;

	@ElementCollection
	@CollectionTable(
			name = "preset_document_requirement_allowed_file_types",
			joinColumns = @JoinColumn(name = "preset_document_requirement_id")
	)
	@Enumerated(EnumType.STRING)
	@Column(name = "file_type", nullable = false)
	private Set<AllowedFileType> allowedFileTypes = new LinkedHashSet<>();

	@NotNull
	@Min(0)
	@Column(nullable = false)
	private Integer sortOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_id")
	private DocumentTemplate template;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rubric_id")
	private EvaluationRubric rubric;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public PresetDocumentRequirement() {
	}

	public PresetDocumentRequirement(String name, Integer sortOrder) {
		this.name = name;
		this.sortOrder = sortOrder;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DocumentRequirementPreset getPreset() {
		return preset;
	}

	public void setPreset(DocumentRequirementPreset preset) {
		this.preset = preset;
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

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Set<AllowedFileType> getAllowedFileTypes() {
		return allowedFileTypes;
	}

	public void setAllowedFileTypes(Set<AllowedFileType> allowedFileTypes) {
		this.allowedFileTypes = allowedFileTypes;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public DocumentTemplate getTemplate() {
		return template;
	}

	public void setTemplate(DocumentTemplate template) {
		this.template = template;
	}

	public EvaluationRubric getRubric() {
		return rubric;
	}

	public void setRubric(EvaluationRubric rubric) {
		this.rubric = rubric;
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
