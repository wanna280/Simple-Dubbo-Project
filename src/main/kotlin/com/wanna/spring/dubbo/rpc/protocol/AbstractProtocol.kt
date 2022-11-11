package com.wanna.spring.dubbo.rpc.protocol

import com.alibaba.nacos.common.utils.ConcurrentHashSet
import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.remoting.Constants
import com.wanna.spring.dubbo.rpc.Exporter
import com.wanna.spring.dubbo.rpc.Invoker
import com.wanna.spring.dubbo.rpc.Protocol
import com.wanna.spring.dubbo.rpc.support.ProtocolUtils
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

/**
 * 抽象的Protocol的实现，为所有的Protocol提供模板方法实现
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
abstract class AbstractProtocol : Protocol {

    /**
     * ExporterMap，维护了在当前Protocol当中要去进行暴露的Dubbo服务列表；
     * Key是serviceKey("group/serviceName:serviceVersion:port")，Value是对应的Exporter；
     * 当远程服务调用过来时，就会自动根据ServiceKey去找到对应的Exporter去进行执行
     */
    protected val exporterMap = ConcurrentHashMap<String, Exporter<*>>()

    /**
     * ServerMap, Key是"ip:port"
     */
    protected val serverMap = ConcurrentHashMap<String, ProtocolServer>()

    /**
     * Invokers(需要去调用调用远程服务的Invoker)
     */
    protected val invokers = ConcurrentHashSet<Invoker<*>>()

    /**
     * 根据URL去生成ServiceKey
     *
     * @param url url
     * @return ServiceKey("group/serviceName:serviceVersion:port")
     */
    protected open fun serviceKey(url: URL): String {
        return serviceKey(
            url.getParameter(Constants.BIND_PORT_KEY, url.getPort()),
            url.getPath() ?: throw IllegalStateException("path(serviceName)不能为null"),
            url.getParameter(Constants.VERSION_KEY),
            url.getParameter(Constants.GROUP_KEY)
        )
    }

    /**
     * 生成ServiceKey("group/serviceName:serviceVersion:port")
     *
     * @param port port
     * @param serviceGroup serviceGroup
     * @param serviceName serviceName
     * @param serviceVersion serviceVersion
     */
    protected open fun serviceKey(
        port: Int,
        serviceName: String,
        serviceVersion: String?,
        serviceGroup: String?
    ): String {
        return ProtocolUtils.serviceKey(port, serviceName, serviceVersion, serviceGroup)
    }

    /**
     * 获取ProtocolServers
     *
     * @return ProtocolServer
     */
    override fun getServers(): List<ProtocolServer> = Collections.unmodifiableList(ArrayList(serverMap.values))

    /**
     * 引用Dubbo服务，返回一个Invoker
     *
     * @param type Dubbo服务类型
     * @param url URL
     * @return 调用远程服务的Invoker
     */
    override fun <T> refer(type: Class<T>, url: URL): Invoker<T> {
        return protocolBindingRefer(type, url)
    }

    /**
     * 执行协议绑定的引用，交给子类去进行实现
     *
     * @param type Dubbo服务接口
     * @param url url
     * @return Invoker
     */
    protected abstract fun <T> protocolBindingRefer(type: Class<T>, url: URL): Invoker<T>

    /**
     * 获取ExporterMap
     *
     * @return ExporterMap
     */
    open fun getExporterMap(): Map<String, Exporter<*>> = this.exporterMap

    /**
     * 获取Exporters
     *
     * @return Exporters
     */
    open fun getExporters(): Collection<Exporter<*>> = Collections.unmodifiableCollection(exporterMap.values)
}