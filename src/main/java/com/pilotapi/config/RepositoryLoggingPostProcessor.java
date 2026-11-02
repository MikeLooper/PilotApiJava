package com.pilotapi.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.stereotype.Component;

/**
 * Adds a {@link RepositoryLoggingInterceptor} to every Spring Data repository, so each
 * database call made through a repository logs its beginning and end.
 * <p>
 * The interceptor is placed first in the repository proxy's chain, ahead of the transaction
 * interceptor, so the logged duration covers the whole call, including the commit.
 */
@Component
public class RepositoryLoggingPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof RepositoryFactoryBeanSupport<?, ?, ?> factoryBean) {
            factoryBean.addRepositoryFactoryCustomizer(factory -> factory.addRepositoryProxyPostProcessor(
                (proxyFactory, repositoryInformation) -> proxyFactory.addAdvice(0,
                    new RepositoryLoggingInterceptor(repositoryInformation.getRepositoryInterface()))));
        }
        return bean;
    }
}
