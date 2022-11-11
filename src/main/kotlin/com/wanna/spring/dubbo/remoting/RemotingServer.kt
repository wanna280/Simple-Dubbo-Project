package com.wanna.spring.dubbo.remoting

import java.net.InetSocketAddress
import com.wanna.spring.dubbo.remoting.netty.NettyServer
import com.wanna.spring.dubbo.remoting.netty.NettyClient

/**
 * RemotingServer
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 *
 * @see NettyServer
 * @see NettyClient
 */
interface RemotingServer : Endpoint {

    /**
     * 获取远程Server的Channels
     *
     * @return Channels
     */
    fun getChannels(): Collection<Channel>

    /**
     * 根据"ip:port"去获取到对应的Channel
     *
     * @param address address
     * @return Channel(获取不到Channel的话，return null)
     */
    fun getChannel(address: InetSocketAddress): Channel?
}