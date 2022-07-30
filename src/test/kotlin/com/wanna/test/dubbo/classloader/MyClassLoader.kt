package com.wanna.test.dubbo.classloader

import com.wanna.framework.core.util.ClassUtils
import java.io.FileInputStream
import java.io.FileNotFoundException

class MyClassLoader(private val rootPath: String) : ClassLoader() {
    override fun loadClass(name: String): Class<*>? {
        if (!name.startsWith("com.wanna")) {
            return super.loadClass(name)
        }
        val shortName = ClassUtils.getShortName(name)
        val classFileName = "$rootPath/$shortName.class"
        try {
            val classFile: ByteArray
            FileInputStream(classFileName).use {
                classFile = it.readAllBytes()
            }
            return defineClass(name, classFile, 0, classFile.size)
        } catch (ex: FileNotFoundException) {
            return null
        }
    }
}