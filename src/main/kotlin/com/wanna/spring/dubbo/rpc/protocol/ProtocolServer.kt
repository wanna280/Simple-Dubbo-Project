package com.wanna.spring.dubbo.rpc.protocol

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.RemotingServer

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface ProtocolServer {
    fun getRemotingServer(): RemotingServer? = null

    fun setRemotingServer(server: RemotingServer) {

    }

    fun getUrl(): URL? = null

    fun getAddress(): String

    fun setAddress(address: String)

    fun close()
}