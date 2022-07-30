package com.wanna.spring.dubbo.util

import com.wanna.framework.core.environment.EnumerablePropertySource
import com.wanna.framework.core.environment.PropertyResolver
import com.wanna.framework.core.environment.PropertySource
import com.wanna.framework.core.environment.PropertySources

/**
 * PropertySource的工具类
 */
object PropertySourcesUtils {

    private val EMPTY_STRING_ARRAY = emptyArray<String>()

    /**
     * 从指定的PropertySource列表当中，去解析到所有的指定的prefix下的propertyValue
     *
     * @param propertyResolver 属性的解析器(提供占位符解析工作)
     * @param propertySources 属性来源列表
     * @param prefix 要解析的属性来源
     * @return 统计完成的所有的以指定的前缀开头的所有属性值(key已经去掉了公共前缀)
     */
    @JvmStatic
    fun getSubProperties(
        propertySources: PropertySources,
        propertyResolver: PropertyResolver,
        prefix: String
    ): Map<String, Any> {
        // 如果必要的话，需要添加一个后缀"."，方便去切取subName
        val prefixToUse = if (prefix.endsWith(".")) prefix else "$prefix."
        val subProperties = LinkedHashMap<String, Any>()
        propertySources.forEach { propertySource ->
            getPropertyNames(propertySource).forEach { name ->
                if (name.startsWith(prefixToUse)) {
                    val subName = name.substring(prefixToUse.length)
                    if (!subProperties.containsKey(subName)) {
                        var propertyValue = propertySource.getProperty(name)
                        // 如果是字符串的话，支持去进行占位符解析
                        if (propertyValue != null && propertyValue is String) {
                            propertyValue = propertyResolver.resolvePlaceholders(propertyValue)
                        }
                        subProperties[subName] = propertyValue!!
                    }
                }
            }
        }
        return subProperties
    }

    /**
     * 获取一个属性源当中的所有的属性的propertyName
     *
     * @param propertySource 要使用的PropertySource
     * @return 解析完成的propertyName列表(如果它不可以去进行枚举，那么return empty Array)
     */
    @JvmStatic
    fun getPropertyNames(propertySource: PropertySource<*>): Array<String> =
        if (propertySource is EnumerablePropertySource<*>) propertySource.getPropertyNames() else EMPTY_STRING_ARRAY
}