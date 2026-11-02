package com.pilotapi.config;

import com.pilotapi.repository.CategoryRepository;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RepositoryLoggingPostProcessorTest {

    private final RepositoryLoggingPostProcessor postProcessor = new RepositoryLoggingPostProcessor();

    @Test
    void RepositoryLoggingPostProcessorTest_postProcessBeforeInitialization_withOtherBean_returnsBeanUnchanged_Test() {
        Object bean = new Object();

        Object result = postProcessor.postProcessBeforeInitialization(bean, "other");

        assertSame(bean, result);
    }

    @Test
    void RepositoryLoggingPostProcessorTest_postProcessBeforeInitialization_withRepositoryFactoryBean_addsLoggingInterceptorFirst_Test() {
        RepositoryFactoryBeanSupport<?, ?, ?> factoryBean = mock(RepositoryFactoryBeanSupport.class);
        RepositoryFactorySupport factory = mock(RepositoryFactorySupport.class);
        RepositoryInformation repositoryInformation = mock(RepositoryInformation.class);
        doReturn(CategoryRepository.class).when(repositoryInformation).getRepositoryInterface();
        MethodInterceptor existingAdvice = invocation -> invocation.proceed();
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addAdvice(existingAdvice);

        Object result = postProcessor.postProcessBeforeInitialization(factoryBean, "categoryRepository");

        assertSame(factoryBean, result);
        ArgumentCaptor<RepositoryFactoryCustomizer> customizer = ArgumentCaptor.forClass(RepositoryFactoryCustomizer.class);
        verify(factoryBean).addRepositoryFactoryCustomizer(customizer.capture());
        customizer.getValue().customize(factory);
        ArgumentCaptor<RepositoryProxyPostProcessor> proxyPostProcessor =
            ArgumentCaptor.forClass(RepositoryProxyPostProcessor.class);
        verify(factory).addRepositoryProxyPostProcessor(proxyPostProcessor.capture());
        proxyPostProcessor.getValue().postProcess(proxyFactory, repositoryInformation);
        assertEquals(2, proxyFactory.getAdvisorCount());
        assertInstanceOf(RepositoryLoggingInterceptor.class, proxyFactory.getAdvisors()[0].getAdvice());
        assertSame(existingAdvice, proxyFactory.getAdvisors()[1].getAdvice());
    }
}
