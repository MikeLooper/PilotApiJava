package com.pilotapi.common;

import io.opentelemetry.context.Context;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class RequestSettingsFilterTest {

    private final RequestSettingsFilter filter = new RequestSettingsFilter();

    @Test
    void RequestSettingsFilterTest_doFilter_setsCorrelationAndRequestIdResponseHeaders_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/categories/get/1");
        request.addHeader("X-Correlation-Id", "7ea2c7f7-8b46-475d-86f8-7aaaa9e4a35b");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        RequestSettings settings = RequestSettings.from(request);
        assertEquals("7ea2c7f7-8b46-475d-86f8-7aaaa9e4a35b", response.getHeader("X-Correlation-Id"));
        assertEquals(settings.getRequestId(), response.getHeader("X-Request-Id"));
    }

    @Test
    void RequestSettingsFilterTest_doFilter_withoutCorrelationId_returnsGeneratedCorrelationId_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/categories/get/1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNotNull(response.getHeader("X-Correlation-Id"));
        assertEquals(RequestSettings.from(request).getCorrelationId(), response.getHeader("X-Correlation-Id"));
    }

    @Test
    void RequestSettingsFilterTest_doFilter_bindsSettingsBeforeRestOfChain_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/categories/get/1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestSettings[] seen = new RequestSettings[1];

        filter.doFilter(request, response, (req, res) ->
            seen[0] = (RequestSettings) req.getAttribute(RequestSettings.ATTRIBUTE_NAME));

        assertNotNull(seen[0]);
        assertSame(seen[0], RequestSettings.from(request));
    }

    @Test
    void RequestSettingsFilterTest_doFilter_makesSettingsCurrentForRestOfChain_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/categories/get/1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestSettings[] seen = new RequestSettings[1];

        filter.doFilter(request, response, (req, res) -> seen[0] = RequestSettings.fromContext(Context.current()));

        assertSame(RequestSettings.from(request), seen[0]);
        assertNull(RequestSettings.fromContext(Context.current()));
    }

    @Test
    void RequestSettingsFilterTest_doFilter_putsSettingsInMdcForRestOfChain_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/categories/get/1");
        request.addHeader("X-Correlation-Id", "7ea2c7f7-8b46-475d-86f8-7aaaa9e4a35b");
        request.addHeader("X-Caller-Name", "PilotAngularUi");
        MockHttpServletResponse response = new MockHttpServletResponse();
        String[] seen = new String[3];

        filter.doFilter(request, response, (req, res) -> {
            seen[0] = MDC.get("correlationId");
            seen[1] = MDC.get("requestId");
            seen[2] = MDC.get("callerName");
        });

        RequestSettings settings = RequestSettings.from(request);
        assertEquals("7ea2c7f7-8b46-475d-86f8-7aaaa9e4a35b", seen[0]);
        assertEquals(settings.getRequestId(), seen[1]);
        assertEquals("PilotAngularUi", seen[2]);
    }

    @Test
    void RequestSettingsFilterTest_doFilter_removesSettingsFromMdcAfterChain_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/categories/get/1");
        request.addHeader("X-Caller-Name", "PilotAngularUi");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(MDC.get("correlationId"));
        assertNull(MDC.get("requestId"));
        assertNull(MDC.get("callerName"));
    }

    @Test
    void RequestSettingsFilterTest_doFilter_doesNotEchoCallerNameHeader_Test() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/categories/get/1");
        request.addHeader("X-Caller-Name", "PilotAngularUi");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(response.getHeader("X-Caller-Name"));
    }
}
