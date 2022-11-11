package com.wanna.spring.dubbo.rpc.model

import com.wanna.spring.dubbo.common.config.Environment
import com.wanna.spring.dubbo.config.context.ConfigManager

object ApplicationModel {

    /**
     * Dubbo的ConfigManager
     */
    private val configManager = ConfigManager()

    /**
     * Dubbo的Environment
     */
    private val environment = Environment()

    /**
     * Dubbo服务的注册中心
     */
    private val serviceRepository = ServiceRepository()

    @JvmStatic
    fun getConfigManager(): ConfigManager {
        return configManager
    }

    @JvmStatic
    fun getEnvironment(): Environment {
        return this.environment
    }

    @JvmStatic
    fun getServiceRepository(): ServiceRepository {
        return this.serviceRepository
    }
}