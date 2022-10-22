package com.wanna.test.dubbo.service

import com.wanna.spring.dubbo.annotation.DubboService

@DubboService(application = "WannaDubboProject", protocol = ["wannaProtocol"], registry = ["wannaRegistry"])
class UserServiceImpl : UserService {
    override fun getUserById(id: Int) = "wanna"
}