package com.wanna.spring.dubbo.remoting.transport

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.Channel
import com.wanna.spring.dubbo.remoting.ChannelHandler
import com.wanna.spring.dubbo.remoting.RemotingException

/**
 * 抽象的Channel的实现
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
abstract class AbstractChannel(url: URL, handler: ChannelHandler) : AbstractPeer(url, handler), Channel {

    override fun send(message: Any, sent: Boolean) {
        if (isClosed()) {
            throw RemotingException("Channel已经关闭")
        }
    }
}