package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.BeanFactoryAware
import com.wanna.framework.beans.factory.BeanFactory
import com.wanna.framework.context.aware.BeanClassLoaderAware
import com.wanna.framework.context.aware.EnvironmentAware
import com.wanna.framework.context.processor.beans.InstantiationAwareBeanPostProcessor
import com.wanna.framework.context.processor.beans.MergedBeanDefinitionPostProcessor
import com.wanna.framework.core.Ordered
import com.wanna.framework.core.PriorityOrdered
import com.wanna.framework.core.environment.Environment

/**
 * Dubbo的抽象的注解处理器，用来完成Dubbo的注解的自动注入功能
 *
 * @see ReferenceAnnotationBeanPostProcessor
 * @see DubboReference
 */
abstract class AbstractAnnotationBeanPostProcessor : MergedBeanDefinitionPostProcessor, BeanFactoryAware,
    EnvironmentAware, PriorityOrdered, BeanClassLoaderAware, InstantiationAwareBeanPostProcessor {

    private var beanFactory: BeanFactory? = null

    private var classLoader: ClassLoader? = null

    private var environment: Environment? = null

    private var order: Int = Ordered.ORDER_LOWEST

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    override fun setBeanClassLoader(classLoader: ClassLoader) {
        this.classLoader = classLoader
    }

    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    override fun getOrder() = this.order
}