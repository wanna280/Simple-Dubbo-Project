package com.wanna.spring.dubbo.config.context

import com.wanna.spring.dubbo.config.AbstractConfig
import com.wanna.spring.dubbo.config.ReferenceConfigBase
import com.wanna.spring.dubbo.config.RegistryConfig
import com.wanna.spring.dubbo.config.ServiceConfigBase

/**
 * Dubbo的配置的管理器，负责去维护整个Dubbo当中的各个配置，
 * 例如RegistryConfig/ServiceConfig/ApplicationConfig/ProtocolConfig等各种类型
 *
 * @see AbstractConfig
 */
@Suppress("UNCHECKED_CAST")
open class ConfigManager {

    // Dubbo配置的缓存 tagName->(config.id-->Config)
    private val configsCache: MutableMap<String, MutableMap<String, AbstractConfig>> = HashMap()

    /**
     * 将给定的config添加到ConfigManager的缓存当中
     *
     * @param config 你想要添加的config
     */
    open fun addToConfig(config: AbstractConfig) {
        val tagName = AbstractConfig.getTagName(config::class.java)
        configsCache.putIfAbsent(tagName, LinkedHashMap())
        configsCache[tagName]!!.putIfAbsent(config.id, config)
    }

    /**
     * 获取ConfigManager当中已经注册的ServiceConfig列表
     *
     * @return ConfigManager当中已经注册的Service列表
     */
    open fun getServices(): List<ServiceConfigBase<*>> {
        val serviceMap = configsCache[AbstractConfig.getTagName(ServiceConfigBase::class.java)] ?: return emptyList()
        return ArrayList(serviceMap.values) as List<ServiceConfigBase<*>>
    }

    /**
     * 获取ConfigManager当中已经注册的ReferenceConfig的列表
     *
     * @return ConfigManager当中已经注册的References列表
     */
    open fun getReferences(): List<ReferenceConfigBase<*>> {
        val referencesMap =
            configsCache[AbstractConfig.getTagName(ReferenceConfigBase::class.java)] ?: return emptyList()
        return ArrayList(referencesMap.values) as List<ReferenceConfigBase<*>>
    }

    /**
     * 获取当前ConfigManager当中已经注册的RegistryConfig的列表
     *
     * @return ConfigManager当中已经注册的Registry列表
     */
    open fun getRegistries(): List<RegistryConfig> {
        val registryMap = configsCache[AbstractConfig.getTagName(RegistryConfig::class.java)] ?: return emptyList()
        return ArrayList(registryMap.values) as List<RegistryConfig>
    }
}