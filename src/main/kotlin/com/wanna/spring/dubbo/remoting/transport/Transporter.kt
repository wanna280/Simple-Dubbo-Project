package com.wanna.spring.dubbo.remoting.transport

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.RemotingException
import com.wanna.spring.dubbo.remoting.RemotingServer
import kotlin.jvm.Throws

/**
 * Transporter，提供数据的传输功能
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface Transporter {

    /**
     * 绑定一个端口，并获取到RemotingServer
     *
     * @param url url
     * @param handler ChannelHandler
     * @return RemotingServer(比如NettyServer)
     */
    @Throws(RemotingException::class)
    fun bind(url: URL, handler: ChannelHandler): RemotingServer

    /**
     * 连接到远程服务器，并获取到RemotingServer
     *
     * @param url url
     * @param handler ChannelHandler
     * @return RemotingServer(比如NettyClient)
     */
    @Throws(RemotingException::class)
    fun connect(url: URL, handler: ChannelHandler): RemotingServer
}