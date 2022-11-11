package com.wanna.spring.dubbo.config

import com.wanna.spring.dubbo.rpc.service.GenericService


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
     * path
     */
    var path: String? = null

    open fun getReference(): T = this.ref ?: throw IllegalStateException("ref不能为null")

    /**
     * 暴露当前Dubbo服务
     */
    abstract fun export()

    /**
     * 获取唯一的ServiceName
     *
     * @return serviceName
     */
    open fun getUniqueServiceName(): String {
        return "$interfaceClass:$group:$version"  // TODO
    }

    /**
     * 获取Dubbo服务的接口
     *
     * @return 要去进行暴露的Dubbo服务的接口Class
     */
    open fun getInterfaceClazz(): Class<*> {

        // 如果初始化了interfaceClass，那么直接使用
        if (this.interfaceClass != null) {
            return this.interfaceClass!!
        }

        // 如果ref是GenericService，那么直接返回
        if (this.ref is GenericService) {
            return GenericService::class.java
        }
        try {
            if (this.interfaceName != null) {
                this.interfaceClass = Class.forName(interfaceName, true, Thread.currentThread().contextClassLoader)
            }
        } catch (ex: ClassNotFoundException) {
            throw IllegalStateException(ex.message, ex)
        }
        return this.interfaceClass ?: throw IllegalStateException("无法获取到ServiceConfig的interfaceClass")
    }
}