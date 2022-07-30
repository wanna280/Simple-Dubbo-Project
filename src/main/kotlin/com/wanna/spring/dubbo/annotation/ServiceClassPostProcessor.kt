package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.factory.config.BeanDefinitionRegistry
import com.wanna.framework.beans.factory.config.ConfigurableListableBeanFactory
import com.wanna.framework.beans.factory.config.RuntimeBeanReference
import com.wanna.framework.beans.factory.config.SingletonBeanRegistry
import com.wanna.framework.beans.factory.support.BeanDefinitionHolder
import com.wanna.framework.beans.factory.support.ManagedList
import com.wanna.framework.beans.factory.support.definition.AbstractBeanDefinition
import com.wanna.framework.beans.factory.support.definition.BeanDefinition
import com.wanna.framework.beans.factory.support.definition.RootBeanDefinition
import com.wanna.framework.context.annotation.AnnotationAttributes
import com.wanna.framework.context.annotation.AnnotationAttributesUtils
import com.wanna.framework.context.annotation.AnnotationBeanNameGenerator
import com.wanna.framework.context.annotation.BeanNameGenerator
import com.wanna.framework.context.aware.BeanClassLoaderAware
import com.wanna.framework.context.aware.EnvironmentAware
import com.wanna.framework.context.processor.factory.BeanDefinitionRegistryPostProcessor
import com.wanna.framework.context.processor.factory.internal.ConfigurationClassPostProcessor
import com.wanna.framework.core.environment.Environment
import com.wanna.framework.core.type.filter.AnnotationTypeFilter
import com.wanna.framework.core.util.AnnotationConfigUtils
import com.wanna.framework.core.util.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR
import com.wanna.framework.core.util.StringUtils
import com.wanna.spring.dubbo.config.spring.ServiceBean
import com.wanna.spring.dubbo.config.context.DubboBootstrapApplicationListener
import com.wanna.spring.dubbo.util.BeanRegistrar
import com.wanna.spring.dubbo.util.DubboAnnotationUtils
import org.slf4j.LoggerFactory

/**
 * Dubbo的Service的处理器，是一个BeanDefinitionRegistryPostProcessor，负责将标注了@DubboService的Bean注册到Spring当中
 *
 * @see DubboService
 */
