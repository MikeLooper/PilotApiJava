package com.pilotapi.config;

import com.pilotapi.common.RequestSettings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(ApplicationMetadataProperties.class)
public class ApplicationConfig implements HibernatePropertiesCustomizer, WebMvcConfigurer {

    private final Environment environment;
    private final ApplicationMetadataProperties metadata;

    public ApplicationConfig(Environment environment, ApplicationMetadataProperties metadata) {
        this.environment = environment;
        this.metadata = metadata;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.physical_naming_strategy",
                new PropertyResolvingNamingStrategy(environment));
    }

    /**
     * Exposes the current request's {@link RequestSettings} for injection anywhere in the app.
     */
    @Bean
    @RequestScope
    public RequestSettings requestSettings(HttpServletRequest request) {
        return RequestSettings.from(request);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiHeaderInterceptor(metadata.getBuildVersion()));
    }

    private static class ApiHeaderInterceptor implements HandlerInterceptor {
        private final String defaultApiVersion;

        ApiHeaderInterceptor(String defaultApiVersion) {
            this.defaultApiVersion = defaultApiVersion;
        }

        @Override
        public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
        ) {
            String apiVersion = request.getHeader("X-Api-Version");
            if (apiVersion == null || apiVersion.isBlank()) {
                apiVersion = defaultApiVersion;
            }

            response.setHeader("X-Api-Version", apiVersion);

            response.setHeader("Content-Type", "application/json");
            response.setDateHeader("Date", System.currentTimeMillis());
        }
    }
}
