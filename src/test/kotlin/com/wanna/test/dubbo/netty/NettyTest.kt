package com.wanna.test.dubbo.netty

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.Channel
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.netty.NettyServer

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
class NettyTest {
}

fun main() {
    val nettyServer = NettyServer(URL("", "", 0), object : ChannelHandler {
        override fun connected(channel: Channel) {

        }

        override fun disconnected(channel: Channel) {

        }

        override fun sent(channel: Channel, message: Any?) {

        }

        override fun received(channel: Channel, message: Any?) {

        }

        override fun caught(channel: Channel) {

        }
    })
}