package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.factory.config.BeanDefinitionRegistry
import com.wanna.framework.context.annotation.ClassPathBeanDefinitionScanner

/**
 * DubboService的BeanDefinitionScanner，将所有的@DubboService注解的类去扫描并注册到BeanDefinitionRegistry当中
 *
 * @param registry 需要注册BeanDefinition的BeanDefinitionRegistry
 */
open class DubboServiceBeanDefinitionScanner(registry: BeanDefinitionRegistry) :
    ClassPathBeanDefinitionScanner(registry, false)