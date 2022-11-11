package com.wanna.spring.dubbo.rpc.protocol

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.rpc.Invoker

/**
 * 抽象的Invoker的实现
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/25
 */
abstract class AbstractInvoker<T>(private val type: Class<T>, private val url: URL) : Invoker<T> {

    override fun getUrl(): URL = this.url

    override fun getInterface(): Class<T> = type
}