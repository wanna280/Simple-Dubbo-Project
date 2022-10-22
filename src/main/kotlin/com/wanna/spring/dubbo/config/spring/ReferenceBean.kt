package com.wanna.spring.dubbo.config.spring

import com.wanna.framework.beans.factory.InitializingBean
import com.wanna.framework.context.ApplicationContext
import com.wanna.framework.context.ApplicationContextAware
import com.wanna.spring.dubbo.config.ReferenceConfig
import java.io.Serializable

/**
 * 将Dubbo的ReferenceConfig转接到Spring的ReferenceBean，
 * 描述的是DubboReference的去进行引用DubboService的Bean
 *
 * @see com.wanna.spring.dubbo.annotation.DubboReference
 */
open class ReferenceBean<T> : ReferenceConfig<T>(), Serializable, ApplicationContextAware,InitializingBean {

    /**
     * ApplicationContext
     */
    private var applicationContext: ApplicationContext? = null

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    open fun getApplicationContext(): ApplicationContext =
        this.applicationContext ?: throw IllegalStateException("ApplicationContext不能为null")

    override fun afterPropertiesSet() {

    }
}