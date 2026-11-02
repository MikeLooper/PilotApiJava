package com.pilotapi.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.read.ListAppender;
import com.pilotapi.repository.CategoryRepository;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RepositoryLoggingInterceptorTest {

    private final RepositoryLoggingInterceptor interceptor = new RepositoryLoggingInterceptor(CategoryRepository.class);
    private final Logger logger = (Logger) LoggerFactory.getLogger(RepositoryLoggingInterceptor.class);
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
    void RepositoryLoggingInterceptorTest_invoke_logsStartAndCompletion_andReturnsResult_Test() throws Throwable {
        MethodInvocation invocation = mock(MethodInvocation.class);
        when(invocation.getMethod()).thenReturn(CategoryRepository.class.getMethod("findAll"));
        Object result = new Object();
        when(invocation.proceed()).thenReturn(result);

        Object returned = interceptor.invoke(invocation);

        assertSame(result, returned);
        assertEquals(2, appender.list.size());
        assertEquals(Level.INFO, appender.list.get(0).getLevel());
        assertEquals("Database call started: CategoryRepository.findAll", appender.list.get(0).getFormattedMessage());
        assertEquals(Level.INFO, appender.list.get(1).getLevel());
        assertTrue(appender.list.get(1).getFormattedMessage().startsWith("Database call completed: CategoryRepository.findAll in "),
            appender.list.get(1).getFormattedMessage());
    }

    @Test
    void RepositoryLoggingInterceptorTest_invoke_whenCallFails_logsFailureAndRethrows_Test() throws Throwable {
        MethodInvocation invocation = mock(MethodInvocation.class);
        when(invocation.getMethod()).thenReturn(CategoryRepository.class.getMethod("findById", Object.class));
        IllegalStateException failure = new IllegalStateException("database unavailable");
        when(invocation.proceed()).thenThrow(failure);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> interceptor.invoke(invocation));

        assertSame(failure, thrown);
        assertEquals(2, appender.list.size());
        assertEquals("Database call started: CategoryRepository.findById", appender.list.get(0).getFormattedMessage());
        ILoggingEvent failed = appender.list.get(1);
        assertEquals(Level.WARN, failed.getLevel());
        assertTrue(failed.getFormattedMessage().startsWith("Database call failed: CategoryRepository.findById after "),
            failed.getFormattedMessage());
        assertSame(failure, ((ThrowableProxy) failed.getThrowableProxy()).getThrowable());
    }

    @Test
    void RepositoryLoggingInterceptorTest_invoke_withObjectMethod_proceedsWithoutLogging_Test() throws Throwable {
        MethodInvocation invocation = mock(MethodInvocation.class);
        when(invocation.getMethod()).thenReturn(Object.class.getMethod("toString"));
        when(invocation.proceed()).thenReturn("proxy");

        Object returned = interceptor.invoke(invocation);

        assertEquals("proxy", returned);
        assertTrue(appender.list.isEmpty());
    }
}
