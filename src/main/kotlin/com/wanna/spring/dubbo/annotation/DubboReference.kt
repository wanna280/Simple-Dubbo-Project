package com.wanna.spring.dubbo.annotation

import kotlin.reflect.KClass

/**
 * 通过引用的方式，去获取到要去进行注入的DubboService的实例对象；可以标注在接口/类上，去提供自动注入，使用方式和Autowired类似；
 * 在客户端使用，去注入DubboService，去完成Dubbo的RPC的远程调用
 *
 * @param interfaceClass 要去引用的Service的接口
 * @param interfaceClassName 要去引用的Service的className
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
annotation class DubboReference(
    val interfaceClass: KClass<*> = Void::class,
    val interfaceClassName: String = ""
)
