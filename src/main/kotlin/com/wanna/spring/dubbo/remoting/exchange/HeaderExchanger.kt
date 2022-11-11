package com.wanna.spring.dubbo.remoting.exchange

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.transport.Transporters

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class HeaderExchanger : Exchanger {

    override fun bind(url: URL, channelHandler: ChannelHandler): ExchangeServer {
        return HeaderExchangeServer(Transporters.bind(url, channelHandler))
    }

    override fun connect(url: URL, channelHandler: ChannelHandler): ExchangeClient {
        TODO("")
    }
}