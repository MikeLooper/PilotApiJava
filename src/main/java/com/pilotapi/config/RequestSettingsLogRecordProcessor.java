package com.pilotapi.config;

import com.pilotapi.common.RequestSettings;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.ReadWriteLogRecord;

/**
 * Adds the request's correlation and request IDs to every log record emitted within a request.
 * <p>
 * Must run before the exporting processor, which snapshots the record when it is emitted.
 */
public class RequestSettingsLogRecordProcessor implements LogRecordProcessor {

    static final AttributeKey<String> CORRELATION_ID = AttributeKey.stringKey("CorrelationId");
    static final AttributeKey<String> REQUEST_ID = AttributeKey.stringKey("Request.Id");

    @Override
    public void onEmit(Context context, ReadWriteLogRecord logRecord) {
        RequestSettings settings = RequestSettings.fromContext(context);
        if (settings == null) {
            return;
        }
        logRecord.setAttribute(CORRELATION_ID, settings.getCorrelationId());
        logRecord.setAttribute(REQUEST_ID, settings.getRequestId());
    }
}