open class ServiceClassPostProcessor(// 要扫描的包的列表
    private var packages: Set<String> = emptySet()
) : BeanDefinitionRegistryPostProcessor, BeanClassLoaderAware, EnvironmentAware {
    companion object {
        // 要注册为DubboService的注解列表
        private val serviceAnnotationTypes = setOf(
            DubboService::class.java
        )

        // Logger
        private val logger = LoggerFactory.getLogger(ServiceClassPostProcessor::class.java)
    }

    private lateinit var classLoader: ClassLoader

    private lateinit var environment: Environment

    override fun setBeanClassLoader(classLoader: ClassLoader) {
        this.classLoader = classLoader
    }

    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    open fun setPackages(packagesToScan: Set<String>) {
        this.packages = packagesToScan
    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        // 注册一个Dubbo的监听器，提供所有的Service的暴露到注册中心当中
        BeanRegistrar.registerInfrastructureBean(
            registry,
            DubboBootstrapApplicationListener.BEAN_NAME,
            DubboBootstrapApplicationListener::class.java
        )

        val packagesToScan = packages
        if (packagesToScan.isEmpty()) {
            if (logger.isDebugEnabled) {
                logger.debug("DubboComponentScan要扫描的包为空，无法去完成DubboService的扫描")
            }
        } else {
            // 扫描@DubboService注解的Bean，并完成注册
            registerServiceBeans(packagesToScan, registry)
        }
    }

    private fun registerServiceBeans(packagesToScan: Set<String>, registry: BeanDefinitionRegistry) {
        // 创建一个Scanner，并添加IncludeFilter，去提供@DubboService注解的匹配功能
        val scanner = DubboServiceBeanDefinitionScanner(registry)
        serviceAnnotationTypes.forEach { scanner.addIncludeFilter(AnnotationTypeFilter(it)) }

        // 设置beanNameGenerator
        val beanNameGenerator = resolveBeanNameGenerator(registry)
        scanner.setBeanNameGenerator(beanNameGenerator)

        // 扫描指定的包下的所有DubboService的所有BeanDefinition的列表
        val beanDefinitions = scanner.doScan(*packagesToScan.toTypedArray())
        beanDefinitions.forEach {
            registerServiceBean(it, registry, scanner)
        }
    }

    /**
     * 给定一个@DubboService的BeanDefinition，去注册一个ServiceBean
     *
     * @param beanDefinitionHolder ServiceBean的BeanDefinitionHolder
     * @param registry BeanDefinitionRegistry
     * @param scanner scanner
     */
    private fun registerServiceBean(
        beanDefinitionHolder: BeanDefinitionHolder,
        registry: BeanDefinitionRegistry,
        scanner: DubboServiceBeanDefinitionScanner
    ) {
        val beanClass =
            beanDefinitionHolder.beanDefinition.getBeanClass() ?: throw IllegalStateException("beanClass不能为null")

        // 获取到@DubboService注解的相关属性信息
        val dubboServiceAnnotation = getDubboServiceAnnotation(beanClass)
        val attributes = AnnotationAttributesUtils.asAnnotationAttributes(dubboServiceAnnotation)!!

        // 解析DubboService要进行注册的Service接口
        // 1.如果@DubboService上指定了interfaceClass，那么使用该接口作为ServiceInterface
        // 2.如果@DubboService上没有指定interfaceClass，那么应该使用该类的第一个接口作为ServiceInterface
        val interfaceClass = DubboAnnotationUtils.resolveServiceInterfaceClass(attributes, beanClass)

        // 构建DubboService的接口对应的ServiceBean的BeanDefinition
        val definition = buildServiceBeanDefinition(
            dubboServiceAnnotation,
            attributes,
            interfaceClass,
            beanDefinitionHolder.beanName
        )
        val serviceBeanName = generateServiceBeanName(attributes, interfaceClass)

        // 将beanName作为id去添加到BeanDefinition的PropertyValues当中
        if (!definition.getPropertyValues().containsProperty("id")) {
            definition.getPropertyValues().addPropertyValue("id", serviceBeanName)
        }

        // 注册BeanDefinition
        registry.registerBeanDefinition(serviceBeanName, definition)
    }

    /**
     * 尝试从给定的beanClass当中去寻找到合适的@DubboService注解
     *
     * @param beanClass beanClass
     * @return 如果找到了合适的DubboService注解，那么return 该注解的实例对象
     * @throws IllegalStateException 如果没有找到@DubboService注解
     */
    private fun getDubboServiceAnnotation(beanClass: Class<*>): Annotation {
        serviceAnnotationTypes.forEach {
            if (beanClass.isAnnotationPresent(it)) {
                return beanClass.getAnnotation(it)
            }
        }
        throw IllegalStateException("无法从[$beanClass]上寻找到合适的@DubboService注解")
    }

    /**
     * 构建一个DubboServiceBean的BeanDefinition，将@DubboService注解当中的信息全部设置到ServiceBean当中去
     *
     * @param serviceAnnotation @DubboService注解
     * @param serviceAnnotationAttributes @DubboService注解属性
     * @param interfaceClass 要去进行注册的DubboService的接口
     * @param annotatedServiceBeanName 标注@DubboService的beanName
     * @return 构建好的DubboService的BeanDefinition
     */
    private fun buildServiceBeanDefinition(
        serviceAnnotation: Annotation,
        serviceAnnotationAttributes: AnnotationAttributes,
        interfaceClass: Class<*>,
        annotatedServiceBeanName: String
    ): AbstractBeanDefinition {
        val definition = RootBeanDefinition(ServiceBean::class.java)
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)

        val propertyValues = definition.getPropertyValues()
        propertyValues.addPropertyValue("interfaceName", interfaceClass.name)
        propertyValues.addPropertyValue("interfaceClass", interfaceClass)
        propertyValues.addPropertyValue("ref", RuntimeBeanReference(annotatedServiceBeanName))

        // 解析配置的ProtocolConfig的beanName列表
        val protocols = serviceAnnotationAttributes.getStringArray("protocol")
        if (protocols.isNotEmpty()) {
            val protocolsReferences = protocols.map { RuntimeBeanReference(it) }.toCollection(ManagedList())
            propertyValues.addPropertyValue("protocols", protocolsReferences)
        }

        // 解析配置的RegistryConfig的beanName
        val registries = serviceAnnotationAttributes.getStringArray("registry")
        if (registries.isNotEmpty()) {
            val registriesReferences = registries.map { RuntimeBeanReference(it) }.toCollection(ManagedList())
            propertyValues.addPropertyValue("registries", registriesReferences)
        }

        // 解析配置的ApplicationConfig的beanName
        val application = serviceAnnotationAttributes.getString("application")
        if (StringUtils.hasText(application)) {
            propertyValues.addPropertyValue("application", RuntimeBeanReference(application!!))
        }

        return definition
    }


    /**
     * 从BeanDefinitionRegistry当中尝试去解析BeanNameGenerator，如果没有解析到，那么创建一个默认的BeanNameGenerator；
     * 官方说明：更好的方式是，应该使用ConfigurationClassPostProcessor内部的componentScan作为BeanNameGenerator，
     * 但是ConfigurationClassPostProcessor它并不对外提供内部的BeanNameGenerator的Getter，因此被迫采用AnnotationBeanNameGenerator
     *
     * @param registry BeanDefinitionRegistry
     * @see ConfigurationClassPostProcessor.componentScanBeanNameGenerator
     * @return BeanNameGenerator
     */
    private fun resolveBeanNameGenerator(registry: BeanDefinitionRegistry): BeanNameGenerator {
        var beanNameGenerator: BeanNameGenerator? = null
        if (registry is SingletonBeanRegistry) {
            beanNameGenerator =
                registry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR) as BeanNameGenerator?
        }
        if (beanNameGenerator == null) {
            if (logger.isInfoEnabled) {
                logger.info("无法根据[${CONFIGURATION_BEAN_NAME_GENERATOR}从BeanFactory当中去获取到BeanNameGenerator]")
                logger.info("将会采用[${AnnotationBeanNameGenerator::class.java.name}]作为BeanNameGenerator去进行beanName的生成，有可能会存在一些潜在的问题")
            }
            beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE
        }
        return beanNameGenerator
    }

    /**
     * 生成ServiceBean的beanName
     *
     * @param attributes @DubboService注解属性信息
     * @param interfaceClass @DubboService的接口类
     */
    private fun generateServiceBeanName(attributes: AnnotationAttributes, interfaceClass: Class<*>): String {
        return "Service:${interfaceClass.name}"
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {

    }
}