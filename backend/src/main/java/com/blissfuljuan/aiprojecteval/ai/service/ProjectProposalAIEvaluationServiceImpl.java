package com.blissfuljuan.aiprojecteval.ai.service;

import com.blissfuljuan.aiprojecteval.ai.config.AIProperties;
import com.blissfuljuan.aiprojecteval.ai.dto.ProjectProposalAIEvaluationRequest;
import com.blissfuljuan.aiprojecteval.ai.dto.ProjectProposalAIEvaluationResponse;
import com.blissfuljuan.aiprojecteval.ai.mapper.ProjectProposalAIEvaluationMapper;
import com.blissfuljuan.aiprojecteval.ai.model.AIEvaluation;
import com.blissfuljuan.aiprojecteval.ai.model.AIEvaluationStatus;
import com.blissfuljuan.aiprojecteval.ai.model.AIEvaluationType;
import com.blissfuljuan.aiprojecteval.ai.model.AIProviderType;
import com.blissfuljuan.aiprojecteval.ai.model.ProjectProposalAIEvaluationResult;
import com.blissfuljuan.aiprojecteval.ai.provider.AIProvider;
import com.blissfuljuan.aiprojecteval.ai.repository.AIEvaluationRepository;
import com.blissfuljuan.aiprojecteval.ai.repository.ProjectProposalAIEvaluationResultRepository;
import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.document.model.Document;
import com.blissfuljuan.aiprojecteval.document.model.DocumentContextType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentExtractionStatus;
import com.blissfuljuan.aiprojecteval.document.model.DocumentType;
import com.blissfuljuan.aiprojecteval.document.model.DocumentVersion;
import com.blissfuljuan.aiprojecteval.document.service.DocumentService;
import com.blissfuljuan.aiprojecteval.projectproposal.model.ProjectProposal;
import com.blissfuljuan.aiprojecteval.projectproposal.repository.ProjectProposalRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ProjectProposalAIEvaluationServiceImpl implements ProjectProposalAIEvaluationService {

	private final AIProperties aiProperties;
	private final List<AIProvider> providers;
	private final ProjectProposalRepository projectProposalRepository;
	private final DocumentService documentService;
	private final AIEvaluationRepository aiEvaluationRepository;
	private final ProjectProposalAIEvaluationResultRepository resultRepository;
	private final ProjectProposalAIPromptBuilder promptBuilder;
	private final ProjectProposalAIResponseParser responseParser;
	private final ProjectProposalAIEvaluationMapper mapper;

	ProjectProposalAIEvaluationServiceImpl(
			AIProperties aiProperties,
			List<AIProvider> providers,
			ProjectProposalRepository projectProposalRepository,
			DocumentService documentService,
			AIEvaluationRepository aiEvaluationRepository,
			ProjectProposalAIEvaluationResultRepository resultRepository,
			ProjectProposalAIPromptBuilder promptBuilder,
			ProjectProposalAIResponseParser responseParser,
			ProjectProposalAIEvaluationMapper mapper) {
		this.aiProperties = aiProperties;
		this.providers = providers;
		this.projectProposalRepository = projectProposalRepository;
		this.documentService = documentService;
		this.aiEvaluationRepository = aiEvaluationRepository;
		this.resultRepository = resultRepository;
		this.promptBuilder = promptBuilder;
		this.responseParser = responseParser;
		this.mapper = mapper;
	}

	@Override
	@Transactional
	public ProjectProposalAIEvaluationResponse evaluate(Long proposalId, ProjectProposalAIEvaluationRequest request) {
		AIProviderType providerType = request.provider() == null ? aiProperties.getProvider() : request.provider();
		boolean forceReevaluate = Boolean.TRUE.equals(request.forceReevaluate());
		if (!forceReevaluate) {
			return resultRepository.findTopByProposalIdAndDocumentVersionIdAndEvaluationProviderAndEvaluationStatusOrderByCreatedAtDesc(
							proposalId,
							request.documentVersionId(),
							providerType,
							AIEvaluationStatus.COMPLETED)
					.map(mapper::toResponse)
					.orElseGet(() -> createEvaluation(proposalId, request.documentVersionId(), providerType));
		}

		return createEvaluation(proposalId, request.documentVersionId(), providerType);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProjectProposalAIEvaluationResponse> findByProposal(Long proposalId) {
		ensureProposalExists(proposalId);

		return resultRepository.findByProposalIdOrderByCreatedAtDesc(proposalId)
				.stream()
				.map(mapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProjectProposalAIEvaluationResponse findLatestByProposal(Long proposalId) {
		ensureProposalExists(proposalId);

		return resultRepository.findTopByProposalIdOrderByCreatedAtDesc(proposalId)
				.map(mapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("AI evaluation not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public ProjectProposalAIEvaluationResponse findByEvaluationId(Long evaluationId) {
		return resultRepository.findByEvaluationId(evaluationId)
				.map(mapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("AI evaluation not found"));
	}

	private ProjectProposalAIEvaluationResponse createEvaluation(
			Long proposalId,
			Long documentVersionId,
			AIProviderType providerType) {
		ProjectProposal proposal = ensureProposalExists(proposalId);
		DocumentVersion documentVersion = findAndValidateDocumentVersion(documentVersionId, proposalId);
		AIProvider provider = findProvider(providerType);
		String prompt = promptBuilder.build(proposal, documentVersion);
		LocalDateTime now = LocalDateTime.now();

		AIEvaluation evaluation = new AIEvaluation();
		evaluation.setType(AIEvaluationType.PROJECT_PROPOSAL);
		evaluation.setStatus(AIEvaluationStatus.PROCESSING);
		evaluation.setProvider(providerType);
		evaluation.setModel(aiProperties.getModel());
		evaluation.setPromptSnapshot(prompt);
		evaluation.setStartedAt(now);
		evaluation = aiEvaluationRepository.save(evaluation);

		String rawResponse = provider.generate(prompt);
		ProjectProposalAIEvaluationResult result = responseParser.parse(rawResponse);
		evaluation.setRawResponse(rawResponse);
		evaluation.setStatus(AIEvaluationStatus.COMPLETED);
		evaluation.setCompletedAt(LocalDateTime.now());
		evaluation = aiEvaluationRepository.save(evaluation);

		result.setEvaluation(evaluation);
		result.setProposalId(proposalId);
		result.setDocumentVersionId(documentVersionId);
		result = resultRepository.save(result);

		return mapper.toResponse(result);
	}

	private AIProvider findProvider(AIProviderType providerType) {
		if (providerType != AIProviderType.MOCK) {
			throw new BadRequestException("Only MOCK AI provider is supported in this phase");
		}

		return providers.stream()
				.filter(provider -> provider.providerType() == providerType)
				.findFirst()
				.orElseThrow(() -> new BadRequestException("AI provider is not available"));
	}

	private ProjectProposal ensureProposalExists(Long proposalId) {
		return projectProposalRepository.findById(proposalId)
				.orElseThrow(() -> new ResourceNotFoundException("Project proposal not found"));
	}

	private DocumentVersion findAndValidateDocumentVersion(Long documentVersionId, Long proposalId) {
		DocumentVersion documentVersion = documentService.findVersionById(documentVersionId);
		Document document = documentVersion.getDocument();

		if (document.getContextType() != DocumentContextType.PROJECT_PROPOSAL
				|| !proposalId.equals(document.getContextId())
				|| document.getDocumentType() != DocumentType.PROJECT_PROPOSAL_DOCUMENT) {
			throw new BadRequestException("Document version does not belong to this project proposal");
		}
		if (documentVersion.getExtractionStatus() != DocumentExtractionStatus.EXTRACTED) {
			throw new BadRequestException("Document version text must be extracted before AI evaluation");
		}
		if (documentVersion.getExtractedText() == null || documentVersion.getExtractedText().isBlank()) {
			throw new BadRequestException("Document version extracted text is empty");
		}

		return documentVersion;
	}
}
