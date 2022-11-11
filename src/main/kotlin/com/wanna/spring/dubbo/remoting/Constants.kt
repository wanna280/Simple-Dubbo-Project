package com.wanna.spring.dubbo.remoting

import kotlin.math.min

/**
 * Remoting相关的常量信息
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
object Constants {

    /**
     * TimeoutKey
     */
    const val TIMEOUT_KEY = "timeout"

    /**
     * 默认超时时间
     */
    const val DEFAULT_TIMEOUT = 1000

    /**
     * 绑定的IP的Key
     */
    const val BIND_IP_KEY = "bind.ip"

    /**
     * 绑定的端口号的Key
     */
    const val BIND_PORT_KEY = "bind.port"

    /**
     * ServiceInterface的Key
     */
    const val INTERFACE_KEY = "interface"

    /**
     * HostKey
     */
    const val HOST_KEY = "host"

    /**
     * GroupKey
     */
    const val GROUP_KEY = "group"

    /**
     * Path的Key
     */
    const val PATH_KEY = "path"

    /**
     * VersionKey
     */
    const val VERSION_KEY = "version"


    /**
     * IO线程数量的Key
     */
    const val IO_THREAD_KEY = "iothreads"

    /**
     * 默认的IO线程数量 = min(系统的逻辑处理器数量+1, 32)
     */
    @JvmField
    val DEFAULT_IO_THREADS = min(Runtime.getRuntime().availableProcessors() + 1, 32)
}