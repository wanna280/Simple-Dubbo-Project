package com.wanna.spring.dubbo.rpc.protocol.dubbo

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.Channel
import com.wanna.spring.dubbo.remoting.Constants
import com.wanna.spring.dubbo.remoting.RemotingException
import com.wanna.spring.dubbo.remoting.exchange.ExchangeChannel
import com.wanna.spring.dubbo.remoting.exchange.ExchangeHandler
import com.wanna.spring.dubbo.remoting.exchange.Exchangers
import com.wanna.spring.dubbo.rpc.Exporter
import com.wanna.spring.dubbo.rpc.Invocation
import com.wanna.spring.dubbo.rpc.Invoker
import com.wanna.spring.dubbo.rpc.protocol.AbstractProtocol
import com.wanna.spring.dubbo.rpc.protocol.ProtocolServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * DubboProtocol的实现
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class DubboProtocol : AbstractProtocol() {

    companion object {
        @JvmField
        val INSTANCE = DubboProtocol()

        /**
         * Dubbo协议的默认端口号
         */
        const val DEFAULT_PORT = 20880

        /**
         * 是否是一个Callback的Service调用？
         */
        const val IS_CALLBACK_SERVICE_INVOKE = "_isCallBackServiceInvoke"
    }

    /**
     * Logger
     */
    protected val logger: Logger = LoggerFactory.getLogger(DubboProtocol::class.java)

    /**
     * 处理Dubbo请求的Handler
     */
    private val requestHandler = object : ExchangeHandler {
        override fun reply(channel: ExchangeChannel, request: Any): CompletableFuture<Any?>? {
            if (request !is Invocation) {
                throw IllegalStateException("request必须是一个Invocation才支持去进行处理")
            }
            if (request.getAttachments()[IS_CALLBACK_SERVICE_INVOKE] == "true") {

            }

            val invoker = getInvoker(channel, request)
            val result = invoker.invoke(request)

            // 返回结果
            return result.thenApply(Function.identity())
        }

        /**
         * 接收到消息时的Callback回调方法
         *
         * @param channel Channel
         * @param message 接收到的消息
         */
        override fun received(channel: Channel, message: Any?) {
            if (message is Invocation) {
                reply(channel as ExchangeChannel, message)
            } else {
                super.received(channel, message)
            }
        }
    }

    /**
     * 根据Channel和Invocation，去获取到对应的Invoker
     *
     * @param channel Channel
     * @param invocation Invocation(方法执行的上下文信息)
     * @return 找到的合适的Invoker
     * @throws RemotingException 如果找不到合适的Invoker的话
     */
    open fun getInvoker(channel: ExchangeChannel, invocation: Invocation): Invoker<*> {

        val path = invocation.getAttachments()[Constants.PATH_KEY]!!
        // 获取ServiceKey
        val serviceKey = serviceKey(
            -1, path,
            invocation.getAttachments()[Constants.VERSION_KEY],
            invocation.getAttachments()[Constants.GROUP_KEY]
        )

        // 根据ServiceKey去获取到对应的Exporter
        val exporter = exporterMap[serviceKey]

        if (exporter == null) {
            throw RemotingException("无法根据给定的[$serviceKey]去找到已经暴露的Dubbo服务, 可能是group/version不匹配导致的")  // TODO, 文本...
        }

        return exporter.getInvoker()
    }

    /**
     * 默认端口号为20880
     *
     * @return 20880
     */
    override fun getDefaultPort() = DEFAULT_PORT

    /**
     * 暴露一个Dubbo服务到注册中心当中
     *
     * @param invoker invoker
     * @return Exporter
     */
    override fun <T> export(invoker: Invoker<T>): Exporter<T> {
        val url = invoker.getUrl()

        // 生成serviceKey("group/serviceName:serviceVersion:port")
        val serviceKey = serviceKey(url)

        // 构建出来一个Dubbo服务的Exporter
        val dubboExporter = DubboExporter<T>(invoker, serviceKey, this.exporterMap)

        // 保存到ExporterMap当中
        this.exporterMap[serviceKey] = dubboExporter

        // open ProtocolServer并保存到serverMap当中
        openServer(url)
        return dubboExporter
    }

    /**
     * 从注册中心当中去引用一个Dubbo服务，获取到调用远程服务的Invoker
     *
     * @param type type
     * @param url url
     * @return Invoker
     */
    override fun <T> protocolBindingRefer(type: Class<T>, url: URL): Invoker<T> {
        val dubboInvoker = DubboInvoker<T>(type, url)
        invokers.add(dubboInvoker)
        return dubboInvoker
    }

    /**
     * DubboProtocol的生命周期的destroy方法，remove掉所有的Invoker和Exporter
     *
     * @see invokers
     * @see exporterMap
     */
    override fun destroy() {
        // remove, 对于ConcurrentHashSet，允许直接删除
        invokers.forEach(invokers::remove)

        // remove exporter
        exporterMap.keys.forEach {
            val exporter = exporterMap.remove(it)
            if (exporter != null) {
                try {
                    if (logger.isInfoEnabled) {
                        logger.info("Unexport service: [${exporter.getInvoker().getUrl()}]")
                    }
                    exporter.unexport()
                } catch (ex: Throwable) {
                    logger.warn(ex.message, ex)
                }
            }
        }

    }

    private fun openServer(url: URL) {
        val key = url.getAddress()

        var protocolServer = serverMap[key]
        if (protocolServer == null) {
            protocolServer = serverMap[key]
            if (protocolServer == null) {
                protocolServer = serverMap.put(key, createServer(url))
            }
        }
    }

    private fun createServer(url: URL): ProtocolServer {
        return doCreateServer(url)
    }

    private fun doCreateServer(url: URL): ProtocolServer {
        // Protocol-->Exchanger-->Transporter-->NettyServer
        val exchangeServer = Exchangers.bind(url, requestHandler)
        return DubboProtocolServer(exchangeServer)
    }
}