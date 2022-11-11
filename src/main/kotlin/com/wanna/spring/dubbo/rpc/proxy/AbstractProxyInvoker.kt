package com.wanna.spring.dubbo.rpc.proxy

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.rpc.AsyncRpcResult
import com.wanna.spring.dubbo.rpc.Invocation
import com.wanna.spring.dubbo.rpc.Invoker
import com.wanna.spring.dubbo.rpc.Result

/**
 * 基于代理实现的Invoker
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/25
 */
abstract class AbstractProxyInvoker<T>(private val proxy: T, private val type: Class<T>, private val url: URL) :
    Invoker<T> {
    override fun getInterface(): Class<T> = type

    override fun invoke(invocation: Invocation): Result {
        // 执行目标方法
        val result = doInvoke(proxy, "", emptyArray(), emptyArray())

        return AsyncRpcResult()
    }

    protected abstract fun doInvoke(
        proxy: T,
        methodName: String,
        parameterTypes: Array<Class<*>>,
        arguments: Array<Any?>
    ): Any?

    override fun getUrl(): URL = url
}