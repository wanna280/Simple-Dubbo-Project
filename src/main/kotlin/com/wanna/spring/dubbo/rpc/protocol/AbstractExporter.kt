package com.wanna.spring.dubbo.rpc.protocol

import com.wanna.spring.dubbo.rpc.Exporter
import com.wanna.spring.dubbo.rpc.Invoker

/**
 * 抽象的Dubbo服务的暴露器的实现
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 *
 * @param invoker invoker
 */
abstract class AbstractExporter<T>(private val invoker: Invoker<T>) : Exporter<T> {

    override fun getInvoker(): Invoker<T> = this.invoker

    override fun unexport() {

    }
}