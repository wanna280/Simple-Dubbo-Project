package com.wanna.spring.dubbo.annotation

/**
 * Bean的引用的处理器，负责处理@DubboReference注解的自动注入
 *
 * @see DubboReference
 * @see AbstractAnnotationBeanPostProcessor
 */
open class ReferenceAnnotationBeanPostProcessor : AbstractAnnotationBeanPostProcessor() {
    companion object {
        const val BEAN_NAME = "referenceAnnotationBeanPostProcessor"
    }
}