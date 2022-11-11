package com.wanna.spring.dubbo.remoting.netty

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.Codec2
import io.netty.channel.ChannelHandler

/**
 * Netty的编解码器的适配器，负责将Dubbo实现的Code2去转换到Netty的Encoder和Decoder当中来
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 *
 * @param channelHandler ChannelHandler
 * @param url url
 * @param codec codec编解码器
 */
open class NettyCodecAdapter(
    val channelHandler: com.wanna.spring.dubbo.remoting.ChannelHandler,
    val url: URL,
    val codec: Codec2
) {
    open fun getEncoder(): ChannelHandler {
        return null!!
    }

    open fun getDecoder(): ChannelHandler {
        return null!!
    }
}