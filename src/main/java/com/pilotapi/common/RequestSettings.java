package com.pilotapi.common;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

/**
 * Per-request values (request ID, correlation ID, ...) shared across the life of a request.
 * Created once at the start of each request by {@link RequestSettingsFilter} and stored as a
 * request attribute. Read it with {@link #from(HttpServletRequest)}, or inject the
 * request-scoped {@code RequestSettings} bean.
 */
public class RequestSettings {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    static final String ATTRIBUTE_NAME = RequestSettings.class.getName();

    private final String requestId;
    private final String correlationId;

    public RequestSettings(String requestId, String correlationId) {
        this.requestId = requestId;
        this.correlationId = correlationId;
    }

    /**
     * Builds the settings for an incoming request: the active request's ID as assigned by the
     * servlet container, and the caller's {@code X-Correlation-Id} header. Either value falls
     * back to a new GUID when the container or caller does not supply one.
     */
    public static RequestSettings initialize(HttpServletRequest request) {
        return new RequestSettings(
            valueOrNewGuid(request.getRequestId()),
            valueOrNewGuid(request.getHeader(CORRELATION_ID_HEADER)));
    }

    private static String valueOrNewGuid(String value) {
        return value == null || value.isBlank() ? UUID.randomUUID().toString() : value.trim();
    }

    /**
     * Returns the settings bound to the request, creating and binding them if the request has
     * not passed through {@link RequestSettingsFilter}.
     */
    public static RequestSettings from(HttpServletRequest request) {
        if (request.getAttribute(ATTRIBUTE_NAME) instanceof RequestSettings settings) {
            return settings;
        }
        RequestSettings settings = initialize(request);
        request.setAttribute(ATTRIBUTE_NAME, settings);
        return settings;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
