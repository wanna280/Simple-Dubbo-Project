package com.wanna.spring.dubbo.boot.autoconfigure

import com.wanna.framework.beans.factory.annotation.Qualifier
import com.wanna.framework.context.annotation.Bean
import com.wanna.framework.context.annotation.Configuration
import com.wanna.framework.core.environment.Environment
import com.wanna.framework.util.StringUtils

@Configuration(proxyBeanMethods = false)
open class DubboRelaxedBindingAutoConfiguration {

    /**
     * 注册Dubbo要扫描的包的列表作为一个Bean放入到SpringBeanFactory当中
     *
     * @return Dubbo要去进行扫描的包的列表
     */
    @Bean
    @Qualifier("dubboBasePackages")
    open fun dubboBasePackages(environment: Environment): DubboBasePackages {
        val scanPackages = environment.getProperty("dubbo.scan.base-packages")
        return if (StringUtils.hasText(scanPackages)) DubboBasePackages(setOf(scanPackages.toString()))
        else DubboBasePackages()
    }

    /**
     * Dubbo要扫描的包的列表
     *
     * @param packages 要扫描的包的列表
     */
    data class DubboBasePackages(val packages: Set<String> = emptySet())
}