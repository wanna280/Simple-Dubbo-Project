package com.wanna.spring.dubbo.rpc.protocol.dubbo

import com.wanna.spring.dubbo.common.URL
import com.wanna.spring.dubbo.rpc.Invocation
import com.wanna.spring.dubbo.rpc.Result
import com.wanna.spring.dubbo.rpc.RpcInvocation
import com.wanna.spring.dubbo.rpc.protocol.AbstractInvoker

/**
 * DubboInvoker的实现
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/25
 */
open class DubboInvoker<T>(type: Class<T>, url: URL) : AbstractInvoker<T>(type, url) {

    /**
     * Invoker的invoke方法
     */
    override fun invoke(invocation: Invocation): Result {
        if (invocation is RpcInvocation) {
            invocation.setInvoker(this)

        }
        throw IllegalStateException("invocation必须的RpcInvocation类型")
    }
}