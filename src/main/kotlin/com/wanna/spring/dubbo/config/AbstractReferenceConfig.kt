package com.wanna.spring.dubbo.config

abstract class AbstractReferenceConfig : AbstractInterfaceConfig() {

    /**
     * 是否要引用远程的Dubbo服务？
     */
    var injvm: Boolean = false
}