package com.wanna.spring.dubbo.annotation

import com.wanna.framework.context.ApplicationContext
import com.wanna.framework.core.annotation.AnnotationAttributes
import com.wanna.spring.dubbo.config.spring.ReferenceBean

/**
 * ReferenceBean的Builder
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
open class ReferenceBeanBuilder(
    attributes: AnnotationAttributes, applicationContext: ApplicationContext
) : AnnotatedInterfaceConfigBeanBuilder<ReferenceBean<*>>(attributes, applicationContext) {

    /**
     * 要去引用的Dubbo服务的接口(不能为null)
     */
    private var interfaceClass: Class<*>? = null

    override fun doBuild(): ReferenceBean<*> = ReferenceBean<Any>()

    override fun preConfigureBean(attributes: AnnotationAttributes, bean: ReferenceBean<*>) {
        interfaceClass ?: throw IllegalStateException("DubboReference的interfaceClass不能为null")
    }

    override fun postConfigureBean(attributes: AnnotationAttributes, bean: ReferenceBean<*>) {
        // 初始化ApplicationContext
        bean.setApplicationContext(applicationContext)

        configureInterface(attributes, bean)

        configureConsumerConfig(attributes, bean)

        configureMethodConfig(attributes, bean)

        // 完成ReferenceBean的初始化
        bean.afterPropertiesSet()
    }

    /**
     * 自定义interfaceClass
     *
     * @param interfaceClass interfaceClass
     * @return this
     */
    open fun interfaceClass(interfaceClass: Class<*>): ReferenceBeanBuilder {
        this.interfaceClass = interfaceClass
        return this
    }

    /**
     * 配置ReferenceBean的接口
     *
     * @param attributes @DubboReference注解的属性
     * @param referenceBean ReferenceBean
     */
    private fun configureInterface(attributes: AnnotationAttributes, referenceBean: ReferenceBean<*>) {
        referenceBean.interfaceClass = interfaceClass
        referenceBean.interfaceName = interfaceClass?.name
    }

    /**
     * 自定义ConfigBean的Consumer配置
     *
     * @param attributes @DubboReference注解属性
     * @param referenceBean 需要去进行自定义的ReferenceBean
     */
    private fun configureConsumerConfig(attributes: AnnotationAttributes, referenceBean: ReferenceBean<*>) {
        val consumer = attributes.getString("consumer")
    }

    private fun configureMethodConfig(attributes: AnnotationAttributes, referenceBean: ReferenceBean<*>) {

    }

    companion object {

        /**
         * 创建出来ReferenceBeanBuilder
         *
         * @param attributes @DubboReference注解的属性信息
         * @param applicationContext ApplicationContext
         * @return 创建出来的ReferenceBeanBuilder
         */
        @JvmStatic
        fun create(attributes: AnnotationAttributes, applicationContext: ApplicationContext): ReferenceBeanBuilder {
            return ReferenceBeanBuilder(attributes, applicationContext)
        }
    }
}