package com.wanna.spring.dubbo.config

import com.wanna.spring.dubbo.rpc.model.ApplicationModel
import java.util.*

/**
 * 这是一个Dubbo的配置信息的公共抽象实现，它为所有的子类都去提供了init方法；
 * 所有的AbstractConfig的子类，在Spring容器启动完成时，都会负责将
 * 自己注册到Dubbo的ConfigManager当中
 *
 * @see com.wanna.spring.dubbo.config.context.ConfigManager
 */
abstract class AbstractConfig {

    // configId
    var id: String = UUID.randomUUID().toString()

    // config的前缀
    var prefix: String = ""

    /**
     * DubboConfig的初始化方法，负责将this注册到Dubbo的ConfigManager当中
     *
     * @see ApplicationModel.configManager
     */
    @javax.annotation.PostConstruct
    open fun addIntoConfigManager() {
        ApplicationModel.getConfigManager().addToConfig(this)
    }

    /**
     * 刷新配置信息
     */
    open fun refresh() {
        val environment = ApplicationModel.getEnvironment()

    }

    override fun toString() = "${javaClass}[id='$id']"

    companion object {
        // Dubbo的配置类的后缀名列表
        private val SUFFIXES = arrayOf("Config", "Bean", "ConfigBase")

        /**
         * 获取TagName，将一个类的后缀名去掉，比如ServiceConfig/ServiceConfigBase/ServiceBean，
         * 将后缀切割完成之后，都变成了Service，我们再转换成为小写去进行return
         *
         * @param clazz DubboConfigClass
         * @return tagName，比如"service"
         */
        @JvmStatic
        fun getTagName(clazz: Class<*>): String {
            val simpleName = clazz.simpleName
            SUFFIXES.forEach {
                if (simpleName.endsWith(it)) {
                    return simpleName.substring(0, simpleName.length - it.length).lowercase()
                }
            }
            throw IllegalStateException("不支持使用这样的[$clazz]去获取TagName，后缀必须为[${SUFFIXES.contentToString()}]之一")
        }
    }
}