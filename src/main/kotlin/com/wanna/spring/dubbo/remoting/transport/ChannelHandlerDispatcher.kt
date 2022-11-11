package com.wanna.spring.dubbo.remoting.transport

import com.wanna.spring.dubbo.remoting.Channel
import com.wanna.spring.dubbo.remoting.ChannelHandler

/**
 * ChannelHandlerDispatcher，提供对于多个ChannelHandler的组合
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class ChannelHandlerDispatcher(private val handlers: Collection<ChannelHandler>) : ChannelHandler {
    override fun connected(channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun disconnected(channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun sent(channel: Channel, message: Any?) {
        TODO("Not yet implemented")
    }

    override fun received(channel: Channel, message: Any?) {
        TODO("Not yet implemented")
    }

    override fun caught(channel: Channel) {
        TODO("Not yet implemented")
    }
}