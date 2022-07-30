package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.factory.config.BeanDefinitionRegistry
import com.wanna.framework.context.annotation.AnnotationAttributesUtils
import com.wanna.framework.context.annotation.ImportBeanDefinitionRegistrar
import com.wanna.framework.context.aware.EnvironmentAware
import com.wanna.framework.core.environment.Environment
import com.wanna.framework.core.type.AnnotationMetadata

/**
 * 处理@EnablConfigurationBeanBindings注解，负责完成内部组合的value属性当中的@EnablConfigurationBeanBinding的解析工作
 *
 * @see EnableConfigurationBeanBindings
 */
open class ConfigurationBeanBindingsRegistrar : ImportBeanDefinitionRegistrar, EnvironmentAware {

    private var environment: Environment? = null

    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    override fun registerBeanDefinitions(annotationMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        val attributes = AnnotationAttributesUtils.fromMap(
            annotationMetadata.getAnnotationAttributes(EnableConfigurationBeanBindings::class.java)
        )
        val registrar = ConfigurationBeanBindingRegistrar()

        // 内部的全部注解，通过交给ConfigurationBeanBindingRegistrar去完成解析工作
        val bindings = attributes.getAnnotationArray("value")
        registrar.setEnvironment(environment ?: throw IllegalStateException("Spring的Environment环境对象不能为空"))  // init Environment
        bindings.forEach { registrar.registerConfigurationBeanDefinitions(it, registry) }
    }
}