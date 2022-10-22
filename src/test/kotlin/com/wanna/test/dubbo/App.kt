package com.wanna.test.dubbo

import com.wanna.boot.autoconfigure.SpringBootApplication
import com.wanna.boot.runSpringApplication
import com.wanna.framework.scheduling.annotation.EnableScheduling
import com.wanna.spring.dubbo.config.AbstractConfig
import com.wanna.test.dubbo.service.UserServiceReferenceService

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
    val applicationContext = runSpringApplication<App>(*args)
    val configs = applicationContext.getBeansForType(AbstractConfig::class.java)

    val userServiceReferenceService = applicationContext.getBean(UserServiceReferenceService::class.java)
    println(userServiceReferenceService.userService.getUserById(1))
    println(applicationContext)
}