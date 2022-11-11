package com.wanna.spring.dubbo.config.invoker

import com.wanna.spring.dubbo.rpc.Invoker
import com.wanna.spring.dubbo.rpc.model.ServiceMetadata

/**
 * 委托ProviderMetadata的Invoker；
 * 对于内部的全部方法全部都委托给给定的invoker去执行，内部还包装了ServiceMetadata
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/25
 */
open class DelegateProviderMetaDataInvoker<T>(private val invoker: Invoker<T>, val metadata: ServiceMetadata) :
    Invoker<T> by invoker