package com.wanna.spring.dubbo.remoting

import com.wanna.spring.dubbo.common.URL
import java.net.InetSocketAddress

/**
 * Endpoint，用于描述一个Channel的通信的端点；
 * Channel的两端合并起来，就是一个Channel
 *
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface Endpoint {

    /**
     * 获取当前Endpoint的URL
     *
     * @return URL
     */
    fun getUrl(): URL

    /**
     * 获取Channel的本地地址
     *
     * @return 本地地址("ip:port")
     */
    fun getLocalAddress(): InetSocketAddress

    /**
     * 往Channel的另一端去发送消息
     *
     * @param message message
     */
    fun send(message: Any)

    /**
     * 往Channel的另一端去进行发送消息
     *
     * @param message  message
     * @param sent 是否已经发送过给Socket？
     */
    fun send(message: Any, sent: Boolean)

    /**
     * 关闭当前Endpoint和远程Endpoint之间的连接
     */
    fun close()
}