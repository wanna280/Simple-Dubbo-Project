package com.wanna.spring.dubbo.remoting.exchange

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.ChannelHandler

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface Exchanger {
    fun bind(url: URL, channelHandler: ChannelHandler): ExchangeServer

    fun connect(url: URL, channelHandler: ChannelHandler): ExchangeClient
}