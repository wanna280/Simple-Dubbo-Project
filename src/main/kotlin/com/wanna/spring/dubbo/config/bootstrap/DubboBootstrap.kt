package com.wanna.spring.dubbo.config.bootstrap

import com.wanna.spring.dubbo.config.ServiceConfig
import com.wanna.spring.dubbo.rpc.model.ApplicationModel

/**
 * Dubbo的Bootstrap初始化类，需要将所有的服务暴露到注册中心当中
 */
open class DubboBootstrap {

    private val configManager = ApplicationModel.getConfigManager()

    open fun start() {
        // 1.暴露Dubbo服务
        exportServices()
    }

    /**
     * 遍历已经注册到ConfigManager当中的所有ServiceConfig去完成暴露；
     * 将该DubboService的具体信息，尝试去暴露到注册中心当中
     *
     * @see ServiceConfig.export
     */
    private fun exportServices() {
        configManager.getServices().forEach {
            if (it is ServiceConfig<*>) {
                it.export()
            }
        }
    }
}