package com.pilotapi.common;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class RequestSettingsTest {

    @Test
    void RequestSettingsTest_initialize_withCorrelationIdHeader_usesHeaderValue_Test() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Correlation-Id", "7ea2c7f7-8b46-475d-86f8-7aaaa9e4a35b");

        RequestSettings settings = RequestSettings.initialize(request);

        assertEquals("7ea2c7f7-8b46-475d-86f8-7aaaa9e4a35b", settings.getCorrelationId());
    }

    @Test
    void RequestSettingsTest_initialize_withoutCorrelationIdHeader_generatesGuid_Test() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        RequestSettings settings = RequestSettings.initialize(request);

        assertDoesNotThrow(() -> UUID.fromString(settings.getCorrelationId()));
    }

    @Test
    void RequestSettingsTest_initialize_withBlankCorrelationIdHeader_generatesGuid_Test() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Correlation-Id", "   ");

        RequestSettings settings = RequestSettings.initialize(request);

        assertDoesNotThrow(() -> UUID.fromString(settings.getCorrelationId()));
    }

    @Test
    void RequestSettingsTest_initialize_withContainerRequestId_usesActiveRequestId_Test() {
        MockHttpServletRequest request = new MockHttpServletRequest() {
            @Override
            public String getRequestId() {
                return "2a";
            }
        };

        RequestSettings settings = RequestSettings.initialize(request);

        assertEquals("2a", settings.getRequestId());
    }

    @Test
    void RequestSettingsTest_initialize_withoutContainerRequestId_generatesDistinctGuidRequestIds_Test() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        RequestSettings first = RequestSettings.initialize(request);
        RequestSettings second = RequestSettings.initialize(request);

        assertDoesNotThrow(() -> UUID.fromString(first.getRequestId()));
        assertNotEquals(first.getRequestId(), second.getRequestId());
        assertNotEquals(first.getRequestId(), first.getCorrelationId());
    }

    @Test
    void RequestSettingsTest_from_calledTwice_returnsSameInstance_Test() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        RequestSettings first = RequestSettings.from(request);
        RequestSettings second = RequestSettings.from(request);

        assertSame(first, second);
        assertSame(first, request.getAttribute(RequestSettings.ATTRIBUTE_NAME));
    }
}
