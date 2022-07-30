package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.factory.config.BeanDefinitionRegistry
import com.wanna.framework.beans.factory.support.definition.RootBeanDefinition
import com.wanna.framework.context.annotation.ImportBeanDefinitionRegistrar
import com.wanna.framework.context.aware.EnvironmentAware
import com.wanna.framework.core.environment.ConfigurableEnvironment
import com.wanna.framework.core.environment.Environment
import com.wanna.framework.core.environment.MapPropertySource
import com.wanna.framework.core.environment.MutablePropertySources
import com.wanna.framework.core.type.AnnotationMetadata
import com.wanna.spring.dubbo.util.BeanRegistrar
import com.wanna.spring.dubbo.util.PropertySourcesUtils
import org.slf4j.LoggerFactory

/**
 * 负责处理Dubbo的@EnableConfigurationBeanBinding注解，提供DubboConfig配置类的绑定功能；
 * 支持直接将SpringEnvironment当中的信息转换为DubboConfig的SpringBean，并注册到SpringBeanFactory当中
 *
 * @see EnableConfigurationBeanBinding
 */
open class ConfigurationBeanBindingRegistrar : ImportBeanDefinitionRegistrar, EnvironmentAware {

    companion object {
        val CONFIGURATION_BINDING_ANNOTATION_CLASS = EnableConfigurationBeanBinding::class.java
        private val logger = LoggerFactory.getLogger(ConfigurationBeanBindingRegistrar::class.java)
    }

    private var environment: ConfigurableEnvironment? = null

    override fun setEnvironment(environment: Environment) {
        this.environment = environment as ConfigurableEnvironment
    }

    override fun registerBeanDefinitions(annotationMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        val attributes = annotationMetadata.getAnnotationAttributes(CONFIGURATION_BINDING_ANNOTATION_CLASS)
        registerConfigurationBeanDefinitions(attributes, registry)
    }

    /**
     * 注册一个Dubbo的配置类的BeanDefinition，并从配置文件当中解析到的相关属性设置到BeanDefinition的Attribute当中，
     * 方便后续的操作当中，可以从BeanDefinition当中去获取到该属性，从而完成DubboConfig的属性设置工作
     *
     * @param attributes 一个@EnableConfigurationBeanBinding注解的属性信息
     * @param registry BeanDefinitionRegistry
     */
    open fun registerConfigurationBeanDefinitions(attributes: Map<String, Any>, registry: BeanDefinitionRegistry) {
        var prefix = attributes["prefix"] as String
        val type = attributes["type"] as Class<*>
        val multiple = attributes["multiple"] as Boolean
        val environment = environment ?: throw IllegalStateException("SpringBeanFactory的环境对象不能为空，必须先完成初始化")
        prefix = environment.resolvePlaceholders(prefix)!!

        // 1.将Dubbo相关的配置信息转换为SpringBeanDefinition，并注册到registry当中(相关的属性被放入到BeanDefinition的Attribute当中)
        registerConfigurationBeans(prefix, type, multiple, registry)

        // 2.注册一个完成配置绑定的BeanPostProcessor，完成DubboConfig配置类的属性的绑定工作
        // 为啥不使用PropertyValues的方式，直接将属性添加到PropertyValues当中，让Spring自行完成注入？
        registerConfigurationBindingBeanPostProcessor(registry)
    }

    /**
     * 给定要去进行注册的相关信息，去注册Dubbo配置类到容器当中
     *
     * @param prefix 要绑定的配置文件的前缀
     * @param multiple 是否要去绑定多个DubboConfig的SpringBean
     * @param configClass 要绑定的Dubbo配置类
     * @param registry BeanDefinitionRegistry
     */
    private fun registerConfigurationBeans(
        prefix: String,
        configClass: Class<*>,
        multiple: Boolean,
        registry: BeanDefinitionRegistry
    ) {
        // 获取指定的前缀下的所有的属性值列表
        val subProperties =
            PropertySourcesUtils.getSubProperties(this.environment!!.getPropertySources(), this.environment!!, prefix)
        if (subProperties.isEmpty()) {
            if (logger.isDebugEnabled) {
                logger.debug("没有找到去绑定[${configClass.name}]配置的属性值前缀[$prefix]")
            }
            return
        }
        val beanNames = if (multiple) resolveMultipleBeanNames(subProperties)
        else setOf(resolveSingleBeanName(subProperties, configClass, registry))
        beanNames.forEach { beanName ->
            registerConfigurationBean(multiple, beanName, configClass, subProperties, registry)
        }
    }


    private fun registerConfigurationBean(
        multiple: Boolean,
        beanName: String,
        configClass: Class<*>,
        properties: Map<String, Any>,
        registry: BeanDefinitionRegistry
    ) {
        val definition = RootBeanDefinition(configClass)
        definition.setSource(CONFIGURATION_BINDING_ANNOTATION_CLASS)  // set Source as Marker

        // (如果multiple=true)解析到所有的以beanName作为开头的所有的Properties，并去掉它的beanName的前缀
        val subProperties = resolveSubProperties(multiple, beanName, properties)

        // 将解析到的subProperties，添加到BeanDefinition的属性当中
        definition.setAttribute(ConfigurationBeanBindingPostProcessor.CONFIGURATION_PROPERTIES_ATTRIBUTE, subProperties)
        registry.registerBeanDefinition(beanName, definition)
    }

    /**
     * 从Property当中去解析多个beanName
     *
     * @param subProperties Environment当中的属性值(不包含前缀)列表
     */
    private fun resolveMultipleBeanNames(subProperties: Map<String, Any>): Set<String> {
        val beanNames = HashSet<String>()
        subProperties.keys.forEach { subName ->
            val index = subName.indexOf(".")
            if (index > 0) {
                beanNames.add(subName.substring(0, index))
            }
        }
        return beanNames
    }

    /**
     * 解析单个配置的beanName的情况
     */
    private fun resolveSingleBeanName(
        subProperties: Map<String, Any>,
        configClass: Class<*>,
        registry: BeanDefinitionRegistry
    ): String {
        if (subProperties.containsKey("id")) {
            return subProperties["id"].toString()
        }
        return configClass.name
    }

    /**
     * 解析SubProperties，因为多个DubboConfig配置类的属性时，
     * 格式如下:"dubbo.registries.wanna.id"，这个类给定的configurationProperties，
     * 已经将前缀"dubbo.registries"去掉的了，但是我们这里实际上需要的是匹配到所有的以beanName
     * 作为开头的propertyKey，把"wanna"(beanName)也给去掉，并返回最终的匹配结果
     *
     * @return 解析完成的去掉了beanName作为前缀的subProperties
     */
    private fun resolveSubProperties(
        multiple: Boolean,
        beanName: String,
        configurationProperties: Map<String, Any>
    ): Map<String, Any> {
        if (!multiple) {
            return configurationProperties
        }
        val propertySources = MutablePropertySources()
        propertySources.addLast(MapPropertySource("_", configurationProperties))
        return PropertySourcesUtils.getSubProperties(propertySources, this.environment!!, beanName)
    }

    /**
     * 注册一个完成Dubbo配置类绑定的PostProcessor，负责将属性绑定到具体的DubboConfig的Bean的身上
     *
     * @param registry BeanDefinitionRegistry
     */
    private fun registerConfigurationBindingBeanPostProcessor(registry: BeanDefinitionRegistry) {
        BeanRegistrar.registerInfrastructureBean(
            registry,
            ConfigurationBeanBindingPostProcessor.NAME,
            ConfigurationBeanBindingPostProcessor::class.java
        )
    }
}