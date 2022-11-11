package com.wanna.spring.dubbo.common.compiler

/**
 * JavaCode的Compiler，提供对于JavaCode的编译器
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/11/7
 */
interface Compiler {

    /**
     * 编译JavaCode成为一个Class
     *
     * @param code javaCode
     * @param classLoader 需要加载生成的类的ClassLoader
     */
    fun compile(code: String, classLoader: ClassLoader): Class<*>
}