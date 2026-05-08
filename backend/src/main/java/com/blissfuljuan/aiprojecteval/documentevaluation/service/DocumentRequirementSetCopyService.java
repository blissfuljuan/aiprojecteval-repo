package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CopyPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;

public interface DocumentRequirementSetCopyService {

	DocumentRequirementSetResponse copyPresetToRequirementSet(
			String currentUserEmail,
			Long presetId,
			CopyPresetRequest request);
}
