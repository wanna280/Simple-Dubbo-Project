package com.wanna.spring.dubbo.annotation

import kotlin.reflect.KClass

/**
 * 标识这是一个DubboService
 *
 * @param interfaceClass 要注册的DubboService的接口
 * @param interfaceClassName 要注册的DubboService的接口名称
 * @param application SpringBeanFactory当中ApplicationConfig的beanName
 * @param protocol 该DubboService要以哪些协议去进行注册？指定Spring beanName
 * @param registry 该DubboService要注册到哪些注册中心当中？指定Spring beanName
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
annotation class DubboService(
    val interfaceClass: KClass<*> = Void::class,
    val interfaceClassName: String = "",
    val application: String = "",
    val protocol:Array<String> = [],
    val registry:Array<String> = []
)
