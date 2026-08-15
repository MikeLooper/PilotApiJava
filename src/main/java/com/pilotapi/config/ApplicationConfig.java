package com.pilotapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(ApplicationMetadataProperties.class)
public class ApplicationConfig implements HibernatePropertiesCustomizer, WebMvcConfigurer {

    private static final String DEFAULT_API_VERSION = "1.0.0";

    private final Environment environment;

    public ApplicationConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.physical_naming_strategy",
                new PropertyResolvingNamingStrategy(environment));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiHeaderInterceptor());
    }

    private static class ApiHeaderInterceptor implements HandlerInterceptor {
        @Override
        public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
        ) {
            String apiVersion = request.getHeader("ApiVersion");
            if (apiVersion == null || apiVersion.isBlank()) {
                apiVersion = DEFAULT_API_VERSION;
            }

            response.setHeader("ApiVersion", apiVersion);
            response.setHeader("Content-Type", "application/json");
            response.setDateHeader("Date", System.currentTimeMillis());
        }
    }
}
