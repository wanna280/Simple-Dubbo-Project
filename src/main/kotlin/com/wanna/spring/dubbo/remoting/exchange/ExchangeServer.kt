package com.wanna.spring.dubbo.remoting.exchange

import com.wanna.spring.dubbo.remoting.RemotingServer
import java.net.InetSocketAddress

/**
 * 支持提供数据的交换的RemotingServer
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface ExchangeServer : RemotingServer {

    /**
     * 获取所有的支持去进行数据交换的Channel列表
     *
     * @return ExchangeChannel列表
     */
    fun getExchangeChannels(): Collection<ExchangeChannel>

    /**
     * 根据"ip:port"去获取到对应的ExchangeChannel
     *
     * @param address address
     * @return ExchangeChannel(获取不到ExchangeChannel的话，return null)
     */
    fun getExchangeChannel(address: InetSocketAddress): ExchangeChannel?
}