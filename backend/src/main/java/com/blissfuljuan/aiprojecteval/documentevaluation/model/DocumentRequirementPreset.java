package com.blissfuljuan.aiprojecteval.documentevaluation.model;

import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.PresetVisibility;
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
@Table(name = "document_requirement_presets")
public class DocumentRequirementPreset {

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

	@Size(max = 100)
	@Column(length = 100)
	private String category;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PresetVisibility visibility;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ConfigurationStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_id")
	private User createdBy;

	@OneToMany(mappedBy = "preset", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("sortOrder ASC")
	private List<PresetDocumentRequirement> documentRequirements = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public DocumentRequirementPreset() {
	}

	public DocumentRequirementPreset(String name, PresetVisibility visibility, ConfigurationStatus status) {
		this.name = name;
		this.visibility = visibility;
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

	public void addDocumentRequirement(PresetDocumentRequirement documentRequirement) {
		documentRequirements.add(documentRequirement);
		documentRequirement.setPreset(this);
	}

	public void removeDocumentRequirement(PresetDocumentRequirement documentRequirement) {
		documentRequirements.remove(documentRequirement);
		documentRequirement.setPreset(null);
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public PresetVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(PresetVisibility visibility) {
		this.visibility = visibility;
	}

	public ConfigurationStatus getStatus() {
		return status;
	}

	public void setStatus(ConfigurationStatus status) {
		this.status = status;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public List<PresetDocumentRequirement> getDocumentRequirements() {
		return documentRequirements;
	}

	public void setDocumentRequirements(List<PresetDocumentRequirement> documentRequirements) {
		this.documentRequirements = documentRequirements;
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
