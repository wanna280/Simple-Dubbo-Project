package com.wanna.spring.dubbo.annotation

import com.wanna.framework.context.annotation.AnnotationAttributes
import com.wanna.framework.core.environment.Environment
import com.wanna.framework.util.StringUtils

/**
 * ServiceBean的beanName生成器
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/22
 *
 * @param attributes 注解属性
 * @param serviceInterfaceName Service接口类名
 * @param environment Environment
 */
open class ServiceBeanNameBuilder(
    private val attributes: AnnotationAttributes,
    private val serviceInterfaceName: String,
    private val environment: Environment
) {
    private var group = attributes.getString("group")

    private var version = attributes.getString("version")

    open fun group(group: String): ServiceBeanNameBuilder {
        this.group = group
        return this
    }

    open fun version(version: String): ServiceBeanNameBuilder {
        this.version = version
        return this
    }

    /**
     * 生成最终的ServiceBean的beanName
     *
     * @return 生成得到的beanName("{interfaceName}#{group}#{version}")
     */
    open fun build(): String {
        val builder = StringBuilder("ServiceBean")
        append(builder, serviceInterfaceName)
        append(builder, group)
        append(builder, version)
        return environment.resolveRequiredPlaceholders(builder.toString())
    }

    companion object {

        /**
         * 分隔符
         */
        private const val SEPARATOR = "#"

        /**
         * 将给定的value(如果value不为空)添加到builder当中
         *
         * @param builder builder
         * @param value 需要添加的字符串
         */
        @JvmStatic
        private fun append(builder: StringBuilder, value: String?) {
            if (StringUtils.hasText(value)) {
                builder.append(SEPARATOR).append(value)
            }
        }

        /**
         * 构建出来一个ServiceBeanBuilder
         *
         * @param attributes 注解属性
         * @param serviceInterface Dubbo服务接口
         * @param environment Environment
         * @return 创建出来ServiceBeanBuilder
         */
        @JvmStatic
        fun create(
            attributes: AnnotationAttributes,
            serviceInterface: Class<*>,
            environment: Environment
        ): ServiceBeanNameBuilder {
            return ServiceBeanNameBuilder(attributes, serviceInterface.name, environment)
        }
    }
}