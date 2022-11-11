package com.wanna.spring.dubbo.common.compiler.support

import javax.tools.ToolProvider

/**
 * Jdk的JavaCode Compiler
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/11/7
 */
open class JdkCompiler : AbstractCompiler() {

    /**
     * 获取系统的JavaCompiler
     */
    private val javaCompiler = ToolProvider.getSystemJavaCompiler()

    override fun doCompile(code: String, classLoader: ClassLoader): Class<*> {
        TODO("Not yet implemented")
    }
}