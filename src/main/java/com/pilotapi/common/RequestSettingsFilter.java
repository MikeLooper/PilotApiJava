package com.pilotapi.common;

import io.opentelemetry.context.Scope;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Initializes the {@link RequestSettings} for every request and returns its
 * {@code X-Correlation-Id} and {@code X-Request-Id} on the response.
 * <p>
 * Runs first so the settings exist before security and the endpoints run. The response
 * headers are set before the rest of the chain because a response body commits the
 * headers once written; setting them here means every response carries them, including
 * auth failures and error responses.
 * <p>
 * The settings are made current in the OpenTelemetry context for the rest of the chain, so the
 * spans and log records produced while handling the request carry its IDs. Running before the
 * OpenTelemetry Web MVC filter means the server span is started inside that context too.
 * <p>
 * The correlation ID, request ID and caller name are also put in the SLF4J {@link MDC} for the
 * rest of the chain, so console log lines written while handling the request show them.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestSettingsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        RequestSettings settings = RequestSettings.from(request);

        response.setHeader(RequestSettings.CORRELATION_ID_HEADER, settings.getCorrelationId());
        response.setHeader(RequestSettings.REQUEST_ID_HEADER, settings.getRequestId());

        try (Scope ignored = settings.makeCurrent();
             MDC.MDCCloseable correlationId = MDC.putCloseable(
                 RequestSettings.CORRELATION_ID_MDC_KEY, settings.getCorrelationId());
             MDC.MDCCloseable requestId = MDC.putCloseable(
                 RequestSettings.REQUEST_ID_MDC_KEY, settings.getRequestId());
             MDC.MDCCloseable callerName = MDC.putCloseable(
                 RequestSettings.CALLER_NAME_MDC_KEY, settings.getCallerName())) {
            filterChain.doFilter(request, response);
        }
    }
}
