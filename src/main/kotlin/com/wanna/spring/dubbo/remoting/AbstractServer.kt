package com.wanna.spring.dubbo.remoting

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.common.threadpool.manager.DefaultExecutorRepository
import com.wanna.spring.dubbo.remoting.transport.AbstractPeer
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.util.concurrent.ExecutorService

/**
 * 抽象的RemotingServer的实现
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 *
 * @param url url
 * @param handler handler
 */
abstract class AbstractServer(url: URL, handler: ChannelHandler) : AbstractPeer(url, handler),
    RemotingServer {

    companion object {
        /**
         * Logger
         */
        @JvmStatic
        private val logger = LoggerFactory.getLogger(AbstractServer::class.java)
    }

    /**
     * 要去进行绑定的Socket地址
     */
    private var bindAddress: InetSocketAddress

    /**
     * localAddress
     */
    private val localAddress: InetSocketAddress

    /**
     * ExecutorRepository
     */
    private val executorRepository = DefaultExecutorRepository()

    /**
     * ExecutorService
     */
    private var executorService: ExecutorService

    init {
        val host = url.getParameter(Constants.BIND_IP_KEY, url.getHost() ?: throw IllegalStateException("host不能为null"))
        val port = url.getParameter(Constants.BIND_PORT_KEY, url.getPort())

        localAddress = url.toInetSocketAddress()
        bindAddress = InetSocketAddress(host, port)

        this.executorService = executorRepository.createExecutorIfAbsent(url)

        try {
            this.doOpen()
            if (logger.isInfoEnabled) {
                logger.info("绑定[${bindAddress}]启动[${this.javaClass.simpleName}]成功, 暴露端口号[$localAddress]")
            }
        } catch (ex: Throwable) {
            throw RemotingException("启动Server失败", ex)
        }
    }

    /**
     * 启动Server
     */
    protected abstract fun doOpen()

    /**
     * 关闭Server
     */
    protected abstract fun doClose()

    /**
     * 获取需要去进行绑定的地址
     *
     * @return InetSocketAddress
     */
    open fun getBindAddress(): InetSocketAddress = this.bindAddress

    /**
     * 获取LocalAddress
     *
     * @return LocalAddress
     */
    override fun getLocalAddress(): InetSocketAddress = this.localAddress

    /**
     * 利用Server去进行发送消息时，我们需要将消息去转发给所有的Channel去进行发送
     *
     * @param message message
     * @param sent 是否已经发送给Socket？
     */
    override fun send(message: Any, sent: Boolean) {
        getChannels().forEach {
            if (it.isConnected()) {
                it.send(message, sent)
            }
        }
    }

    open fun getCodec(): Codec2 {
        return null!!
    }

    override fun close() {

    }
}