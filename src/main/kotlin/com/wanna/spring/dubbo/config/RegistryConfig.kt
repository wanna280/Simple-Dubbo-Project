package com.wanna.spring.dubbo.config

/**
 * Dubbo对于注册中心的配置信息的封装，一个DubboService可能会注册到多个Registry当中，
 * 而RegistryConfig就维护的是，其中一个Registry的配置信息
 *
 * @see ServiceConfig
 * @see AbstractServiceConfig.registries
 */
open class RegistryConfig : AbstractConfig() {

    // 注册中心的地址
    var address: String? = null

    // 注册中心的端口
    var port: Int = -1

    // 登录注册中心的用户名
    var username: String? = null

    // 登录注册中心的密码
    var password: String? = null

    // 连接到注册中心要使用的协议
    var protocol: String? = null
}