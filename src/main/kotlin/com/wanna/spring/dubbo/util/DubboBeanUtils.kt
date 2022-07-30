package com.wanna.spring.dubbo.util

import com.wanna.framework.beans.factory.config.BeanDefinitionRegistry
import com.wanna.framework.beans.factory.support.definition.BeanDefinition
import com.wanna.framework.beans.factory.support.definition.RootBeanDefinition
import com.wanna.spring.dubbo.annotation.ReferenceAnnotationBeanPostProcessor
import org.slf4j.LoggerFactory

/**
 * Dubbo的BeanUtils
 */
object DubboBeanUtils {

    // Logger
    private val logger = LoggerFactory.getLogger(DubboBeanUtils::class.java)

    /**
     * 给SpringBeanFactory当中去注册一些Dubbo的公共Bean
     *
     * @param registry BeanDefinitionRegistry
     */
    @JvmStatic
    fun registerCommonBeans(registry: BeanDefinitionRegistry) {
        // 注册处理@DubboReference注解的处理器
        BeanRegistrar.registerInfrastructureBean(
            registry,
            ReferenceAnnotationBeanPostProcessor.BEAN_NAME,
            ReferenceAnnotationBeanPostProcessor::class.java
        )
    }

}