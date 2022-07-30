package com.wanna.test.dubbo.config

import com.alibaba.druid.pool.DruidDataSource
import com.wanna.framework.context.annotation.Bean
import com.wanna.framework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
class DataSourceConfig {

    @Bean
    fun dataSource() : DataSource {
        return DruidDataSource()
    }
}