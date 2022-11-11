package com.wanna.spring.dubbo.common.utils

import java.net.InetSocketAddress

/**
 * 网络相关的工具类
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
object NetUtils {

    /**
     * 将一个InetSocketAddress去转换成为"ip:port"的形式
     *
     * @param address InetSocketAddress
     * @return "ip:port"
     */
    @JvmStatic
    fun toAddressString(address: InetSocketAddress): String = address.hostString + ":" + address.port

    /**
     * 将"ip:port"去转换成为InetSocketAddress
     *
     * @param addressString "ip:port"
     * @return InetSocketAddress
     */
    @JvmStatic
    fun toAddress(addressString: String): InetSocketAddress {
        val index = addressString.indexOf(":")
        val host: String
        val port: Int
        if (index != -1) {
            host = addressString.substring(0, index)
            port = addressString.substring(index + 1).toInt()
        } else {
            host = addressString
            port = 0
        }
        return InetSocketAddress(host, port)
    }
}