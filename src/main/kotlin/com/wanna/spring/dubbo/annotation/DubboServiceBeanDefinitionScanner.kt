package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.factory.config.BeanDefinitionRegistry
import com.wanna.framework.context.annotation.ClassPathBeanDefinitionScanner

/**
 * DubboService的BeanDefintionScanner
 *
 * @param registry BeanDefinitionRegistry
 */
open class DubboServiceBeanDefinitionScanner(registry: BeanDefinitionRegistry) :
    ClassPathBeanDefinitionScanner(registry, false)