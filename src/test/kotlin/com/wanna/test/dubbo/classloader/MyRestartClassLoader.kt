package com.wanna.test.dubbo.classloader

import org.slf4j.Logger
import java.net.URL
import java.net.URLClassLoader

class MyRestartClassLoader(urls: Array<URL>, parent: ClassLoader) :
    URLClassLoader(urls, parent) {
    override fun loadClass(name: String): Class<*> {
        if (!name.startsWith("com.wanna")) {
            return super.loadClass(name)
        }
        val classFileName = name.replace(".", "/") + ".class"
        val stream = getResourceAsStream(classFileName) ?: return super.loadClass(name)
        val classFile = stream.readAllBytes()
        // check LoadedClass Cache，如果不检查，很可能会导致多次defineClass导致抛出异常
        val findLoadedClass = findLoadedClass(name)
        if (findLoadedClass != null) {
            return findLoadedClass
        }
        return defineClass(name, classFile, 0, classFile.size)
    }
}