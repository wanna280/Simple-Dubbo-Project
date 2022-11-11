package com.wanna.test.dubbo

import com.wanna.boot.test.context.SpringBootTest
import com.wanna.framework.context.ApplicationContext
import com.wanna.framework.context.annotation.Autowired
import com.wanna.framework.context.stereotype.Component
import com.wanna.framework.test.context.event.ApplicationEvents
import com.wanna.framework.test.context.event.RecordApplicationEvents
import com.wanna.framework.test.context.event.annotation.AfterTestMethod
import org.junit.jupiter.api.Test

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/11/6
 */
@RecordApplicationEvents
@SpringBootTest
class AppJUnitTest {

    @Autowired
    private var applicationContext: ApplicationContext? = null

    @Autowired
    private var applicationEvents: ApplicationEvents? = null

    @AfterTestMethod
    fun listener() {

    }


    @Test
    fun test() {
        applicationContext!!.getParent()
        println(applicationContext)
    }
}