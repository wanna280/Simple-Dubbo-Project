package com.wanna.spring.dubbo.common

import com.wanna.spring.dubbo.remoting.Constants.GROUP_KEY
import com.wanna.spring.dubbo.remoting.Constants.INTERFACE_KEY
import com.wanna.spring.dubbo.remoting.Constants.VERSION_KEY
import com.wanna.spring.dubbo.common.utils.StringUtils
import java.io.Serializable
import java.net.*
import java.net.URL
import java.util.*

/**
 * Dubbo自定义实现的URL(统一资源定位符)
 * 例如：
 * * 1. registry://192.168.1.7:9090/org.apache.dubbo.service1?param1=value1&param2=value2
 * * 2. http://username:password@10.20.130.230:8080/list?version=1.0.0
 *
 * @author jianchao.jia
 * @version v1.0
 * @date 2022/10/23
 *
 * @param protocol 协议名称
 * @param username username
 * @param password password
 * @param host host
 * @param port port
 * @param path 相对根路径的路径，例如"/service"(如果以"/")作为开头，那么会自动去掉
 * @param parameters 参数列表
 * @param methodParameters 方法参数列表
 */
open class URL(
    private val protocol: String?,
    private val username: String?,
    private val password: String?,
    private val host: String?,
    port: Int,
    path: String?,
    private val parameters: Map<String, String>,
    private val methodParameters: Map<String, Map<String, String>>
) : Serializable {

    /**
     * port，不能为负
     */
    private val port: Int = maxOf(port, 0)

    /**
     * 路径(如果必要的话需要去掉前缀的"/")
     */
    private val path = when {
        path == null -> null
        path.startsWith("/") -> path.substring(0, path.length - 1)
        else -> path
    }

    /**
     * toString的结果
     */
    @Transient
    private var string: String? = null

    /**
     * ServiceKey
     */
    @Transient
    private var serviceKey = parameters[INTERFACE_KEY]

    /**
     * address("ip:port")
     */
    @Transient
    private var address: String? = null

    init {
        if (!StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
            throw IllegalStateException("指定了username时, password不能为空")
        }
        this.address = getAddress(host ?: throw IllegalArgumentException("host不能为null"), port)
    }

    constructor(protocol: String?, host: String?, port: Int, path: String?, parameters: Map<String, String>) : this(
        protocol, null, null, host, port, path, parameters,
        emptyMap()
    )

    constructor(protocol: String?, host: String?, port: Int, parameters: Map<String, String>) : this(
        protocol, null, null, host, port, null, parameters,
        emptyMap()
    )

    constructor(protocol: String?, host: String?, path: String?, port: Int) : this(
        protocol, null, null, host, port, path, emptyMap(),
        emptyMap()
    )

    constructor(protocol: String?, host: String?, port: Int) : this(
        protocol, null, null, host, port, null, emptyMap(),
        emptyMap()
    )

    protected constructor() : this(null, null, null, null, 0, null, emptyMap(), emptyMap())

    /**
     * 根据key去获取到对应的参数
     *
     * @param key 参数名
     * @return 获取到的参数对象(获取不到return null)
     */
    open fun getParameter(key: String): String? {
        return null
    }

    /**
     * 根据key去获取到对应的参数
     *
     * @param key 参数名
     * @param defaultValue 默认值
     * @return 获取到的参数值(获取不到返回默认值)
     */
    open fun getParameter(key: String, defaultValue: String): String {
        return defaultValue
    }

    /**
     * 根据key去获取到对应的参数
     *
     * @param key 参数名
     * @param defaultValue 默认值
     * @return 获取到的参数值(获取不到返回默认值)
     */
    open fun getParameter(key: String, defaultValue: Array<String>): Array<String> {
        return defaultValue
    }

    /**
     * 根据key去获取到对应的参数
     *
     * @param key 参数名
     * @param defaultValue 默认值
     * @return 获取到的参数值(获取不到返回默认值)
     */
    open fun getParameter(key: String, defaultValue: List<String>): List<String> {
        return defaultValue
    }

    open fun getParameter(key: String, defaultValue: Int): Int {
        return defaultValue
    }

    open fun getParameter(key: String, defaultValue: Boolean): Boolean {
        return defaultValue
    }

    open fun addParameterIfAbsent(key: String, value: String?): com.wanna.spring.dubbo.common.URL {
        return this
    }

    override fun toString(): String {
        if (string == null) {
            this.string = buildString(false, true, false, false, emptyArray())
        }
        return string!!
    }


    /**
     * 构建toString的结果
     */
    private fun buildString(
        appendUser: Boolean,
        appendParameter: Boolean,
        userIp: Boolean,
        useService: Boolean,
        parameters: Array<String>
    ): String {
        val builder = StringBuilder()
        if (!StringUtils.isEmpty(protocol)) {
            builder.append(protocol).append("://")
        }

        // 如果需要拼接username/password的话，那么拼接成为"username:password"的格式
        if (appendUser && !StringUtils.isEmpty(username)) {
            builder.append(username)
            if (!StringUtils.isEmpty(password)) {
                builder.append(":").append(password)
            }
        }

        // 如果必要的话，拼接host和port
        val host = if (userIp) getIp() else getHost()
        if (!StringUtils.isEmpty(host)) {
            builder.append(host)
            if (port > 0) {
                builder.append(":").append(port)
            }
        }
        // 拼接path
        val path = if (useService) getServiceKey() else getPath()
        if (!StringUtils.isEmpty(path)) {
            builder.append("/").append(path)
        }

        // 拼接参数
        if (appendParameter) {
            buildParameters(builder, true, parameters)
        }
        return builder.toString()
    }

    /**
     * 将当前URL转换成为一个Java的URL对象，toString的格式本身就是"protocol://host:port/serviceKey?k1=v1&k2=v2"的格式
     *
     * @return 转换生成的JavaURL对象
     * @throws IllegalStateException 如果URL格式不合法的话
     */
    @Throws(IllegalStateException::class)
    open fun toJavaURL(): URL {
        try {
            return URL(toString())
        } catch (ex: MalformedURLException) {
            throw IllegalStateException(ex.message, ex)
        }
    }

    /**
     * 将URL去转换成为InetSocketAddress
     *
     * @return 转换得到的InetSocketAddress
     */
    open fun toInetSocketAddress(): InetSocketAddress = InetSocketAddress(host, port)

    /**
     * 构建参数列表，并拼接到builder当中，最终形成的结构为"?k1=v1&k2=v2"
     *
     * @param parameters 如果为空数组，那么代表拼接所有的参数；如果不为空参数，那么按照给定的去进行构建
     * @param concat 是否需要在Builder当中添加一个"?"
     * @param builder 需要将参数添加到哪个builder当中？输出参数
     */
    private fun buildParameters(builder: StringBuilder, concat: Boolean, parameters: Array<String>) {
        if (getParameters().isNotEmpty()) {
            var first = true
            TreeMap(getParameters()).entries.forEach { (key, value) ->
                if (!StringUtils.isEmpty(key) && parameters.isEmpty() || parameters.contains(key)) {
                    if (first) {
                        if (concat) {
                            builder.append("?")
                        }
                        first = false
                    } else {
                        builder.append("&")
                    }
                    builder.append(key).append("=").append(value)
                }
            }
        }
    }

    /**
     * 获取ServiceKey
     *
     * @return ServiceKey
     */
    open fun getServiceKey(): String? {
        // 1.先尝试获取ServiceKey
        if (serviceKey != null) {
            return serviceKey!!
        }

        // 2.尝试获取"interface"参数
        val serviceInterface = getServiceInterface() ?: return null

        // 根据"interface"和"group"、"version"去生成Key
        return buildKey(serviceInterface, getParameter(GROUP_KEY), getParameter(VERSION_KEY))
    }


    /**
     * 获取ServiceInterface
     *
     * @return serviceInterfaceName
     */
    open fun getServiceInterface(): String? {
        return getParameter(INTERFACE_KEY, path ?: "")
    }

    /**
     * 为host和port生成address
     *
     * @param host host
     * @param port port
     * @return address
     */
    private fun getAddress(host: String, port: Int): String {
        return if (port <= 0) host else "$host:$port"
    }

    /**
     * 获取Address
     *
     * @return address("host:port")
     */
    open fun getAddress(): String {
        if (this.address == null) {
            this.address = getAddress(host ?: throw IllegalArgumentException("host不能为空"), port)
        }
        return this.address!!
    }

    /**
     * 根据host去获取IP
     *
     * @return IP
     */
    open fun getIp(): String? {
        return try {
            InetAddress.getByName(host).hostAddress
        } catch (ex: UnknownHostException) {
            host
        }
    }

    /**
     * 获取Parameters
     *
     * @return Parameters
     */
    open fun getParameters(): Map<String, String> = this.parameters

    /**
     * 方法级别的参数列表
     *
     * @return 方法级别的参数列表 methodName->(Key->Value)
     */
    open fun getMethodParameters(): Map<String, Map<String, String>> = this.methodParameters

    open fun getHost(): String? = this.host

    open fun getPath(): String? = this.path

    open fun getPort(): Int = this.port

    companion object {
        @JvmStatic
        fun buildKey(path: String?, group: String?, version: String?): String? {
            return "$path:$group:$version"
        }
    }
}