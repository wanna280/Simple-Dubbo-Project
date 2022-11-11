package com.wanna.spring.dubbo.rpc

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.rpc.protocol.ProtocolServer

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface Protocol {

    /**
     * 获取当前Protocol的默认端口号
     *
     * @return 默认端口号(比如dubbo的"20880")
     */
    fun getDefaultPort(): Int

    /**
     * 暴露一个Dubbo服务
     *
     * @param invoker invoker
     * @return Exporter
     */
    fun <T> export(invoker: Invoker<T>): Exporter<T>

    /**
     * 引用一个Dubbo服务
     *
     * @param type type
     * @param url url
     * @return Invoker
     */
    fun <T> refer(type: Class<T>, url: URL): Invoker<T>

    /**
     * 获取当前Protocol当中已经暴露的ProtocolServer
     *
     * @return ProtocolServers
     */
    fun getServers(): List<ProtocolServer> = emptyList()

    fun destroy()


}