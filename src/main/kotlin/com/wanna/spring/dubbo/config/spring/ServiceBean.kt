package com.wanna.spring.dubbo.config.spring

import com.wanna.framework.beans.factory.InitializingBean
import com.wanna.framework.beans.factory.support.DisposableBean
import com.wanna.framework.context.ApplicationContext
import com.wanna.framework.context.ApplicationContextAware
import com.wanna.framework.context.ApplicationEventPublisherAware
import com.wanna.framework.context.aware.BeanNameAware
import com.wanna.framework.context.event.ApplicationEventPublisher
import com.wanna.spring.dubbo.config.ServiceConfig

/**
 * 将Dubbo的ServiceConfig转接到Dubbo的ServiceConfig的类，描述的是Spring当中一个标注了@DubboService注解的DubboService
 *
 * @see com.wanna.spring.dubbo.annotation.DubboService
 */
open class ServiceBean<T> : ServiceConfig<T>(), ApplicationContextAware, ApplicationEventPublisherAware, BeanNameAware,
    InitializingBean, DisposableBean {

    private var applicationContext: ApplicationContext? = null

    private var applicationEventPublisher: ApplicationEventPublisher? = null

    private var beanName: String? = null

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun setApplicationEventPublisher(publisher: ApplicationEventPublisher) {
        this.applicationEventPublisher = publisher
    }

    override fun setBeanName(beanName: String) {
        this.beanName = beanName
    }

    override fun afterPropertiesSet() {

    }

    override fun destroy() {

    }
}