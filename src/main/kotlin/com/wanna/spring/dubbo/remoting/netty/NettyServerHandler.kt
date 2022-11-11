package com.wanna.spring.dubbo.remoting.netty

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.common.utils.NetUtils
import com.wanna.spring.dubbo.remoting.Channel
import com.wanna.spring.dubbo.remoting.ChannelHandler
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

/**
 * NettyServerHandler，针对于原生的Netty的[ChannelHandler]的实现，负责处理Dubbo的调用；
 * * 1.当远程连接到来时，就会触发相关的方法，我们需要将它去交给Dubbo的ChannelHandler去进行真正的请求的处理；
 * * 2.对于Dubbo的ChannelHandler需要处理的是Dubbo的Channel，我们还需要将它去转换成为Dubbo实现的NettyChannel去交给ChannelHandler
 * * 3.我们需要在[channels]字段维护起来所有的DubboChannel的列表，Key为"ip:port"，Value为DubboChannel
 *
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 *
 * @param channelHandler ChannelHandler
 * @param url URL
 */
@Sharable
open class NettyServerHandler(private val channelHandler: ChannelHandler, private val url: URL) :
    ChannelDuplexHandler() {

    /**
     * Channels，Key:"ip:port"，Value-DubboChannel
     */
    private val channels = ConcurrentHashMap<String, Channel>()

    override fun channelActive(ctx: ChannelHandlerContext) {
        val channel = NettyChannel.getOrAddChannel(ctx.channel(), url, channelHandler)
        channels[NetUtils.toAddressString(ctx.channel().remoteAddress() as InetSocketAddress)] = channel
        channelHandler.connected(channel)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val channel = NettyChannel.getOrAddChannel(ctx.channel(), url, channelHandler)
        try {
            channelHandler.disconnected(channel)
            channels.remove(NetUtils.toAddressString(ctx.channel().remoteAddress() as InetSocketAddress))
        } finally {
            NettyChannel.removeChannel(ctx.channel())
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val channel = NettyChannel.getOrAddChannel(ctx.channel(), url, channelHandler)
        channelHandler.received(channel, msg)
    }

    override fun write(ctx: ChannelHandlerContext, msg: Any?, promise: ChannelPromise) {
        val channel = NettyChannel.getOrAddChannel(ctx.channel(), url, channelHandler)
        promise.addListener { future ->
            if (future.isSuccess) {
                channelHandler.sent(channel, msg)
            }
        }

    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        val channel = NettyChannel.getOrAddChannel(ctx.channel(), url, channelHandler)
        try {
            channelHandler.caught(channel)
        } finally {
            NettyChannel.removeChannel(ctx.channel())
        }
    }

    /**
     * 获取所有的Channels
     *
     * @return Channels
     */
    open fun getChannels(): Map<String, Channel> = channels
}