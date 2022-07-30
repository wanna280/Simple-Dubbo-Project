package com.wanna.spring.dubbo.boot.autoconfigure

import com.wanna.boot.context.properties.ConfigurationProperties
import com.wanna.boot.context.properties.NestedConfigurationProperty
import com.wanna.spring.dubbo.config.*

/**
 * 维护Dubbo的配置信息，实际上并不需要这个类当中的很多Dubbo的配置的存在，Dubbo的配置都是可以生效的。
 *
 * ## Note:
 *
 * 因为Dubbo的本身的实现当中，就是支持在配置文件当中去进行配置的，这里@ConfigurationProperties并未生效，
 * 而只是让Spring的注解处理器能生成配置文件，方便IDE去进行智能提示(但是目前我们并未实现该功能)
 */
@ConfigurationProperties("dubbo")
open class DubboConfigurationProperties {
    @NestedConfigurationProperty
    var scan: Scan? = null

    @NestedConfigurationProperty
    var registry = RegistryConfig()

    @NestedConfigurationProperty
    var protocol = ProtocolConfig()

    @NestedConfigurationProperty
    var application = ApplicationConfig()

    @NestedConfigurationProperty
    var provider = ProviderConfig()

    @NestedConfigurationProperty
    var consumer = ConsumerConfig()

    open class Scan {
        var basePackages: Set<String> = emptySet()
    }
}