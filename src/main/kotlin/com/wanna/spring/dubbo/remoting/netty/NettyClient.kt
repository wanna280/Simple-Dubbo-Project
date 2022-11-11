package com.wanna.spring.dubbo.remoting.netty

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.Channel
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.RemotingServer
import java.net.InetSocketAddress

/**
 * NettyClient
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class NettyClient(private val url: URL, private val handler: ChannelHandler) : RemotingServer {
    override fun getChannels(): Collection<Channel> {
        TODO("Not yet implemented")
    }

    override fun send(message: Any) {
        TODO("Not yet implemented")
    }

    override fun send(message: Any, sent: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getChannel(address: InetSocketAddress): Channel? {
        TODO("Not yet implemented")
    }

    override fun getUrl() = this.url

    override fun getLocalAddress(): InetSocketAddress = url.toInetSocketAddress()

    override fun close() {
        TODO("Not yet implemented")
    }


}