package com.wanna.spring.dubbo.config.spring

import com.wanna.spring.dubbo.config.ReferenceConfig
import java.io.Serializable

/**
 * 将Dubbo的ReferenceConfig转接到Spring的ReferenceBean，
 * 描述的是DubboReference的去进行引用DubboService的Bean
 *
 * @see com.wanna.spring.dubbo.annotation.DubboReference
 */
open class ReferenceBean<T> : ReferenceConfig<T>(), Serializable {


}