package com.wanna.spring.dubbo.remoting.transport

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.Channel
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.Constants
import com.wanna.spring.dubbo.remoting.Endpoint

/**
 * 抽象的Peer的实现
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/24
 *
 * @param url url
 * @param handler handler
 */
abstract class AbstractPeer(private val url: URL, private val handler: ChannelHandler) : ChannelHandler, Endpoint {

    /**
     * 连接是否已经关闭？
     */
    @Volatile
    private var closed = false

    /**
     * send message发送消息
     *
     * @param message 需要发送的消息
     */
    override fun send(message: Any) = send(message, url.getParameter(Constants.HOST_KEY, false))

    /**
     * 获取URL
     *
     * @return URL
     */
    override fun getUrl() = this.url

    /**
     * 检查是否已经关闭？
     *
     * @return 如果已经关闭return true；否则return false
     */
    open fun isClosed(): Boolean = this.closed

    override fun connected(channel: Channel) {
        handler.connected(channel)
    }

    override fun disconnected(channel: Channel) {
        handler.disconnected(channel)
    }

    override fun sent(channel: Channel, message: Any?) {
        handler.sent(channel, message)
    }

    override fun received(channel: Channel, message: Any?) {
        handler.received(channel, message)
    }

    override fun caught(channel: Channel) {
        handler.connected(channel)
    }
}