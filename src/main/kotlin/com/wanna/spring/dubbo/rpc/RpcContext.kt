package com.wanna.spring.dubbo.rpc

/**
 * RPC调用的上下文信息，单例对象
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/24
 */
object RpcContext {

    /**
     * 获取单例的RpcContext
     *
     * @return RpcContext
     */
    @JvmStatic
    fun getContext(): RpcContext = RpcContext
}