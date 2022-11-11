package com.wanna.spring.dubbo.remoting.exchange

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.Channel
import com.wanna.spring.dubbo.remoting.RemotingServer
import java.net.InetSocketAddress

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class HeaderExchangeServer(private val server: RemotingServer) : ExchangeServer {

    override fun getChannels(): Collection<Channel> = server.getChannels()

    override fun getChannel(address: InetSocketAddress): Channel? = server.getChannel(address)

    override fun getExchangeChannels(): Collection<ExchangeChannel> {
        TODO("Not yet implemented")
    }

    override fun send(message: Any) {
        TODO("Not yet implemented")
    }

    override fun send(message: Any, sent: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getExchangeChannel(address: InetSocketAddress): ExchangeChannel? {
        TODO("Not yet implemented")
    }

    override fun getUrl(): URL = server.getUrl()

    override fun close() = server.close()

    override fun getLocalAddress(): InetSocketAddress = server.getLocalAddress()
}