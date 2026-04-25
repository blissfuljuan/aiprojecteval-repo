package com.blissfuljuan.aiprojecteval.ai.mapper;

import com.blissfuljuan.aiprojecteval.ai.dto.AIResponse;
import com.blissfuljuan.aiprojecteval.ai.model.AIRequestLog;
import org.springframework.stereotype.Component;

@Component
public class AIMapper {

	public AIResponse toResponse(AIRequestLog requestLog) {
		return new AIResponse(null);
	}
}
