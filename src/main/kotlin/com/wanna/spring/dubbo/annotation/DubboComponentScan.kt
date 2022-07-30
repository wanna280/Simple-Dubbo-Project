package com.wanna.spring.dubbo.annotation

import com.wanna.framework.context.annotation.Import
import kotlin.reflect.KClass

/**
 * Dubbo的组件扫描，支持去扫描指定的包下的@DubboService的组件到Spring容器当中
 *
 * @param value 要扫描的包的列表(同basePackages)
 * @param basePackages 要扫描的包(同value)
 * @param basePackageClasses 以类的形式去指定要扫描的包
 *
 * @see DubboService
 * @see DubboComponentScanRegistrar
 */
@Import([DubboComponentScanRegistrar::class])
@Target(AnnotationTarget.CLASS)
annotation class DubboComponentScan(
    val value: Array<String> = [],
    val basePackages: Array<String> = [],
    val basePackageClasses: Array<KClass<*>> = []
)
