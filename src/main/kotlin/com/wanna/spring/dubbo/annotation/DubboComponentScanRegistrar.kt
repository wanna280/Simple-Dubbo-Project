package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.factory.config.BeanDefinitionRegistry
import com.wanna.framework.beans.factory.support.definition.BeanDefinition
import com.wanna.framework.beans.factory.support.definition.RootBeanDefinition
import com.wanna.framework.context.annotation.ImportBeanDefinitionRegistrar
import com.wanna.framework.core.type.AnnotationMetadata
import com.wanna.framework.util.ClassUtils
import com.wanna.spring.dubbo.util.DubboBeanUtils

/**
 * Dubbo的组件扫描(ComponentScan)的注册器，负责处理@DubboComponentScan注解
 * 负责将要进行扫描的包下的全部的DubboService全部注册到Spring的BeanFactory当中
 *
 * @see DubboComponentScan
 */
open class DubboComponentScanRegistrar : ImportBeanDefinitionRegistrar {

    /**
     * 注册提供DubboComponentScan的组件到BeanDefinitionRegistry当中
     *
     * @param annotationMetadata 标注了@DubboComponentScan的类的元信息
     * @param registry BeanDefinitionRegistry
     */
    override fun registerBeanDefinitions(annotationMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        // 从注解当中的interfaceClass当中去解析到要进行扫描的包的列表
        val packagesToScan = getDubboServicePackagesToScan(annotationMetadata)

        // 注册一个DubboServiceClass的PostProcessor，负责去处理@DubboService注解
        registerDubboServiceClassPostProcessor(packagesToScan, registry)

        // 注册一些Dubbo的公共的Bean，比如处理@DubboReference注解的处理器
        DubboBeanUtils.registerCommonBeans(registry)
    }

    /**
     * 注册一个DubboServiceClass的PostProcessor到SpringBeanFactory当中，
     * 该组件的作用是：负责将标注了@DuuboService注解的Bean注册到SpringBeanFactory当中
     *
     * @param packagesToScan 要去进行扫描DubboService的包的列表
     * @param registry BeanDefinitionRegistry
     */
    private fun registerDubboServiceClassPostProcessor(packagesToScan: Set<String>, registry: BeanDefinitionRegistry) {
        val rootBeanDefinition = RootBeanDefinition(ServiceClassPostProcessor::class.java)

        // add PropertyValues to ServiceClassPostProcessor
        rootBeanDefinition.getPropertyValues().addPropertyValue("packages", packagesToScan)
        rootBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
        registry.registerBeanDefinition(ServiceClassPostProcessor::class.java.name, rootBeanDefinition)
    }

    /**
     * 获取@DubboComponentScan注解上要去进行扫描的DubboService的包的列表
     *
     * @param annotationMetadata @DubboComponentScan注解上的相关的属性信息
     * @return 解析到的要扫描的包的列表(如果注解上没有合适的配置信息的话，那么默认情况会以标注@DubboComponentScan的类的包作为扫描的包)
     */
    @Suppress("UNCHECKED_CAST")
    private fun getDubboServicePackagesToScan(annotationMetadata: AnnotationMetadata): Set<String> {
        val attributes = annotationMetadata.getAnnotationAttributes(DubboComponentScan::class.java.name) ?: emptyMap()

        val packages = HashSet<String>()
        packages += (attributes["value"] as Array<String>)
        packages += (attributes["basePackages"] as Array<String>)
        val basePackageClasses = attributes["basePackageClasses"] as Array<Class<*>>
        packages += basePackageClasses.map { it.packageName }.toList()
        if (packages.isEmpty()) {
            packages += ClassUtils.getPackageName(annotationMetadata.getClassName())
        }
        return packages
    }
}