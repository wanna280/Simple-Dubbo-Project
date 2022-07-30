package com.wanna.test.dubbo.service

import com.wanna.spring.dubbo.annotation.DubboService

@DubboService(application = "application", protocol = ["protocol"], registry = ["registry"])
class UserServiceImpl : UserService {

}