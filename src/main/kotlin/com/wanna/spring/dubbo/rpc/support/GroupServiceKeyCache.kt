package com.wanna.spring.dubbo.rpc.support

import com.wanna.spring.dubbo.common.utils.StringUtils
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/24
 */
open class GroupServiceKeyCache(val serviceGroup: String) {

    /**
     * ServiceKey的缓存 serviceName-serviceVersion-servicePort-->serviceKey
     */
    private val serviceKeyMap = ConcurrentHashMap<String, ConcurrentMap<String, ConcurrentMap<Int, String>>>()

    /**
     * 获取ServiceKeyName
     *
     * @param serviceName serviceName
     * @param serviceVersion serviceVersion
     * @param port port
     * @return serviceNameKey(("group/serviceName:serviceVersion:port"))
     */
    open fun getServiceKey(serviceName: String, serviceVersion: String?, port: Int): String {
        val version = serviceVersion ?: ""
        var versionMap: ConcurrentMap<String, ConcurrentMap<Int, String>>? = serviceKeyMap[serviceName]
        if (versionMap == null) {
            serviceKeyMap.putIfAbsent(serviceName, ConcurrentHashMap())
            versionMap = serviceKeyMap[serviceName]
        }
        var portMap = versionMap!![version]
        if (portMap == null) {
            versionMap.putIfAbsent(version, ConcurrentHashMap())
            portMap = versionMap[version]
        }
        var serviceKey = portMap!![port]
        if (serviceKey == null) {
            serviceKey = createServiceKey(serviceName, version, port)
            portMap[port] = serviceKey
        }
        return serviceKey!!
    }

    /**
     * 生成serviceKey("group/serviceName:serviceVersion:port")
     *
     * @param serviceName serviceName
     * @param serviceVersion serviceVersion
     * @param port port
     */
    private fun createServiceKey(serviceName: String, serviceVersion: String, port: Int): String? {
        val buf = StringBuilder()
        if (StringUtils.isNotEmpty(serviceGroup)) {
            buf.append(serviceGroup).append('/')
        }
        buf.append(serviceName)
        if (StringUtils.isNotEmpty(serviceVersion) && "0.0.0" != serviceVersion) {
            buf.append(':').append(serviceVersion)
        }
        buf.append(':').append(port)
        return buf.toString()
    }
}