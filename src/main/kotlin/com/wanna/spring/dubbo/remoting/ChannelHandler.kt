package com.wanna.spring.dubbo.remoting

/**
 * Dubbo封装的ChannelHandler，对应一个原生的Netty的ChannelHandler当中的相关事件
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface ChannelHandler {
    /**
     * 当连接建立...
     *
     * @param channel 建立连接的Channel
     */
    fun connected(channel: Channel) {

    }

    /**
     * 当连接断开
     *
     * @param channel 断开连接的Channel
     */
    fun disconnected(channel: Channel) {

    }

    /**
     * 发送消息...
     *
     * @param channel 需要发送消息的Channel
     * @param message message
     */
    fun sent(channel: Channel, message: Any?) {

    }

    /**
     * 接收到消息...
     *
     * @param channel 接收到消息的Channel
     * @param message message
     */
    fun received(channel: Channel, message: Any?) {

    }

    /**
     * 当发生异常...
     *
     * @param channel 发生异常的Channel
     */
    fun caught(channel: Channel) {

    }
}