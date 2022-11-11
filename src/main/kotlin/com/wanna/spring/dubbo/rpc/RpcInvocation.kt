package com.wanna.spring.dubbo.rpc

/**
 * RpcInvocation
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/25
 */
open class RpcInvocation : Invocation {

    /**
     * Invoker
     */
    private var invoker: Invoker<*>? = null

    open fun setInvoker(invoker: Invoker<*>) {
        this.invoker = invoker
    }

    override fun getAttachments(): Map<String, String> {
        return emptyMap()
    }
}