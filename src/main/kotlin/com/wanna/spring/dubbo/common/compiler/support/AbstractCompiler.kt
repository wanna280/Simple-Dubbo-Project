package com.wanna.spring.dubbo.common.compiler.support

import com.wanna.spring.dubbo.common.compiler.Compiler

/**
 * 抽象的JavaCode的Compiler的实现
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/11/7
 */
abstract class AbstractCompiler : Compiler {

    override fun compile(code: String, classLoader: ClassLoader): Class<*> {
        return doCompile(code, classLoader)
    }

    protected abstract fun doCompile(code: String, classLoader: ClassLoader): Class<*>
}