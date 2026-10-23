package com.pilotapi.config;

import com.pilotapi.common.RequestSettings;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestSettingsSpanProcessorTest {

    private final SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(new RequestSettingsSpanProcessor())
        .build();
    private final Tracer tracer = tracerProvider.get("test");

    @AfterEach
    void tearDown() {
        tracerProvider.close();
    }

    @Test
    void RequestSettingsSpanProcessorTest_onStart_withinRequest_tagsCorrelationAndRequestId_Test() {
        RequestSettings settings = new RequestSettings("request-1", "correlation-1");

        Span span;
        try (Scope ignored = settings.makeCurrent()) {
            span = tracer.spanBuilder("test").startSpan();
        }
        span.end();

        ReadableSpan readable = (ReadableSpan) span;
        assertEquals("correlation-1", readable.getAttribute(RequestSettingsSpanProcessor.CORRELATION_ID));
        assertEquals("request-1", readable.getAttribute(RequestSettingsSpanProcessor.REQUEST_ID));
    }

    @Test
    void RequestSettingsSpanProcessorTest_onStart_outsideRequest_addsNoTags_Test() {
        Span span = tracer.spanBuilder("test").startSpan();
        span.end();

        ReadableSpan readable = (ReadableSpan) span;
        assertNull(readable.getAttribute(RequestSettingsSpanProcessor.CORRELATION_ID));
        assertNull(readable.getAttribute(RequestSettingsSpanProcessor.REQUEST_ID));
    }
}
