package com.pilotapi.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Logs the beginning and end of every endpoint (controller method) call.
 * <p>
 * Registered ahead of the other interceptors, so the end is logged even when a later
 * interceptor or the endpoint itself rejects the request. The request's correlation ID,
 * request ID and caller name are attached to each line by {@link RequestSettingsFilter}
 * (MDC, for the console) and the OpenTelemetry log record processor (for exported logs).
 */
public class EndpointLoggingInterceptor implements HandlerInterceptor {

    static final String START_TIME_ATTRIBUTE = EndpointLoggingInterceptor.class.getName() + ".startTime";

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            request.setAttribute(START_TIME_ATTRIBUTE, System.nanoTime());
            LOGGER.info("Endpoint started: {} {} ({})",
                request.getMethod(), request.getRequestURI(), endpointName(handlerMethod));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }
        long elapsedMillis = request.getAttribute(START_TIME_ATTRIBUTE) instanceof Long startTime
            ? (System.nanoTime() - startTime) / 1_000_000
            : 0;
        if (ex == null) {
            LOGGER.info("Endpoint completed: {} {} ({}) with status {} in {} ms",
                request.getMethod(), request.getRequestURI(), endpointName(handlerMethod),
                response.getStatus(), elapsedMillis);
        } else {
            LOGGER.warn("Endpoint failed: {} {} ({}) after {} ms",
                request.getMethod(), request.getRequestURI(), endpointName(handlerMethod), elapsedMillis, ex);
        }
    }

    private static String endpointName(HandlerMethod handlerMethod) {
        return handlerMethod.getBeanType().getSimpleName() + "." + handlerMethod.getMethod().getName();
    }
}
