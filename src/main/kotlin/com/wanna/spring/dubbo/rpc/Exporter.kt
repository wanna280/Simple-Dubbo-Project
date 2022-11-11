package com.wanna.spring.dubbo.rpc

/**
 * Dubbo服务暴露的暴露器，提供对于一个Dubbo服务的暴露
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface Exporter<T> {

    /**
     * 获取Invoker，提供对于远程调用的请求处理
     *
     * @return Invoker
     */
    fun getInvoker(): Invoker<T>

    /**
     * 取消Dubbo服务的暴露
     */
    fun unexport()
}