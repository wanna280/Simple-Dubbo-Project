package com.wanna.spring.dubbo.annotation

import com.wanna.framework.context.annotation.Import

/**
 * 组合@EnableConfigurationBeanBinding注解的数组，从而去支持同时去对多个Dubbo配置类去进行绑定；
 *
 * ## Note
 * 这里似乎可以利用JDK/Kotlin的@Repeatable注解去进行完善，可以有更好的效果
 *
 * @see Repeatable
 * @param value 要去进行绑定的EnableConfigurationBeanBinding
 */
@Import([ConfigurationBeanBindingsRegistrar::class])
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class EnableConfigurationBeanBindings(
    vararg val value: EnableConfigurationBeanBinding = []
)
