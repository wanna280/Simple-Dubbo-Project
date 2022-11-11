package com.wanna.spring.dubbo.common.threadpool.manager

import com.wanna.spring.dubbo.common.URL
import java.util.concurrent.ExecutorService

/**
 * 线程池的仓库
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface ExecutorRepository {

    /**
     * 如果必要的话，去创建一个Executor
     *
     * @param url URL
     * @return 获取/创建出来的ExecutorService线程池
     */
    fun createExecutorIfAbsent(url: URL): ExecutorService
}