package com.wanna.spring.dubbo.rpc.protocol.dubbo

import com.wanna.spring.dubbo.remoting.RemotingServer
import com.wanna.spring.dubbo.rpc.protocol.ProtocolServer

/**
 * DubboProtocolServer
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class DubboProtocolServer(private var server: RemotingServer) : ProtocolServer {

    /**
     * address
     */
    private var address: String? = null

    override fun getAddress(): String {
        return address ?: server.getUrl().getAddress()
    }

    override fun setAddress(address: String) {
        this.address = address
    }

    override fun getRemotingServer() = this.server

    override fun setRemotingServer(server: RemotingServer) {
        this.server = server
    }

    override fun getUrl() = server.getUrl()

    override fun close() {
        server.close()
    }
}