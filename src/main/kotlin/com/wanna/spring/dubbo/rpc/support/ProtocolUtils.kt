package com.wanna.spring.dubbo.rpc.support

import java.util.concurrent.ConcurrentHashMap

/**
 * Protocol工具类
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/24
 */
object ProtocolUtils {

    /**
     * GroupServiceKey的缓存, Key-serviceGroup
     */
    private val groupServiceKeyCacheMap = ConcurrentHashMap<String, GroupServiceKeyCache>()

    /**
     * 获取ServiceKey
     *
     * @param port port
     * @param serviceGroup serviceGroup
     * @param serviceName serviceName
     * @param serviceVersion serviceVersion
     * @return serviceKey("group/serviceName:serviceVersion:port")
     */
    @JvmStatic
    fun serviceKey(port: Int, serviceName: String, serviceVersion: String?, serviceGroup: String?): String {
        val group = serviceGroup ?: ""
        var groupServiceKeyCache = groupServiceKeyCacheMap[group]
        if (groupServiceKeyCache == null) {
            groupServiceKeyCacheMap.putIfAbsent(group, GroupServiceKeyCache(group))
            groupServiceKeyCache = groupServiceKeyCacheMap[group]!!
        }
        return groupServiceKeyCache.getServiceKey(serviceName, serviceVersion, port)
    }
}