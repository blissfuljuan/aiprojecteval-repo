package com.blissfuljuan.aiprojecteval.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI aiProjectEvalOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("AI Project Evaluation API")
						.description("Modular monolith backend API for AI-assisted project evaluation.")
						.version("v1")
						.contact(new Contact().name("Blissful Juan"))
						.license(new License().name("Proprietary")));
	}
}
