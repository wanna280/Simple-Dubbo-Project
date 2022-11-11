package com.wanna.spring.dubbo.remoting.exchange

import com.wanna.spring.dubbo.remoting.ChannelHandler
import java.util.concurrent.CompletableFuture

/**
 * 用于去进行数据交换的ChannelHandler
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 * @see ChannelHandler
 */
interface ExchangeHandler : ChannelHandler {

    /**
     * 给对方回复消息
     *
     * @param channel Channel
     * @param request request
     */
    fun reply(channel: ExchangeChannel, request: Any): CompletableFuture<Any?>? {
        return null
    }
}