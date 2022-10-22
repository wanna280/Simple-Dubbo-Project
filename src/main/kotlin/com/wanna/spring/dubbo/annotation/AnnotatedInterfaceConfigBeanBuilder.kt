package com.wanna.spring.dubbo.annotation

import com.wanna.framework.context.ApplicationContext
import com.wanna.framework.context.annotation.AnnotationAttributes
import com.wanna.framework.context.exception.NoSuchBeanDefinitionException
import com.wanna.framework.util.BeanFactoryUtils
import com.wanna.spring.dubbo.config.AbstractInterfaceConfig
import com.wanna.spring.dubbo.config.ApplicationConfig
import com.wanna.spring.dubbo.config.RegistryConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 提供基于注解配置的ConfigBean的Builder
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 *
 * @param attributes 注解属性
 * @param applicationContext ApplicationContext
 */
abstract class AnnotatedInterfaceConfigBeanBuilder<C : AbstractInterfaceConfig>(
    val attributes: AnnotationAttributes,
    val applicationContext: ApplicationContext
) {

    /**
     * Logger
     */
    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * 创建一个真正的Dubbo的Config对象
     *
     * @return 创建出来的Config对象
     */
    abstract fun doBuild(): C


    /**
     * 对给定的Dubbo的Config的Bean去进行配置
     */
    open fun configureBean(bean: C) {
        // 交给子类对ConfigBean去进行自定义处理
        postConfigureBean(attributes, bean)

        // 从ApplicationContext当中去获取注册中心的配置
        configureRegistryConfigs(bean)

        // 从ApplicationContext当中去获取Application的配置
        configureApplicationConfig(bean)

        // 交给子类对ConfigBean去进行自定义处理
        postConfigureBean(attributes, bean)
    }

    /**
     * 配置给定的ConfigBean的RegistryConfigs
     *
     * @param bean ConfigBean
     */
    private fun configureRegistryConfigs(bean: C) {
        val registries = attributes.getStringArray("registry")
        bean.registries = registries.map { applicationContext.getBean(it, RegistryConfig::class.java) }.toMutableList()
    }

    /**
     * 配置给定的ConfigBean的ApplicationConfig
     *
     * @param bean ConfigBean
     */
    private fun configureApplicationConfig(bean: C) {
        val application = attributes.getString("application")
        try {
            bean.application = applicationContext.getBean(application, ApplicationConfig::class.java)
        } catch (ex: NoSuchBeanDefinitionException) {
            // ignore
        }
    }

    /**
     * 执行build，构建出来目标对象
     *
     * @return 构建出来的目标对象
     */
    open fun build(): C {
        val bean = doBuild()
        configureBean(bean)
        if (logger.isInfoEnabled) {
            logger.info("ConfigBean[$bean]已经被构建完成")
        }
        return bean
    }

    protected abstract fun preConfigureBean(attributes: AnnotationAttributes, bean: C)

    protected abstract fun postConfigureBean(attributes: AnnotationAttributes, bean: C)
}