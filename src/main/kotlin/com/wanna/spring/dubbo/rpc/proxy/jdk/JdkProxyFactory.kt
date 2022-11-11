package com.wanna.spring.dubbo.rpc.proxy.jdk

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.rpc.Invoker
import com.wanna.spring.dubbo.rpc.ProxyFactory
import com.wanna.spring.dubbo.rpc.proxy.AbstractProxyInvoker

/**
 * JdkProxyFactory
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/25
 */
open class JdkProxyFactory : ProxyFactory {

    override fun <T> getInvoker(proxy: T, type: Class<T>, url: URL): Invoker<T> {
        return object : AbstractProxyInvoker<T>(proxy, type, url) {
            override fun doInvoke(
                proxy: T,
                methodName: String,
                parameterTypes: Array<Class<*>>,
                arguments: Array<Any?>
            ): Any? {
                val method = proxy!!::class.java.getMethod(methodName, *parameterTypes)
                return method.invoke(proxy, *arguments)
            }
        }
    }
}