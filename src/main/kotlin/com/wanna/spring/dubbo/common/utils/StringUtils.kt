package com.wanna.spring.dubbo.common.utils

/**
 * 字符串工具类
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 */
object StringUtils {

    @JvmStatic
    fun isEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }

    @JvmStatic
    fun isNotEmpty(str: String?):Boolean = !isEmpty(str)
}