package com.wanna.spring.dubbo.rpc.service

/**
 * Dubbo的通用Service的接口
 */
interface GenericService {
    fun invoke(method: String?, parameterTypes: Array<String?>?, args: Array<Any?>?): Any?
}