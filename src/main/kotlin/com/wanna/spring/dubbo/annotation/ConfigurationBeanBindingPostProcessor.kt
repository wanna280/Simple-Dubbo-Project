package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.BeanFactoryAware
import com.wanna.framework.beans.factory.BeanFactory
import com.wanna.framework.beans.factory.config.ConfigurableListableBeanFactory
import com.wanna.framework.beans.factory.support.definition.BeanDefinition
import com.wanna.framework.context.processor.beans.BeanPostProcessor
import com.wanna.framework.core.Ordered
import com.wanna.framework.core.PriorityOrdered
import com.wanna.framework.core.convert.TypeDescriptor
import com.wanna.framework.core.util.ReflectionUtils
import com.wanna.spring.dubbo.annotation.ConfigurationBeanBindingRegistrar.Companion.CONFIGURATION_BINDING_ANNOTATION_CLASS
import java.lang.reflect.Method

/**
 * Dubbo配置类的属性的绑定的BeanPostProcessor，对应SpringBoot的@ConfigurationProperties注解的相关功能，都是完成属性值的绑定工作；
 * 它主要将之前的Dubbo的配置Bean当中的各个属性去进行设置，它的所有属性都已经被放入到BeanDefinition的Attribute当中，因此在这里就可以去
 * 从Attribute当中去获取到之前保存的数据，并绑定给Bean的字段当中
 */
open class ConfigurationBeanBindingPostProcessor : BeanPostProcessor, PriorityOrdered, BeanFactoryAware {

    companion object {
        // beanName
        const val NAME = "configurationBeanBindingPostProcessor"

        // ConfigurationProperties Attribute Name
        const val CONFIGURATION_PROPERTIES_ATTRIBUTE = "configurationProperties"
    }

    private var order = Ordered.ORDER_LOWEST

    private var beanFactory: ConfigurableListableBeanFactory? = null

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory as ConfigurableListableBeanFactory
    }

    override fun getOrder() = order

    /**
     * 从BeanDefinition当中去获取到"configurationProperties"属性，从而获取到之前放入进来的配置信息，
     * 从而去完成属性的绑定工作
     *
     * @param bean bean
     * @param beanName beanName
     */
    override fun postProcessBeforeInitialization(beanName: String, bean: Any): Any? {
        val beanDefinition = this.beanFactory?.getBeanDefinition(beanName)

        // 判断source是否是EnableConfigurationBeanBinding，如果是的话，说明它是一个Dubbo的配置类
        // 我们需要去对该Bean当中的各个属性去完成设置
        if (isConfigurationBean(bean, beanDefinition)) {
            bindConfigurationBean(bean, beanDefinition!!)
        }
        return bean
    }

    /**
     * 绑定DubboConfig的配置类，从BeanDefinition的Attributes当中去获取到之前完成保存的所有的配置信息，
     * 在这里将之前保存的所有的配置信息，全部apply给该Bean
     */
    @Suppress("UNCHECKED_CAST")
    private fun bindConfigurationBean(bean: Any, beanDefinition: BeanDefinition) {
        val beanClass = bean::class.java

        // 从BeanDefinition的属性当中去获取到之前放入进去的属性
        val configurationProperties =
            beanDefinition.getAttribute(CONFIGURATION_PROPERTIES_ATTRIBUTE) as Map<String, Any>

        // 遍历当前的Dubbo的配置类当中的所有的属性，去完成绑定工作
        configurationProperties.forEach { (k, v) ->
            val field = ReflectionUtils.findField(beanClass, k) ?: return@forEach
            val setterMethod = setterMethod(beanClass, k, field.type) ?: return@forEach
            val conversionService = this.beanFactory?.getConversionService() ?: return
            val fieldTypeDescriptor = TypeDescriptor.forField(field)
            if (conversionService.canConvert(TypeDescriptor.forClass(String::class.java), fieldTypeDescriptor)) {
                val convertedValue = conversionService.convert(v, fieldTypeDescriptor)
                ReflectionUtils.makeAccessible(setterMethod)
                ReflectionUtils.invokeMethod(setterMethod, bean, convertedValue)
            }
        }
    }

    private fun setterMethod(clazz: Class<*>, fieldName: String, fieldType: Class<*>): Method? {
        // 获取setter的方法
        val setMethodName = "set" + fieldName[0].uppercaseChar() + fieldName.substring(1)
        return ReflectionUtils.findMethod(clazz, setMethodName, fieldType)
    }

    /**
     * 判断它是否是一个Dubbo的配置类的SpringBean？
     *
     * @param bean bean
     * @param beanDefinition beanDefinition
     * @return 如果source==EnableConfigurationBeanBinding，return true；否则return false
     */
    private fun isConfigurationBean(bean: Any, beanDefinition: BeanDefinition?): Boolean {
        return beanDefinition != null && beanDefinition.getSource() == CONFIGURATION_BINDING_ANNOTATION_CLASS
    }
}