package com.wanna.spring.dubbo.config

abstract class ServiceConfigBase<T> : AbstractServiceConfig() {
    // 该ServiceBean对应的接口
    protected var interfaceClass: Class<*>? = null

    // 该接口对应的实现类的应用ref，也就是@DubboService的Bean的实例对象
    protected var ref: T? = null

    abstract fun export()
}