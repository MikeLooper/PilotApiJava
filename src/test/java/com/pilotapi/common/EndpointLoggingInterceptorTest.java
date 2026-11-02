package com.pilotapi.common;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EndpointLoggingInterceptorTest {

    private final EndpointLoggingInterceptor interceptor = new EndpointLoggingInterceptor();
    private final Logger logger = (Logger) LoggerFactory.getLogger(EndpointLoggingInterceptor.class);
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    @BeforeEach
    void setUp() {
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    void EndpointLoggingInterceptorTest_preHandle_withHandlerMethod_logsEndpointStart_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/dummy/get/1");

        boolean proceed = interceptor.preHandle(request, new MockHttpServletResponse(), dummyHandler());

        assertTrue(proceed);
        ILoggingEvent event = appender.list.getFirst();
        assertEquals(Level.INFO, event.getLevel());
        assertEquals("Endpoint started: GET /v1/dummy/get/1 (DummyController.get)", event.getFormattedMessage());
        assertInstanceOf(Long.class, request.getAttribute(EndpointLoggingInterceptor.START_TIME_ATTRIBUTE));
    }

    @Test
    void EndpointLoggingInterceptorTest_preHandle_withNonHandlerMethod_logsNothing_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/favicon.ico");

        boolean proceed = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertTrue(proceed);
        assertTrue(appender.list.isEmpty());
        assertNull(request.getAttribute(EndpointLoggingInterceptor.START_TIME_ATTRIBUTE));
    }

    @Test
    void EndpointLoggingInterceptorTest_afterCompletion_withoutException_logsEndpointCompletionWithStatus_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/v1/dummy/delete/1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(204);
        HandlerMethod handler = dummyHandler();
        interceptor.preHandle(request, response, handler);

        interceptor.afterCompletion(request, response, handler, null);

        ILoggingEvent event = appender.list.get(1);
        assertEquals(Level.INFO, event.getLevel());
        assertTrue(event.getFormattedMessage().startsWith(
            "Endpoint completed: DELETE /v1/dummy/delete/1 (DummyController.get) with status 204 in "),
            event.getFormattedMessage());
    }

    @Test
    void EndpointLoggingInterceptorTest_afterCompletion_withException_logsEndpointFailureAsWarning_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/dummy/get/1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = dummyHandler();
        IllegalStateException failure = new IllegalStateException("boom");
        interceptor.preHandle(request, response, handler);

        interceptor.afterCompletion(request, response, handler, failure);

        ILoggingEvent event = appender.list.get(1);
        assertEquals(Level.WARN, event.getLevel());
        assertTrue(event.getFormattedMessage().startsWith("Endpoint failed: GET /v1/dummy/get/1 (DummyController.get) after "),
            event.getFormattedMessage());
        assertSame(failure, ((ThrowableProxy) event.getThrowableProxy()).getThrowable());
    }

    @Test
    void EndpointLoggingInterceptorTest_afterCompletion_withNonHandlerMethod_logsNothing_Test() throws Exception {
        interceptor.afterCompletion(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object(), null);

        assertTrue(appender.list.isEmpty());
    }

    private static HandlerMethod dummyHandler() throws NoSuchMethodException {
        return new HandlerMethod(new DummyController(), DummyController.class.getMethod("get"));
    }

    private static class DummyController {
        public void get() {
        }
    }
}
