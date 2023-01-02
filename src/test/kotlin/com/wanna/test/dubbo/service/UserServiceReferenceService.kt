package com.wanna.test.dubbo.service

import com.wanna.framework.context.stereotype.Component
import com.wanna.spring.dubbo.annotation.DubboReference

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/22
 */
@Component
class UserServiceReferenceService {

    @DubboReference(
        injvm = true,
        interfaceClass = UserService::class,
        application = "WannaDubboProject",
        registry = ["wannaRegistry"]
    )
    var userService: UserService? = null

}