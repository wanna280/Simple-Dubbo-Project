package com.wanna.spring.dubbo.common.constants

/**
 *
 * 公共的常量
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
object CommonConstants {

    /**
     * ExecutorService组件的Key
     */
    const val EXECUTOR_SERVICE_COMPONENT_KEY = "java.util.concurrent.ExecutorService"

    /**
     * 区分是Provider、Consumer的Key
     */
    const val SIDE_KEY = "side"

    /**
     * 消费者端
     */
    const val CONSUMER_SIDE = "consumer"

    /**
     * 生产者端
     */
    const val PROVIDER_SIDE = "provider"
}