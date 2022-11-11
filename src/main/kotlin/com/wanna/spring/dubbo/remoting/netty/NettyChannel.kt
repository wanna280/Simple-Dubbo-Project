package com.wanna.spring.dubbo.remoting.netty

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.Constants
import com.wanna.spring.dubbo.remoting.RemotingException
import com.wanna.spring.dubbo.remoting.transport.AbstractChannel
import io.netty.channel.Channel
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * NettyChannel，负责将Netty的Channel转换成为Dubbo的NettyChannel对象
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 *
 * @param channel 来自Netty本身的Channel对象
 * @param url url
 * @param handler Dubbo的ChannelHandler，负责去真正地处理Channel的事件，当Channel的事件发生时会自动回调handler
 */
open class NettyChannel(private val channel: Channel, url: URL, handler: ChannelHandler) :
    AbstractChannel(url, handler) {

    /**
     * 当前Channel是否还活跃？
     */
    private val active = AtomicBoolean(false)

    /**
     * Attributes
     */
    private val attributes = ConcurrentHashMap<String, Any>()

    open fun markActive(active: Boolean) {
        this.active.set(active)
    }

    override fun isConnected() = active.get() && !isClosed()

    /**
     * 对于Channel的发送消息来说，我们直接使用Netty原生的Channel去writeAndFlush即可
     *
     * @param message message
     * @param sent sent
     */
    override fun send(message: Any, sent: Boolean) {
        // invoke super
        super.send(message, sent)

        var success = true
        var timeout = 0
        try {
            // writeAndFlush
            val channelFuture = channel.writeAndFlush(message)
            if (sent) {
                timeout = getUrl().getParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT)
                success = channelFuture.await(timeout.toLong())
            }
            val cause = channelFuture.cause()
            if (cause != null) {
                throw cause
            }
        } catch (ex: Throwable) {
            throw RemotingException(ex)
        }
        if (!success) {
            throw RemotingException("")
        }
    }

    /**
     * 检查当前Channel是否还处于活跃
     *
     * @return 如果还活跃return true；否则return false
     */
    open fun isActive(): Boolean = active.get()

    /**
     * 获取本地地址，我们直接使用Netty的Channel的LocalAddress去进行返回
     *
     * @return 本地地址
     */
    override fun getLocalAddress(): InetSocketAddress = channel.localAddress() as InetSocketAddress

    /**
     * 获取远程地址，我们直接使用Netty的RemoteAddress去进行返回
     *
     * @return 远程的地址
     */
    override fun getRemoteAddress(): InetSocketAddress = channel.remoteAddress() as InetSocketAddress

    override fun close() {

    }

    companion object {

        /**
         * ChannelMap(Key-原生的NettyChannel，Value-Dubbo封装的NettyChannel)
         */
        @JvmStatic
        private val CHANNEL_MAP = ConcurrentHashMap<Channel, NettyChannel>()

        /**
         * 获取/添加一个Channel
         *
         * @param channel 需要去进行获取的来自Netty的Channel
         * @param url URL
         * @param handler Dubbo ChannelHandler
         * @return Dubbo封装的NettyChannel
         */
        @JvmStatic
        fun getOrAddChannel(
            channel: Channel,
            url: URL,
            handler: ChannelHandler
        ): NettyChannel {
            var ret = CHANNEL_MAP[channel]
            if (ret == null) {
                val nettyChannel = NettyChannel(channel, url, handler)
                if (channel.isActive) {
                    ret = CHANNEL_MAP.putIfAbsent(channel, nettyChannel)
                    nettyChannel.markActive(true)
                }
                ret = ret ?: nettyChannel
            }
            return ret
        }

        /**
         * 移除一个Channel
         *
         * @param channel 需要去进行移除的来自Netty的Channel
         */
        @JvmStatic
        fun removeChannel(channel: Channel) {
            val nettyChannel = CHANNEL_MAP.remove(channel)
            nettyChannel?.markActive(false)
        }
    }
}