package com.wanna.test.dubbo.endpoint

import com.wanna.boot.actuate.endpoint.annotation.Endpoint
import com.wanna.boot.actuate.endpoint.annotation.ReadOperation
import com.wanna.boot.actuate.endpoint.annotation.Selector
import com.wanna.boot.actuate.endpoint.web.annotation.RestControllerEndpoint
import com.wanna.framework.context.stereotype.Component
import com.wanna.framework.lang.Nullable
import com.wanna.framework.web.bind.annotation.RequestMapping

class Endpoint

@Component
@Endpoint("my")
open class MyActuator {
    @ReadOperation
    open fun read(@Nullable id: Int?, @Selector name: String): String {
        return "-- $id -- $name"
    }
}

@Component
@RestControllerEndpoint("/user")
open class MyControllerEndpoint {
    @RequestMapping(path = ["/get"])
    open fun get(): Any {
        return "wanna"
    }
}