package com.pilotapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfig {

    private static final String LICENSE_URL = "https://opensource.org";

    @Bean
    public OpenAPI customOpenApi(OpenApiProperties openApiProperties, ApplicationMetadataProperties metadata) {
        return new OpenAPI()
            .info(new Info()
                .title(openApiProperties.getTitle())
                .version("Version: " + metadata.getBuildVersion())
                .description("Description: " + openApiProperties.getDescription())
                .summary("Summary: " + openApiProperties.getSummary())
                .contact(new Contact()
                    .name("Contact: " + openApiProperties.getContact().getName())
                    .email(openApiProperties.getContact().getEmail())
                    .url(openApiProperties.getContact().getUrl()))
                .license(new License()
                    .name("License: " + openApiProperties.getLicense())
                    .url(LICENSE_URL)));
    }
}
