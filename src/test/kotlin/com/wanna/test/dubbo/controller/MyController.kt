package com.wanna.test.dubbo.controller

import com.wanna.framework.context.ApplicationContext
import com.wanna.framework.context.annotation.Autowired
import com.wanna.framework.context.annotation.Lazy
import com.wanna.framework.context.stereotype.Service
import com.wanna.framework.web.bind.annotation.CrossOrigin
import com.wanna.framework.web.bind.annotation.RequestMapping
import com.wanna.framework.web.bind.annotation.RequestMethod
import com.wanna.framework.web.bind.annotation.RestController
import com.wanna.framework.web.server.HttpServerResponse

@RestController
@CrossOrigin(methods = [RequestMethod.DELETE])
open class MyController {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @RequestMapping(["/user"])
    open fun getUser(response: HttpServerResponse): Any? {
        response.getCookies()
        println(javaClass.classLoader)
        return "11111"
    }
}

data class User(val id: Int, val name: String)

interface MyService {
    fun getUser(): User
}

@Service
class MyServiceImpl : MyService {
    override fun getUser() = User(666, "wanna")
}