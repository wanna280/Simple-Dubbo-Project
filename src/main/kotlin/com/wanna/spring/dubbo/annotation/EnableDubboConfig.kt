package com.wanna.spring.dubbo.annotation

import com.wanna.framework.context.annotation.Import

/**
 * 开启Dubbo的配置功能，支持在Spring的配置文件(Environment)当中去寻找"dubbo."开头的配置属性，
 * Dubbo可以支持自动将该配置信息转换成为DubboConfig的Spring的Bean，并注册到BeanFactory当中
 *
 * * 1.如果multiple=false，只会开启"dubbo.registry"这样的配置信息的解析(仅仅支持单个SpringBean的注册)
 * * 2.如果multiple=true，那么开启"dubbo.registry"之外，还会开启"dubbo.registries"这样的配置的解析(支持多个SpringBean的注册)
 *
 * @param multiple 是否支持去注册多个DubboConfig的SpringBean到SpringBeanFactory当中？默认为true
 */
@Target(AnnotationTarget.CLASS)
@Import([DubboConfigConfigurationRegistrar::class])
annotation class EnableDubboConfig(
    val multiple: Boolean = true
)
