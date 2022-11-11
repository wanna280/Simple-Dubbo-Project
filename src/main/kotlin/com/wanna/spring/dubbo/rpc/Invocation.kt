package com.wanna.spring.dubbo.rpc

/**
 * RPC方法执行需要用到的一些参数信息
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
interface Invocation {

    /**
     * 获取attachments
     *
     * @return attachments
     */
    fun getAttachments(): Map<String, String>
}