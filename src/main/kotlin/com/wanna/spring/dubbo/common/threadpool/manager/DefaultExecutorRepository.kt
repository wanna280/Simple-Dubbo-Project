package com.wanna.spring.dubbo.common.threadpool.manager

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.common.constants.CommonConstants
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 默认的ExecutorRepository的实现
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class DefaultExecutorRepository : ExecutorRepository {

    /**
     * data(hKey-side(consumer/provider/...), key-port, value-ExecutorService)
     */
    private val executorMap = ConcurrentHashMap<String, ConcurrentMap<Int, ExecutorService>>()

    /**
     * 如果必要的话，去创建一个ExecutorService(如果缓存当中已经有的话，那么我们直接从缓存当中去进行获取)
     *
     * @param url URL
     * @return ExecutorService
     */
    @Synchronized
    override fun createExecutorIfAbsent(url: URL): ExecutorService {
        var componentKey = CommonConstants.EXECUTOR_SERVICE_COMPONENT_KEY
        if (url.getParameter(CommonConstants.SIDE_KEY) == CommonConstants.CONSUMER_SIDE) {
            componentKey = CommonConstants.CONSUMER_SIDE
        }
        val executors = executorMap.computeIfAbsent(componentKey) { ConcurrentHashMap() }
        val port = url.getPort()
        var executorService = executors.computeIfAbsent(port) { createExecutor(url) }
        if (executorService.isTerminated || executorService.isShutdown) {
            executors.remove(port)
            executorService = createExecutor(url)
            executors[port] = executorService
        }
        return executorService
    }

    private fun createExecutor(url: URL): ExecutorService {
        return Executors.newSingleThreadExecutor() // TODO
    }
}