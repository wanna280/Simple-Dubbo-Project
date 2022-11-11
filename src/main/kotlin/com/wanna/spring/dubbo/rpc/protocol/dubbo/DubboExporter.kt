package com.wanna.spring.dubbo.rpc.protocol.dubbo

import com.wanna.spring.dubbo.rpc.Exporter
import com.wanna.spring.dubbo.rpc.Invoker
import com.wanna.spring.dubbo.rpc.protocol.AbstractExporter

/**
 * DubboExporter
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class DubboExporter<T>(
    invoker: Invoker<T>,
    private val key: String,
    private val exporterMap: MutableMap<String, Exporter<*>>
) : AbstractExporter<T>(invoker) {

    override fun unexport() {
        super.unexport()
        exporterMap.remove(key)
    }
}