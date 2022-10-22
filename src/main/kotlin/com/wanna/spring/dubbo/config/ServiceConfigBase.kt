package com.wanna.spring.dubbo.config


/**
 * 提供Dubbo的服务暴露的相关配置
 *
 * @see ServiceConfig
 */
abstract class ServiceConfigBase<T> : AbstractServiceConfig() {
    /**
     * interfaceName
     */
    var interfaceName: String? = null
        protected set

    /**
     * interfaceClass
     */
    var interfaceClass: Class<*>? = null
        protected set

    /**
     * 该接口对应的实现类的应用ref，也就是@DubboService的Bean的实例对象
     */
    var ref: T? = null
        protected set

    /**
     * 暴露
     */
    abstract fun export()
}