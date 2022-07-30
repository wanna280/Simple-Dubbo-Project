package com.wanna.spring.dubbo.boot.autoconfigure

import com.wanna.boot.autoconfigure.condition.ConditionalOnMissingBean
import com.wanna.boot.context.properties.EnableConfigurationProperties
import com.wanna.framework.beans.factory.annotation.Qualifier
import com.wanna.framework.context.annotation.Bean
import com.wanna.spring.dubbo.annotation.EnableDubboConfig
import com.wanna.spring.dubbo.annotation.ServiceClassPostProcessor
import com.wanna.spring.dubbo.boot.autoconfigure.DubboRelaxedBindingAutoConfiguration.DubboBasePackages

/**
 * Dubbo针对SpringBoot的自动配置类
 *
 * ## Note:
 *
 * 对于要导入一个BeanFactoryPostProcessor/BeanPostProcessor的Bean时，都不能去注入Properties，
 * 因为此时BeanPostProcessor并未准备好，就看你导致Properties当中的字段根本就不能去进行赋值
 */
@EnableDubboConfig  // 开启Dubbo配置的支持，支持在配置文件当中去配置相关的属性，即可自动添加Dubbo配置类
@EnableConfigurationProperties([DubboConfigurationProperties::class])   // 导入DubboConfigurationProperties
open class DubboAutoConfiguration {

    /**
     * 注意，这里不能去注入DubboConfigurationProperties，因为它是一个BeanFactoryPostProcessor，
     * 实例化时机很早，这个时候BeanPostProcessor根本还没完成实例化，如果我们在这里直接就去注入了该Properties对象，
     * 就会导致BeanPostProcessor不能apply给DubboConfigurationProperties，从而导致该属性的功能不能生效，
     * 因为@ConfigurationProperties，是被BeanPostProcessor所处理的，因此我们这里采用的是，不动用Properties配置文件，
     * 而是直接使用DubboBasePackages这样的一个Bean，让它直接去Environment去进行属性值的解析工作
     */
    @Bean
    @ConditionalOnMissingBean
    open fun serviceClassPostProcessor(@Qualifier("dubboBasePackages") dubboBasePackages: DubboBasePackages): ServiceClassPostProcessor {
        return ServiceClassPostProcessor(dubboBasePackages.packages)
    }
}