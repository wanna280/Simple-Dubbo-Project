package com.wanna.spring.dubbo.config

open class ReferenceConfig<T> : ReferenceConfigBase<T>() {

    open fun get(): T {
        if (this.ref == null) {
            init()
        }
        return this.ref ?: throw IllegalStateException("无法获取到ref")
    }

    @Synchronized
    private fun init() {
        // TODO
    }
}