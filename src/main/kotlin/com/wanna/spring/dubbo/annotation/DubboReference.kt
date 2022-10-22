package com.wanna.spring.dubbo.annotation

import kotlin.reflect.KClass

/**
 * 通过引用的方式，去获取到要去进行注入的DubboService的实例对象；可以标注在接口/类上，去提供自动注入，使用方式和Autowired类似；
 * 在客户端使用，去注入DubboService，去完成Dubbo的RPC的远程调用
 *
 * @param id id(ReferenceBean的beanName)
 * @param interfaceClass 要去引用的Service的接口
 * @param interfaceClassName 要去引用的Service的className
 * @param injvm 是否要引用本地的Dubbo服务？
 * @param group 引用哪个组的Dubbo服务？
 * @param version 引用的服务的版本
 * @param registry 要从哪个注册中心当中去引用？
 * @param application ApplicationConfig
 * @param consumer consumer
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
annotation class DubboReference(
    val id: String = "",
    val interfaceClass: KClass<*> = Void::class,
    val interfaceClassName: String = "",
    val injvm: Boolean = false,
    val group: String = "",
    val version: String = "",
    val registry: Array<String> = [],
    val application: String = "",
    val consumer: String = ""
)
