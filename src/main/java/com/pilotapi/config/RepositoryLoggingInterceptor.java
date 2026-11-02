package com.pilotapi.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

/**
 * Logs the beginning and end of every database call made through a Spring Data repository.
 * <p>
 * Added to each repository proxy by {@link RepositoryLoggingPostProcessor}. The request's
 * correlation ID, request ID and caller name are attached to each line by
 * {@link com.pilotapi.common.RequestSettingsFilter} (MDC, for the console) and
 * {@link RequestSettingsLogRecordProcessor} (for exported logs).
 */
public class RepositoryLoggingInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryLoggingInterceptor.class);

    private final String repositoryName;

    public RepositoryLoggingInterceptor(Class<?> repositoryInterface) {
        this.repositoryName = repositoryInterface.getSimpleName();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (ReflectionUtils.isObjectMethod(invocation.getMethod())) {
            return invocation.proceed();
        }

        String call = repositoryName + "." + invocation.getMethod().getName();
        LOGGER.info("Database call started: {}", call);
        long startTime = System.nanoTime();
        try {
            Object result = invocation.proceed();
            LOGGER.info("Database call completed: {} in {} ms", call, elapsedMillis(startTime));
            return result;
        } catch (Throwable ex) {
            LOGGER.warn("Database call failed: {} after {} ms", call, elapsedMillis(startTime), ex);
            throw ex;
        }
    }

    private static long elapsedMillis(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000;
    }
}
