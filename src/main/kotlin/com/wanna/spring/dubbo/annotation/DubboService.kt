package com.wanna.spring.dubbo.annotation

import kotlin.reflect.KClass

/**
 * 标识这是一个DubboService，提供Dubbo的服务暴露功能
 *
 * @param id id(ServiceBean的beanName)
 * @param interfaceClass 要注册的DubboService的接口
 * @param interfaceClassName 要注册的DubboService的接口名称
 * @param application SpringBeanFactory当中ApplicationConfig的beanName
 * @param protocol 该DubboService要以哪些协议去进行注册？指定Spring beanName
 * @param registry 该DubboService要注册到哪些注册中心当中？指定Spring beanName
 * @param group 要将服务暴露到哪个组当中？
 * @param version Dubbo服务的版本
 *
 * @see ServiceClassPostProcessor
 * @see DubboServiceBeanDefinitionScanner
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
annotation class DubboService(
    val id: String = "",
    val interfaceClass: KClass<*> = Void::class,
    val interfaceClassName: String = "",
    val group: String = "",
    val version: String = "",
    val application: String = "",
    val protocol: Array<String> = [],
    val registry: Array<String> = []
)
