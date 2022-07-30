package com.wanna.spring.dubbo.annotation

import com.wanna.spring.dubbo.config.*

open class DubboConfigConfiguration {

    @EnableConfigurationBeanBindings(
        EnableConfigurationBeanBinding(
            prefix = "dubbo.registries",
            type = RegistryConfig::class,
            multiple = true
        ),
        EnableConfigurationBeanBinding(
            prefix = "dubbo.protocols",
            type = ProtocolConfig::class,
            multiple = true
        ),
        EnableConfigurationBeanBinding(
            prefix = "dubbo.applications",
            type = ApplicationConfig::class,
            multiple = true
        ),
        EnableConfigurationBeanBinding(
            prefix = "dubbo.providers",
            type = ProviderConfig::class,
            multiple = true
        ),
        EnableConfigurationBeanBinding(
            prefix = "dubbo.consumers",
            type = ConsumerConfig::class,
            multiple = true
        ),
    )
    open class Multiple

    @EnableConfigurationBeanBindings(
        EnableConfigurationBeanBinding(
            prefix = "dubbo.registry",
            type = RegistryConfig::class,
            multiple = false
        ),
        EnableConfigurationBeanBinding(
            prefix = "dubbo.protocol",
            type = ProtocolConfig::class,
            multiple = false
        ),
        EnableConfigurationBeanBinding(
            prefix = "dubbo.application",
            type = ApplicationConfig::class,
            multiple = false
        ),
        EnableConfigurationBeanBinding(
            prefix = "dubbo.provider",
            type = ProviderConfig::class,
            multiple = false
        ),
        EnableConfigurationBeanBinding(
            prefix = "dubbo.consumer",
            type = ConsumerConfig::class,
            multiple = false
        ),
    )
    open class Single
}