package com.pilotapi.service;

import com.pilotapi.config.ApplicationMetadataProperties;
import com.pilotapi.dto.AboutResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemServiceTest {

    @Mock
    private Environment environment;

    private ApplicationMetadataProperties metadata;
    private SystemService service;

    @BeforeEach
    void setUp() {
        metadata = new ApplicationMetadataProperties();
        service = new SystemService(metadata, environment);
        ReflectionTestUtils.setField(service, "applicationName", "PilotApiJava");
    }

    @Test
    void SystemService_healthcheck_returns_ok_Test() {
        // Arrange

        // Act
        String result = service.healthcheck();

        // Assert
        assertEquals("OK", result);
    }

    @Test
    void SystemService_about_uses_metadata_name_when_provided_Test() {
        // Arrange
        metadata.setName("ConfiguredName");
        metadata.setBuildVersion("1.2.3");
        metadata.setDeployDate("2026-07-24");

        // Act
        AboutResponseDto result = service.about(false, "v1");

        // Assert
        assertEquals("v1", result.getApiVersion());
        assertEquals("ConfiguredName", result.getName());
        assertEquals("1.2.3", result.getBuildVersion());
        assertEquals("2026-07-24", result.getDeployDate());
        assertNull(result.getApplicationConfiguration());
    }

    @Test
    void SystemService_about_uses_application_name_fallback_and_includes_details_Test() {
        // Arrange
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev", "local"});

        // Act
        AboutResponseDto result = service.about(true, "v2");

        // Assert
        assertEquals("v2", result.getApiVersion());
        assertEquals("PilotApiJava", result.getName());
        assertInstanceOf(Map.class, result.getApplicationConfiguration());

        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) result.getApplicationConfiguration();
        assertInstanceOf(String[].class, details.get("activeProfiles"));
    }
}
