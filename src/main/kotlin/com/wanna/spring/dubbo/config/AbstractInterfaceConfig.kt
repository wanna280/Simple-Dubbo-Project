package com.wanna.spring.dubbo.config

/**
 * 它的子类主要分为两派
 * ```
 *       ---AbstractReferenceConfig
 *                --ServiceConfig
 *                --ProviderConfig
 *
 *       ---AbstractServiceConfig
 *                --ReferenceConfig
 *                --ConsumerConfig
 * ```
 */
abstract class AbstractInterfaceConfig : AbstractMethodConfig() {

    /**
     * 当前Service要使用哪些协议去暴露服务？
     */
    var protocols: MutableList<ProtocolConfig> = ArrayList()

    /**
     * ApplicationConfig
     */
    var application: ApplicationConfig? = null

    /**
     * 要注册到哪些注册中心当中？
     */
    var registries: MutableList<RegistryConfig> = ArrayList()

    /**
     * Consumer配置
     */
    var consumer: ConsumerConfig? = null

}