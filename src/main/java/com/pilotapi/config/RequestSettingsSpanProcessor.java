package com.pilotapi.config;

import com.pilotapi.common.RequestSettings;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

/**
 * Tags every span started within a request with the request's correlation ID, request ID and
 * caller name. The caller name is only added when the caller sent an {@code X-Caller-Name} header.
 */
public class RequestSettingsSpanProcessor implements SpanProcessor {

    static final AttributeKey<String> CORRELATION_ID = AttributeKey.stringKey("correlationId");
    static final AttributeKey<String> REQUEST_ID = AttributeKey.stringKey("request.Id");
    static final AttributeKey<String> CALLER_NAME = AttributeKey.stringKey("callerName");

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
        RequestSettings settings = RequestSettings.fromContext(parentContext);
        if (settings == null) {
            return;
        }
        span.setAttribute(CORRELATION_ID, settings.getCorrelationId());
        span.setAttribute(REQUEST_ID, settings.getRequestId());
        if (settings.getCallerName() != null) {
            span.setAttribute(CALLER_NAME, settings.getCallerName());
        }
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
