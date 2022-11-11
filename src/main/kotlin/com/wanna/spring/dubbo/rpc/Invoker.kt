package com.wanna.spring.dubbo.rpc

/**
 * Invoker，提供去执行远程RPC方法
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface Invoker<T> : Node {

    /**
     * 获取ServiceInterface
     *
     * @return ServiceInterface
     */
    fun getInterface(): Class<T>

    /**
     * 执行目标RPC方法
     *
     * @param  invocation Invocation
     * @return 执行目标方法的结果
     */
    fun invoke(invocation: Invocation): Result
}