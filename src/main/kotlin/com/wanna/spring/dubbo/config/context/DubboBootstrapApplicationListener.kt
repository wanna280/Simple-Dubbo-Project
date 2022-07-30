package com.wanna.spring.dubbo.config.context

import com.wanna.framework.context.event.ApplicationEvent
import com.wanna.framework.context.event.ApplicationListener
import com.wanna.framework.context.event.ContextRefreshedEvent
import com.wanna.framework.core.Ordered
import com.wanna.spring.dubbo.config.bootstrap.DubboBootstrap

/**
 * Dubbo的引导启动的监听器，当Spring容器刷新完成时(ContextRefreshedEvent事件发布)；
 * 使用DubboBootstrap，去将全部的DubboService去进行暴露(export)到注册中心当中
 *
 * @see ContextRefreshedEvent
 * @see DubboBootstrap
 */
open class DubboBootstrapApplicationListener : ApplicationListener<ApplicationEvent>, Ordered {

    companion object {
        const val BEAN_NAME = "dubboBootstrapApplicationListener"
    }

    override fun getOrder() = Ordered.ORDER_LOWEST

    private val dubboBootstrap = DubboBootstrap()

    override fun onApplicationEvent(event: ApplicationEvent) {
        if (event is ContextRefreshedEvent) {
            dubboBootstrap.start()
        }
    }
}