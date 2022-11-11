package com.wanna.spring.dubbo.rpc

import com.wanna.spring.dubbo.common.URL

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface Node {
    fun getUrl(): URL
}