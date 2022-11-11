package com.wanna.spring.dubbo.remoting.exchange

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.Channel
import java.net.InetSocketAddress

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
class HeaderExchangeClient : ExchangeClient, Channel {
    override fun getUrl(): URL {
        TODO("Not yet implemented")
    }

    override fun getLocalAddress(): InetSocketAddress {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun isConnected(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRemoteAddress(): InetSocketAddress {
        TODO("Not yet implemented")
    }

    override fun send(message: Any) {
        TODO("Not yet implemented")
    }

    override fun send(message: Any, sent: Boolean) {
        TODO("Not yet implemented")
    }
}