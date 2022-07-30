package com.wanna.test.dubbo

import com.wanna.boot.autoconfigure.SpringBootApplication
import com.wanna.boot.runSpringApplication
import com.wanna.framework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(proxyBeanMethods = false)
open class App

class Main {
    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            val applicationContext = runSpringApplication<App>(*args)
            println("Wanna-$applicationContext")
        }
    }
}

fun main(vararg args: String) {
    runSpringApplication<App>(*args)
}