package com.pilotapi.config;

import com.pilotapi.common.RequestSettings;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

/**
 * Tags every span started within a request with the request's correlation and request IDs.
 */
public class RequestSettingsSpanProcessor implements SpanProcessor {

    static final AttributeKey<String> CORRELATION_ID = AttributeKey.stringKey("correlationId");
    static final AttributeKey<String> REQUEST_ID = AttributeKey.stringKey("request.Id");

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
        RequestSettings settings = RequestSettings.fromContext(parentContext);
        if (settings == null) {
            return;
        }
        span.setAttribute(CORRELATION_ID, settings.getCorrelationId());
        span.setAttribute(REQUEST_ID, settings.getRequestId());
    }

    @Override
    public boolean isStartRequired() {
        return true;
    }

    @Override
    public void onEnd(ReadableSpan span) {
    }

    @Override
    public boolean isEndRequired() {
        return false;
    }
}
