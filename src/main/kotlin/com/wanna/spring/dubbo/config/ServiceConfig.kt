package com.wanna.spring.dubbo.config

import com.alibaba.nacos.api.NacosFactory

/**
 * 维护的是注册到Dubbo的一个Service的Bean，一个ServiceBean对应一个@DubboService的Bean
 */
open class ServiceConfig<T> : ServiceConfigBase<T>() {

    @Volatile
    private var exported: Boolean = false

    open fun isExported(): Boolean = this.exported


    /**
     * 暴露一个DubboService到注册中心当中
     */
    open fun exported() {
        this.exported = true
    }

    override fun export() {
        val namingService = NacosFactory.createNamingService("127.0.0.1:8848")
        namingService.registerInstance(interfaceClass?.name ?: "wanna", "127.0.0.1", 9789)
        exported()
    }
}