package com.wanna.spring.dubbo.remoting

/**
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
class RemotingException(message: String?, cause: Throwable?) : RuntimeException(message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(null, cause)
}