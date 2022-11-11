package com.wanna.spring.dubbo.registry.integration

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.rpc.Exporter
import com.wanna.spring.dubbo.rpc.Invoker
import com.wanna.spring.dubbo.rpc.Protocol

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/25
 */
open class RegistryProtocol : Protocol {

    override fun getDefaultPort(): Int {
        TODO("Not yet implemented")
    }

    override fun <T> export(invoker: Invoker<T>): Exporter<T> {
        TODO("Not yet implemented")
    }

    override fun <T> refer(type: Class<T>, url: URL): Invoker<T> {
        TODO("Not yet implemented")
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }

    protected open fun getRegistryUrl(invoker: Invoker<*>): URL {
        return invoker.getUrl()
    }
}