package com.wanna.test.dubbo.protocol

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.transport.Transporters
import com.wanna.spring.dubbo.rpc.Invocation
import com.wanna.spring.dubbo.rpc.Invoker
import com.wanna.spring.dubbo.rpc.Protocol
import com.wanna.spring.dubbo.rpc.Result
import com.wanna.spring.dubbo.rpc.protocol.dubbo.DubboProtocol

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
class DubboProtocolTest {
}

fun main() {
    val url = URL("http", "127.0.0.1", 8080, "wanna", emptyMap())
    val dubboProtocol = DubboProtocol()
    dubboProtocol.refer(Protocol::class.java, url)

    val exporter = dubboProtocol.export(object : Invoker<Any> {
        override fun getInterface(): Class<Any> {
            TODO("Not yet implemented")
        }

        override fun invoke(invocation: Invocation): Result {
            TODO("Not yet implemented")
        }

        override fun getUrl(): URL = url
    })
}