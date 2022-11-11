package com.wanna.spring.dubbo.remoting.transport

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.RemotingServer
import com.wanna.spring.dubbo.remoting.netty.NettyClient
import com.wanna.spring.dubbo.remoting.netty.NettyServer

/**
 * 基于Netty实现的Transporter，不管是Server还是Client都采用Netty去进行实现；
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class NettyTransporter : Transporter {

    /**
     * 创建一个NettyServer，并绑定一个本地端口
     *
     * @param url url
     * @param handler ChannelHandler
     * @return NettyServer
     */
    override fun bind(url: URL, handler: ChannelHandler): RemotingServer = NettyServer(url, handler)

    /**
     * 创建一个NettyClient，连接到远程服务器
     *
     * @param url url
     * @param handler ChannelHandler
     * @return NettyClient
     */
    override fun connect(url: URL, handler: ChannelHandler): RemotingServer = NettyClient(url, handler)
}