package com.wanna.spring.dubbo.rpc

import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface Result {
    /**
     * thenApply
     *
     * @param fn 用于转换的function
     * @return CompletableFuture
     */
    fun <U> thenApply(fn: Function<com.wanna.spring.dubbo.rpc.Result, out U>): CompletableFuture<U>
}