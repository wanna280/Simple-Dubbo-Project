package com.wanna.test.dubbo.scheduling

import com.wanna.framework.context.stereotype.Component
import com.wanna.framework.scheduling.annotation.Scheduled

//@Component
open class Scheduled {
    @Scheduled(fixedDelay = 5000)
    open fun task1() {
        println("111")
    }

    @Scheduled(fixedRate = 10000)
    open fun task2() {
        println("222")
    }
}
