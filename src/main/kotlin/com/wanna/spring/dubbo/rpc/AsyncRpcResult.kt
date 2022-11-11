package com.wanna.spring.dubbo.rpc

import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/25
 */
class AsyncRpcResult :Result {
    override fun <U> thenApply(fn: Function<Result, out U>): CompletableFuture<U> {
        TODO("Not yet implemented")
    }
}