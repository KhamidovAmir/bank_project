package ru.khan.bank.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Mini Bank System API",
                description = "API для банковской системы",
                version = "1.0.0"
        )
)
public class OpenApiConfig {
}
