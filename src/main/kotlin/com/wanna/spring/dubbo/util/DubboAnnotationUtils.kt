package com.wanna.spring.dubbo.util

import com.wanna.framework.context.annotation.AnnotationAttributes
import com.wanna.framework.core.util.ClassUtils
import com.wanna.framework.core.util.StringUtils

/**
 * Dubbo的注解工具类
 */
object DubboAnnotationUtils {
    /**
     * 从注解属性当中去决策出来DubboService的接口类
     *
     * @param attributes @DubboService的注解信息
     * @param defaultInterfaceClass 标注了@dubboService的类
     * @return 解析到的DubboService要使用的具体接口
     */
    @JvmStatic
    fun resolveServiceInterfaceClass(attributes: AnnotationAttributes, defaultInterfaceClass: Class<*>): Class<*> {
        val classLoader = defaultInterfaceClass.classLoader
        // 1.获取interfaceClass
        var interfaceClass: Class<*>? = attributes.getClass("interfaceClass")
        if (interfaceClass == Void::class.java) {
            interfaceClass = null
        }
        // 2.获取interfaceClassName
        val interfaceClassName = attributes.getString("interfaceClassName")
        if (interfaceClass == null && StringUtils.hasText(interfaceClassName)) {
            interfaceClass = ClassUtils.forName<Any>(interfaceClassName, classLoader)
        }
        // 3.获取一个类的所有接口，如果存在有多个，那么直接返回数组当中的第一个接口
        val allInterfaces = ClassUtils.getAllInterfacesForClass(defaultInterfaceClass)
        if (interfaceClass == null && allInterfaces.isNotEmpty()) {
            interfaceClass = allInterfaces[0]
        }
        interfaceClass ?: throw IllegalStateException("无法为@DubboService[$defaultInterfaceClass]找到合适的接口")
        if (!interfaceClass.isInterface) {
            throw IllegalStateException("从[$defaultInterfaceClass]当中解析到的类[$interfaceClass]不是一个接口")
        }
        return interfaceClass
    }
}