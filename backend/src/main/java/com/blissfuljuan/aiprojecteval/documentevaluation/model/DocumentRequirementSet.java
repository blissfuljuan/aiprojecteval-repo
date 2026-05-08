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
@Table(name = "document_requirement_sets")
public class DocumentRequirementSet {

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
	private Long sourcePresetId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_instructor_id")
	private User ownerInstructor;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ConfigurationStatus status;

	@OneToMany(mappedBy = "requirementSet", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("sortOrder ASC")
	private List<DocumentRequirement> documentRequirements = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public DocumentRequirementSet() {
	}

	public DocumentRequirementSet(String name, ConfigurationStatus status) {
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

	public void addDocumentRequirement(DocumentRequirement documentRequirement) {
		documentRequirements.add(documentRequirement);
		documentRequirement.setRequirementSet(this);
	}

	public void removeDocumentRequirement(DocumentRequirement documentRequirement) {
		documentRequirements.remove(documentRequirement);
		documentRequirement.setRequirementSet(null);
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

	public Long getSourcePresetId() {
		return sourcePresetId;
	}

	public void setSourcePresetId(Long sourcePresetId) {
		this.sourcePresetId = sourcePresetId;
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

	public List<DocumentRequirement> getDocumentRequirements() {
		return documentRequirements;
	}

	public void setDocumentRequirements(List<DocumentRequirement> documentRequirements) {
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
