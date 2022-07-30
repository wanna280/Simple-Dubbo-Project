package com.wanna.spring.dubbo.annotation

import com.wanna.framework.context.annotation.Import
import kotlin.reflect.KClass

/**
 * 开启Dubbo配置类的绑定的功能支持，可以将Spring的Environment当中的信息，绑定到对应的Dubbo配置类的属性当中；
 * 只要你配置了"prefix"对应的配置信息，Dubbo就能自动将信息转换为Dubbo的配置类，并注册到SpringBeanFactory当中
 *
 * @see EnableConfigurationBeanBindings
 *
 * @param prefix 要绑定配置文件当中的哪个前缀的属性？
 * @param type 要绑定到哪个Dubbo配置类上？
 * @param multiple 要绑定的是否是多个SpringBean
 */
@Repeatable
@Import([ConfigurationBeanBindingRegistrar::class])
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
annotation class EnableConfigurationBeanBinding(
    val prefix: String,
    val type: KClass<*>,
    val multiple: Boolean = false
)
