package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.factory.config.BeanDefinitionRegistry
import com.wanna.framework.context.annotation.AnnotatedBeanDefinitionReader
import com.wanna.framework.context.annotation.ImportBeanDefinitionRegistrar
import com.wanna.framework.core.type.AnnotationMetadata
import com.wanna.spring.dubbo.util.DubboBeanUtils

/**
 * DubboConfig的注册器，负责处理@EnableDubbo注解，往往容器当中导入对应类型的处理器(Single/Multiple)；
 * 从而实现将Spring配置文件(Environment)当中的内容，绑定到一个Dubbo的配置类(RegistryConfig/ProtocolConfig等)上的功能
 *
 * @see EnableDubboConfig
 * @see DubboConfigConfiguration.Multiple
 * @see DubboConfigConfiguration.Single
 */
open class DubboConfigConfigurationRegistrar : ImportBeanDefinitionRegistrar {

    override fun registerBeanDefinitions(annotationMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        val attributes = annotationMetadata.getAnnotations().get(EnableDubboConfig::class.java)
        val reader = AnnotatedBeanDefinitionReader(registry)

        // 注册处理单个DubboConfig的配置类到BeanFactory当中
        reader.registerBean(DubboConfigConfiguration.Single::class.java)
        val multiple = attributes.getBoolean("multiple")
        // 注册处理多个DubboConfig的配置类到BeanFactory当中(在Dubbo2.6.6之后才支持)
        if (multiple) {
            reader.registerBean(DubboConfigConfiguration.Multiple::class.java)
        }
        // 注册一些Dubbo通用的Bean
        DubboBeanUtils.registerCommonBeans(registry)
    }
}