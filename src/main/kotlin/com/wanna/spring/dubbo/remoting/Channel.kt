package com.wanna.spring.dubbo.remoting

import java.net.InetSocketAddress

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface Channel : Endpoint {

    fun isConnected(): Boolean

    fun getRemoteAddress(): InetSocketAddress
}