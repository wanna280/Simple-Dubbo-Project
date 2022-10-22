package com.wanna.spring.dubbo.config

/**
 * 提供Dubbo的Reference的配置的基础类
 *
 * @see ReferenceConfig
 */
abstract class ReferenceConfigBase<T> : AbstractReferenceConfig() {
    /**
     * interfaceName
     */
    var interfaceName: String? = null

    /**
     * interfaceClass
     */
    var interfaceClass: Class<*>? = null

    /**
     * 引用的接口的实现
     */
    var ref: T? = null
}