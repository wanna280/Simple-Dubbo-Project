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

    // interfaceName
    protected var interfaceName: String? = null
}