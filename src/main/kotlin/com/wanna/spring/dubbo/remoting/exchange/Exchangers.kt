package com.wanna.spring.dubbo.remoting.exchange

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.exchange.support.Replier

/**
 * 构建Exchanger的工具类
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
object Exchangers {
    @JvmStatic
    fun bind(url: URL, handler: ChannelHandler, replier: Replier<*>): ExchangeServer {
        return getExchanger().bind(url, handler)
    }

    @JvmStatic
    fun bind(url: URL, handler: ChannelHandler): ExchangeServer {
        return getExchanger().bind(url, handler)
    }

    @JvmStatic
    fun getExchanger(): Exchanger = HeaderExchanger()
}