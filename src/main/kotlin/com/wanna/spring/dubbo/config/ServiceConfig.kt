package com.wanna.spring.dubbo.config

import com.alibaba.nacos.api.NacosFactory
import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.common.constants.RegistryConstants
import com.wanna.spring.dubbo.common.constants.RegistryConstants.DYNAMIC_KEY
import com.wanna.spring.dubbo.common.constants.RegistryConstants.PROXY_KEY
import com.wanna.spring.dubbo.common.utils.StringUtils
import com.wanna.spring.dubbo.config.invoker.DelegateProviderMetaDataInvoker
import com.wanna.spring.dubbo.rpc.Exporter
import com.wanna.spring.dubbo.rpc.Protocol
import com.wanna.spring.dubbo.rpc.ProxyFactory
import com.wanna.spring.dubbo.rpc.cluster.Constants
import com.wanna.spring.dubbo.rpc.cluster.Constants.EXPORT_KEY
import com.wanna.spring.dubbo.rpc.model.ApplicationModel
import com.wanna.spring.dubbo.rpc.model.ServiceMetadata
import com.wanna.spring.dubbo.rpc.protocol.dubbo.DubboProtocol
import com.wanna.spring.dubbo.rpc.proxy.jdk.JdkProxyFactory

/**
 * 维护的是注册到Dubbo的一个Service的Bean，一个ServiceBean对应一个@DubboService的Bean
 */
open class ServiceConfig<T> : ServiceConfigBase<T>() {

    companion object {

        /**
         * Protocol
         */
        private val PROTOCOL: Protocol = DubboProtocol.INSTANCE

        /**
         * ProxyFactory
         */
        private val PROXY_FACTORY: ProxyFactory = JdkProxyFactory()
    }


    @Volatile
    private var exported: Boolean = false

    /**
     * Exporters
     */
    private val exporters = ArrayList<Exporter<*>>()

    open fun isExported(): Boolean = this.exported


    /**
     * 暴露一个DubboService到注册中心当中
     */
    open fun exported() {
        this.exported = true
    }

    @Synchronized
    override fun export() {
        val namingService = NacosFactory.createNamingService("127.0.0.1:8848")
        namingService.registerInstance(interfaceClass?.name ?: "wanna", "127.0.0.1", 9789)

        doExport()

        exported()
    }

    @Synchronized
    open fun doExport() {

        // 如果path为空，那么我们需要把它去初始化为interfaceName
        // 因为我们最终要去进行暴露的URL的path是这个...
        if (StringUtils.isEmpty(path)) {
            path = interfaceName
        }

        doExportUrls()
    }

    private fun doExportUrls() {
        // 获取Dubbo服务仓库
        val serviceRepository = ApplicationModel.getServiceRepository()

        // 为当前ServiceConfig当中维护的接口去注册Service到仓库当中
        serviceRepository.registerService(getInterfaceClazz())

        // 注册一个Provider到仓库当中
        serviceRepository.registerProvider(getUniqueServiceName(), getReference()!!, this, ServiceMetadata())

        // 加载所有的注册中心
        val registryURLs = loadRegistries(this, true)

        // 遍历所有要去进行暴露的的protocol，将它去暴露到注册中心当中
        protocols.forEach {
            doExportUrlsFor1Protocol(registryURLs, it)
        }
    }

    /**
     * 针对一个Protocol，将它去注册到所有的RegistryURL当中
     *
     * @param registryURLs 注册中心URL
     * @param protocolConfig Protocol
     */
    @Suppress("UNCHECKED_CAST")
    private fun doExportUrlsFor1Protocol(registryURLs: List<URL>, protocolConfig: ProtocolConfig) {
        val name = protocolConfig.name
        val parametersMap = LinkedHashMap<String, String>()

        // 对于host和port，都得遍历本地网卡的方式去进行搜索

        // 生成一个URL，这个URL用于描述当前要去进行暴露的服务的相关信息
        // 比如需要将当前服务暴露在哪个端口？以及一些Dubbo的元数据信息
        var url = URL(name, "127.0.0.1", 8080, path, parametersMap)


        // 为所有的Registry去注册服务
        registryURLs.forEach { registryURL ->

            url = url.addParameterIfAbsent(DYNAMIC_KEY, registryURL.getParameter(DYNAMIC_KEY))
            val proxy = url.getParameter(PROXY_KEY)
            var newRegistryURL = registryURL
            if (StringUtils.isNotEmpty(proxy)) {
                newRegistryURL = registryURL.addParameterIfAbsent(PROXY_KEY, proxy)
            }

            val invoker = PROXY_FACTORY.getInvoker(
                ref as Any,
                interfaceClass!! as Class<Any>,

                // 使用RegistryURL去进行注册，将URL放入到RegistryURL当中
                newRegistryURL.addParameterIfAbsent(EXPORT_KEY, url.toString())
            )
            val wrapperInvoker = DelegateProviderMetaDataInvoker(invoker, ServiceMetadata())
            val exporter = PROTOCOL.export(wrapperInvoker)
            this.exporters += exporter
        }
    }

    /**
     * 从给定的ConfigBean当中去加载所有配置的RegistryConfig
     *
     * @param interfaceConfig ConfigBean
     * @param provider 是否是一个Provider？
     * @return 加载到的所有的注册中心URL
     */
    open fun loadRegistries(interfaceConfig: AbstractInterfaceConfig, provider: Boolean): List<URL> {
        interfaceConfig.registries.forEach {

        }
        return emptyList()
    }
}