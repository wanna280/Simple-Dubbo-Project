package com.wanna.spring.dubbo.rpc.model

import com.wanna.spring.dubbo.config.ServiceConfig
import com.wanna.spring.dubbo.config.ServiceConfigBase
import java.util.concurrent.ConcurrentHashMap

/**
 * Dubbo服务的仓库
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class ServiceRepository {

    /**
     * Dubbo服务列表
     */
    private val services = ConcurrentHashMap<String, ServiceDescriptor>()

    /**
     * 消费者列表
     */
    private val consumers = ConcurrentHashMap<String, ConsumerModel>()

    /**
     * 生产者列表
     */
    private val providers = ConcurrentHashMap<String, ProviderModel>()

    /**
     * 没有Group的Provider列表
     */
    private val providerWithoutGroups = ConcurrentHashMap<String, ProviderModel>()

    /**
     * 注册一个Dubbo服务到当前仓库当中
     *
     * @param interfaceClazz Dubbo服务接口
     */
    open fun registerService(interfaceClazz: Class<*>): ServiceDescriptor {
        return services.computeIfAbsent(interfaceClazz.name) {
            ServiceDescriptor(interfaceClazz)
        }
    }

    /**
     * 注册一个Provider到当前的仓库当中
     */
    open fun registerProvider(
        serviceKey: String,
        serviceInstance: Any,
        serviceConfig: ServiceConfigBase<*>,
        serviceMetadata: ServiceMetadata
    ) {

    }

    open fun registerConsumer() {

    }
}