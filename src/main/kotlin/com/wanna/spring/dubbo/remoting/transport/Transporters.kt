package com.wanna.spring.dubbo.remoting.transport

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.RemotingServer

/**
 * 提供Transporter的操作的工具类
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
object Transporters {

    /**
     * Transporter
     */
    private val transporter = NettyTransporter()

    /**
     * 绑定本地端口，并指定处理请求的ChannelHandler
     *
     * @param url URL
     * @param handlers 需要使用的ChannelHandler
     * @return 创建出来的RemotingServer
     */
    @JvmStatic
    fun bind(url: URL, vararg handlers: ChannelHandler): RemotingServer {
        if (handlers.isEmpty()) {
            throw IllegalStateException("ChannelHandler不能为空")
        }
        val handler = if (handlers.size == 1) handlers[0] else ChannelHandlerDispatcher(listOf(*handlers))
        return transporter.bind(url, handler)
    }

    /**
     * 指定处理请求的ChannelHandler去连接到远程服务器
     *
     * @param url URL
     * @param handlers 需要使用的ChannelHandler
     * @return 创建出来的RemotingServer
     */
    @JvmStatic
    fun connect(url: URL, vararg handlers: ChannelHandler): RemotingServer {
        if (handlers.isEmpty()) {
            throw IllegalStateException("ChannelHandler不能为空")
        }
        val handler = if (handlers.size == 1) handlers[0] else ChannelHandlerDispatcher(listOf(*handlers))
        return transporter.connect(url, handler)
    }

    /**
     * 获取Transporter
     *
     * @return Transporter
     */
    @JvmStatic
    fun getTransporter(): Transporter {
        return this.transporter
    }
}