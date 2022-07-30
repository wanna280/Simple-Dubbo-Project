package com.wanna.spring.dubbo.util

import com.wanna.framework.beans.factory.config.BeanDefinitionRegistry
import com.wanna.framework.beans.factory.support.definition.BeanDefinition
import com.wanna.framework.beans.factory.support.definition.RootBeanDefinition

object BeanRegistrar {

    @JvmStatic
    fun registerInfrastructureBean(registry: BeanDefinitionRegistry, beanName: String, beanClass: Class<*>) {
        if (registry.containsBeanDefinition(beanName)) {
            return
        }
        val rootBeanDefinition = RootBeanDefinition(beanClass)
        rootBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
        registry.registerBeanDefinition(beanName, rootBeanDefinition)
    }
}