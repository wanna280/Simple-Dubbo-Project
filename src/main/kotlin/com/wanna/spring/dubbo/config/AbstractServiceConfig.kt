package com.wanna.spring.dubbo.config

/**
 * 一个DubboService的具体信息
 *
 * * 1.protocol，描述了要以何种形式去暴露当前服务的具体信息，比如以HTTP在8080端口暴露
 * * 2.application，描述了当前的DubboApplication的name
 * * 3.registry，描述了要把当前DubboService注册到哪个注册中心当中的具体信息(协议/地址等)
 */
abstract class AbstractServiceConfig : AbstractInterfaceConfig() {

    // 当前Service要使用哪些协议去暴露服务？
    var protocols: MutableList<ProtocolConfig> = ArrayList()

    // 当前Service的ApplicationConfig
    var application: ApplicationConfig? = null

    // 当前DubboService服务要注册到哪些注册中心当中去
    var registries: MutableList<RegistryConfig> = ArrayList()

    open fun setRegistry(registryConfig: RegistryConfig) {
        this.registries = arrayListOf(registryConfig)
    }

    open fun setProtocol(protocolConfig: ProtocolConfig) {
        this.protocols = arrayListOf(protocolConfig)
    }
}