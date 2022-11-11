package com.wanna.spring.dubbo.rpc

import com.wanna.spring.dubbo.common.URL

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/25
 */
interface ProxyFactory {

    /**
     * 获取Invoker
     *
     * @param proxy proxy
     * @param type ServiceInterface
     * @param url URL
     * @return Invoker
     */
    fun <T> getInvoker(proxy: T, type: Class<T>, url: URL): Invoker<T>
}