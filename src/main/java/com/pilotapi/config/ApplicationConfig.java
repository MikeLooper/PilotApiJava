package com.pilotapi.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(ApplicationMetadataProperties.class)
public class ApplicationConfig implements HibernatePropertiesCustomizer {

    private final Environment environment;

    public ApplicationConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.physical_naming_strategy",
                new PropertyResolvingNamingStrategy(environment));
    }
}
