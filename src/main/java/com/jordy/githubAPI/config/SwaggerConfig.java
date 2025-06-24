package com.jordy.githubAPI.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Git Server API", version = "0.1", description = "Git Server API"))
public class SwaggerConfig {
}
