package com.wanna.test.dubbo

import com.wanna.boot.autoconfigure.SpringBootApplication
import com.wanna.boot.context.properties.ConfigurationProperties
import com.wanna.boot.context.properties.ConstructorBinding
import com.wanna.boot.context.properties.EnableConfigurationProperties
import com.wanna.boot.runSpringApplication
import com.wanna.framework.context.annotation.Autowired
import com.wanna.framework.context.annotation.Bean
import com.wanna.framework.context.processor.beans.BeanPostProcessor
import com.wanna.framework.scheduling.annotation.EnableScheduling
import com.wanna.framework.scheduling.concurrent.ThreadPoolTaskExecutor
import com.wanna.spring.dubbo.config.AbstractConfig
import com.wanna.test.dubbo.service.UserServiceReferenceService

@EnableConfigurationProperties([Properties::class])
@EnableScheduling
@SpringBootApplication(proxyBeanMethods = false)
open class App {

    @Autowired
    private lateinit var app: App

    @Bean
    open fun threadPool(): ThreadPoolTaskExecutor {
        return ThreadPoolTaskExecutor()
    }

    @Autowired
    private var list: Map<String, BeanPostProcessor>? = null

}

@ConfigurationProperties("user")
data class Properties @ConstructorBinding constructor(val id: Int, val name: String)

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
    var args = arrayOf(*args, "--user.id=1", "--user.name=2")
    val applicationContext = runSpringApplication<App>(*args)
    val configs = applicationContext.getBeansForType(AbstractConfig::class.java)

    val userServiceReferenceService = applicationContext.getBean(UserServiceReferenceService::class.java)
    println(userServiceReferenceService.userService?.getUserById(1))
    println(applicationContext)
}