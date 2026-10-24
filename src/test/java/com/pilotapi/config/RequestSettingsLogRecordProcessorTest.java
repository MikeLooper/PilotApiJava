package com.pilotapi.config;

import com.pilotapi.common.RequestSettings;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.ReadWriteLogRecord;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestSettingsLogRecordProcessorTest {

    private final List<LogRecordData> exported = new ArrayList<>();
    private final SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
        .addLogRecordProcessor(LogRecordProcessor.composite(new RequestSettingsLogRecordProcessor(), new LogRecordProcessor() {
            @Override
            public void onEmit(Context context, ReadWriteLogRecord logRecord) {
                exported.add(logRecord.toLogRecordData());
            }
        }))
        .build();
    private final Logger logger = loggerProvider.get("test");

    @AfterEach
    void tearDown() {
        loggerProvider.close();
    }

    @Test
    void RequestSettingsLogRecordProcessorTest_onEmit_withinRequest_addsCorrelationAndRequestId_Test() {
        try (Scope ignored = new RequestSettings("request-1", "correlation-1").makeCurrent()) {
            logger.logRecordBuilder().setBody("message").emit();
        }

        LogRecordData record = exported.getFirst();
        assertEquals("correlation-1", record.getAttributes().get(RequestSettingsLogRecordProcessor.CORRELATION_ID));
        assertEquals("request-1", record.getAttributes().get(RequestSettingsLogRecordProcessor.REQUEST_ID));
    }

    @Test
    void RequestSettingsLogRecordProcessorTest_onEmit_outsideRequest_addsNoRequestAttributes_Test() {
        logger.logRecordBuilder().setBody("message").emit();

        LogRecordData record = exported.getFirst();
        assertNull(record.getAttributes().get(RequestSettingsLogRecordProcessor.CORRELATION_ID));
        assertNull(record.getAttributes().get(RequestSettingsLogRecordProcessor.REQUEST_ID));
    }
}
