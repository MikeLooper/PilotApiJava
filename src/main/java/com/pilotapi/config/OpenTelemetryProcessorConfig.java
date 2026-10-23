package com.pilotapi.config;

import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.trace.SpanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the request-settings processors with the OpenTelemetry SDK built by the Spring Boot
 * starter. Each is placed in front of the exporting processor so its attributes are set before
 * the record or span is exported.
 */
@Configuration
public class OpenTelemetryProcessorConfig {

    @Bean
    public AutoConfigurationCustomizerProvider requestSettingsTelemetryCustomizer() {
        return customizer -> customizer
            .addSpanProcessorCustomizer((processor, config) ->
                SpanProcessor.composite(new RequestSettingsSpanProcessor(), processor))
            .addLogRecordProcessorCustomizer((processor, config) ->
                LogRecordProcessor.composite(new RequestSettingsLogRecordProcessor(), processor));
    }
}
