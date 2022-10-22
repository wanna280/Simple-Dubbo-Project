package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.factory.InjectionMetadata
import com.wanna.framework.beans.factory.support.DisposableBean
import com.wanna.framework.context.ApplicationContext
import com.wanna.framework.context.ApplicationContextAware
import com.wanna.framework.context.annotation.AnnotationAttributes
import com.wanna.framework.core.environment.Environment
import com.wanna.framework.util.ReflectionUtils
import com.wanna.framework.util.StringUtils
import com.wanna.spring.dubbo.config.spring.ReferenceBean
import com.wanna.spring.dubbo.config.spring.ServiceBean
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 * Bean的引用的处理器，负责处理@DubboReference注解的自动注入
 *
 * @see DubboReference
 * @see AbstractAnnotationBeanPostProcessor
 */
open class ReferenceAnnotationBeanPostProcessor
    : AbstractAnnotationBeanPostProcessor(
    arrayOf(
        DubboReference::class.java
    )
), ApplicationContextAware {
    companion object {

        /**
         * 处理Reference注解的BeanPostProcessor的beanName
         */
        const val BEAN_NAME = "referenceAnnotationBeanPostProcessor"
    }

    /**
     * ApplicationContext
     */
    private var applicationContext: ApplicationContext? = null

    /**
     * 要去进行注入的@DubboReference的字段的ReferenceBean的缓存
     * (Key是要去进行注入的字段/方法，Value是该字段/方法生成的ReferenceBean)
     */
    private val injectedFieldReferenceBeanCache =
        ConcurrentHashMap<InjectionMetadata.InjectedElement, ReferenceBean<*>>()

    /**
     * 要去进行注入的@DubboReference的方法的ReferenceBean的缓存
     * (Key是要去进行注入的字段/方法，Value是该字段/方法生成的ReferenceBean)
     */
    private val injectedMethodReferenceBeanCache =
        ConcurrentHashMap<InjectionMetadata.InjectedElement, ReferenceBean<*>>()

    /**
     * ReferenceBean的缓存，Key是ReferenceBeanName
     */
    private var referenceBeanCache = ConcurrentHashMap<String, ReferenceBean<*>>()

    /**
     * ReferenceBeanName->InvocationHandler的缓存
     */
    private val referencedBeanInvocationHandlersCache = ConcurrentHashMap<String, InvocationHandler>()


    /**
     * 为@DubboReference去提供用来注入的Bean
     *
     * @param attributes @DubboReference注解的属性信息
     * @param bean bean 请求去进行注入的Bean
     * @param beanName beanName 请求去进行注入的beanName
     * @param injectedElement 描述一个需要去进行注入的元素(字段/方法)
     * @param injectedType 需要去进行注入的类(DubboService的接口类型)
     */
    override fun doGetInjectedBean(
        attributes: AnnotationAttributes,
        bean: Any,
        beanName: String,
        injectedType: Class<*>,
        injectedElement: InjectionMetadata.InjectedElement
    ): Any {

        // 生成需要被引用的Dubbo服务的beanName("{interfaceName}#{group}#{version}")，对应的是注册的ServiceBean的beanName
        // 对于@DubboService注册的ServiceBean，也会采用这种方式去生成beanName
        val referencedBeanName = buildReferencedBeanName(attributes, injectedType)

        // ReferenceBean的beanName
        val referenceBeanName = injectedType.name

        // 根据@DubboReference注解属性，去构建出来ReferenceBean对象
        val referenceBean = buildReferenceBeanIfAbsent(referenceBeanName, attributes, injectedType)

        // 检查它是否要引用本地的Dubbo服务？(如果本地恰好有该ServiceBean，并且@DubboReference当中恰好配置了"injvm=true")
        val localServiceBean = isLocalServiceBean(referencedBeanName, referenceBean, attributes)

        // 将构建出来的ReferenceBean对象，使用单例对象的方式直接去注册到Spring BeanFactory当中
        registerReferenceBean(referencedBeanName, attributes, referenceBean, localServiceBean, injectedType)

        // 将要去进行注入的@DubboReference注解去进行缓存起来(Key是要去进行注入的字段/方法，Value是该字段/方法生成的ReferenceBean)
        cacheInjectedReferenceBean(referenceBean, injectedElement)

        // 创建一个代理对象去进行返回，这样@DubboReference注入的就是一个引用的代理对象了
        return getOrCreateProxy(referencedBeanName, referenceBean, localServiceBean, injectedType)
    }

    /**
     * 注册一个ReferenceBean到Spring容器当中？
     *
     * @param referenceBean ReferenceBean
     * @param referencedBeanName referenceBeanName
     * @param attributes @DubboReference注解的属性信息
     * @param localServiceBean 是否需要去引用一个本地的Dubbo服务？
     * @param serviceInterfaceType Dubbo服务接口类型
     */
    private fun registerReferenceBean(
        referencedBeanName: String,
        attributes: AnnotationAttributes,
        referenceBean: ReferenceBean<*>,
        localServiceBean: Boolean,
        serviceInterfaceType: Class<*>
    ) {
        val beanFactory = getBeanFactory()

        // 1.如果配置了"id"属性，那么我们直接使用id作为beanName
        // 2.如果没有配置id，那么使用@DubboReference注解的所有属性，拼接上interfaceClassName去作为beanName
        val beanName = getReferenceBeanName(attributes, serviceInterfaceType)

        // 将ReferenceBean去注册到BeanFactory当中
        beanFactory.registerSingleton(beanName, referenceBean)
    }

    /**
     * 根据@DubboReference注解当中的属性，去获取到ReferenceBean的beanName
     *
     * @param attributes @DubboReference注解的属性信息
     * @param interfaceClass 要引用的Dubbo服务的接口
     * @return 最终得到的beanName(如果配置了id，那么使用id；不然根据注解配置去进行生成)
     */
    private fun getReferenceBeanName(attributes: AnnotationAttributes, interfaceClass: Class<*>): String {
        // 1.如果配置了id属性，那么直接使用id去作为beanName
        val beanName = attributes.getString("id")
        if (StringUtils.hasText(beanName)) {
            return beanName
        }
        // 2.如果没有配置id属性，那么我们直接去进行生成
        return generateReferenceBeanName(attributes, interfaceClass)
    }

    /**
     * 根据@DubboReference注解当中的属性，去获取到ReferenceBean的beanName
     *
     * @param attributes @DubboReference注解的属性信息
     * @param interfaceClass 要引用的Dubbo服务的接口
     * @return 生成得到的beanName(@DubboReference注解的所有属性，拼接上interfaceClassName)
     */
    private fun generateReferenceBeanName(attributes: AnnotationAttributes, interfaceClass: Class<*>): String {
        val referenceBeanNameBuilder = StringBuilder("@DubboReference(")

        // 利用@DubboReference注解上的全部属性去生成beanName
        attributes.entries.forEach { referenceBeanNameBuilder.append(it.key).append("=").append(it.value).append(",") }

        // 把最后一个逗号去掉，替换成为")"
        referenceBeanNameBuilder.setCharAt(referenceBeanNameBuilder.lastIndexOf(','), ')')

        // 拼接上Dubbo接口名
        referenceBeanNameBuilder.append(" ").append(interfaceClass.name)

        return referenceBeanNameBuilder.toString()
    }

    /**
     * 将ReferenceBean去保存到缓存当中(Key是要去进行注入的字段/方法，Value是该字段/方法生成的ReferenceBean)
     *
     * @param referenceBean ReferenceBean
     * @param injectedElement 需要去进行注入的元素
     */
    private fun cacheInjectedReferenceBean(
        referenceBean: ReferenceBean<*>,
        injectedElement: InjectionMetadata.InjectedElement
    ) {
        if (injectedElement.isField) {
            injectedFieldReferenceBeanCache[injectedElement] = referenceBean
        } else {
            injectedMethodReferenceBeanCache[injectedElement] = referenceBean
        }
    }

    /**
     * 为给定的需要去进行注入的@DubboReference方法/字段，去获取/创建一个代理对象去进行注入
     *
     * @param referenceBean ReferenceBean
     * @param referencedBeanName 需要去引用的Dubbo服务的beanName
     * @param localServiceBean 是否需要去引用一个本地的Dubbo服务？
     * @param serviceInterfaceType Dubbo服务接口类型
     */
    private fun getOrCreateProxy(
        referencedBeanName: String,
        referenceBean: ReferenceBean<*>,
        localServiceBean: Boolean,
        serviceInterfaceType: Class<*>
    ): Any {
        // 如果是Dubbo的本地引用的话，那么我们直接使用JDK动态代理去指向对应的@DubboService的Bean
        if (localServiceBean) {
            return Proxy.newProxyInstance(
                getBeanClassLoader(),
                arrayOf(serviceInterfaceType),
                newReferencedBeanInvocationHandler(referencedBeanName)
            )
        }

        // 如果不是Dubbo的本地引用的话，那么我们需要使用ReferenceBean去进行引用真正的远程服务...
        exportServiceBeanIfNecessary(referencedBeanName)
        return referenceBean.get()!!
    }

    /**
     * 如果必要的话，先去暴露本地的Dubbo服务接口
     *
     * @param referencedBeanName 要去引用的Dubbo服务的接口
     */
    private fun exportServiceBeanIfNecessary(referencedBeanName: String) {
        if (existsServiceBean(referencedBeanName)) {
            val serviceBean = getApplicationContext().getBean(referencedBeanName, ServiceBean::class.java)
            if (!serviceBean.isExported()) {
                serviceBean.export()
            }
        }
    }

    /**
     * 创建一个针对当前的ReferenceBeanName的InvocationHandler
     *
     * @param referencedBeanName ReferenceBeanName
     * @return 创建出来的InvocationHandler
     */
    private fun newReferencedBeanInvocationHandler(referencedBeanName: String): InvocationHandler {
        return referencedBeanInvocationHandlersCache.computeIfAbsent(referencedBeanName) {
            ReferencedBeanInvocationHandler(referencedBeanName)
        }
    }

    /**
     * 判断是否需要去引用一个本地的Dubbo服务？如果Reference注解指定了"injvm=true"，并且恰好本地也有该服务的Bean，那么正好可以引用本地的服务
     *
     * @param referencedBeanName ReferenceBeanName
     * @param attributes @DubboReference注解的属性
     * @param referenceBean ReferenceBeanName对应的ReferenceBean
     */
    private fun isLocalServiceBean(
        referencedBeanName: String,
        referenceBean: ReferenceBean<*>,
        attributes: AnnotationAttributes
    ): Boolean {
        return existsServiceBean(referencedBeanName) && isRemoteReferenceBean(referenceBean, attributes)
    }

    /**
     * 判断给定的ServiceBean是否在当前的应用当中？
     *
     * @param referenceBeanName ReferenceBeanName
     * @return 如果当前ApplicationContext当中已经包含了该beanName的Bean，那么return true；否则return false
     */
    private fun existsServiceBean(referenceBeanName: String): Boolean {
        val applicationContext = getApplicationContext()
        return applicationContext.containsBeanDefinition(referenceBeanName)
                && applicationContext.isTypeMatch(referenceBeanName, ServiceBean::class.java)
    }

    /**
     * 判断一个ReferenceBean是否要引用远程的Dubbo服务？
     *
     * @param referenceBean ReferenceBean
     * @param attributes @DubboReference注解的属性信息
     * @return 如果需要引用远程服务，那么return true；否则return false
     */
    private fun isRemoteReferenceBean(referenceBean: ReferenceBean<*>, attributes: AnnotationAttributes): Boolean {
        return !referenceBean.injvm || !attributes.getBoolean("injvm")
    }

    /**
     * 根据@DubboReference注解去构建出来一个ReferenceBean
     *
     * @param referenceBeanName ReferenceBeanName
     * @param attributes @DubboReference注解的属性
     * @param serviceInterfaceType Dubbo服务接口
     */
    private fun buildReferenceBeanIfAbsent(
        referenceBeanName: String,
        attributes: AnnotationAttributes,
        serviceInterfaceType: Class<*>
    ): ReferenceBean<*> {
        var referenceBean = referenceBeanCache[referenceBeanName]
        if (referenceBean == null) {
            referenceBean =
                ReferenceBeanBuilder.create(attributes, getApplicationContext())
                    .interfaceClass(serviceInterfaceType)
                    .build()
            referenceBeanCache[referenceBeanName] = referenceBean
        }
        return referenceBean
    }

    /**
     * 为指定的需要去进行注入的元素去构建出来CacheKey
     *
     * @param attributes 注解属性信息
     * @param bean bean
     * @param beanName beanName
     * @param injectedElement 描述一个用来进行注入的元素
     * @param injectedType 需要去进行注入的类(DubboService的接口类型)
     */
    override fun buildInjectedObjectCacheKey(
        attributes: AnnotationAttributes,
        bean: Any,
        beanName: String,
        injectedType: Class<*>,
        injectedElement: InjectionMetadata.InjectedElement
    ): String {
        return buildReferencedBeanName(attributes, injectedType) +
                "#${injectedElement.member}#" +
                getAttributes(attributes, getEnvironment())
    }

    /**
     * 为DubboService去生成一个引用的DubboReference的BeanName
     *
     * @param attributes attributes
     * @param serviceInterfaceType Dubbo服务接口类型
     * @return 生成的beanName("{interfaceName}#{group}#{version}")
     */
    private fun buildReferencedBeanName(attributes: AnnotationAttributes, serviceInterfaceType: Class<*>): String {
        return ServiceBeanNameBuilder.create(attributes, serviceInterfaceType, getEnvironment()).build()
    }

    /**
     * 获取Attributes
     *
     * @param attributes attributes
     */
    private fun getAttributes(attributes: AnnotationAttributes, environment: Environment): Map<String, String> {
        return attributes.entries.associate { (key, value) -> key to value.toString() }
    }

    /**
     * 设置ApplicationContext
     *
     * @param applicationContext ApplicationContext
     */
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    open fun getApplicationContext(): ApplicationContext =
        this.applicationContext ?: throw IllegalStateException("ApplicationContext不能为null")

    /**
     * 当Bean摧毁时，需要把所有的Cache给清空掉
     */
    override fun destroy() {
        super.destroy()
        this.referenceBeanCache.clear()
        this.injectedFieldReferenceBeanCache.clear()
        this.injectedMethodReferenceBeanCache.clear()
        this.referencedBeanInvocationHandlersCache.clear()

        if (logger.isInfoEnabled) {
            logger.info("[${this.javaClass}]正在被摧毁...")
        }
    }

    /**
     * 处理Dubbo的本地服务引用的InvocationHandler，负责生成@DubboReference注解需要注入的对象的代理对象；
     * 在运行时拦截该对象的所有方法，直接转交给对应的ServiceBean的ref对象去真正执行目标方法
     *
     * @param referencedBeanName 要去进行引用的本地服务的beanName
     */
    private inner class ReferencedBeanInvocationHandler(private val referencedBeanName: String) : InvocationHandler {

        /**
         * 找到的ServiceBean的ref对象
         *
         * @see ServiceBean.ref
         */
        private var bean: Any? = null

        override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
            try {
                if (this.bean == null) {
                    init()
                }
                ReflectionUtils.makeAccessible(method)
                return ReflectionUtils.invokeMethod(method, bean, *(args ?: emptyArray()))
            } catch (ex: InvocationTargetException) {
                throw ex.targetException  // rethrow
            }
        }

        private fun init() {
            val serviceBean = getApplicationContext().getBean(referencedBeanName, ServiceBean::class.java)
            this.bean =
                serviceBean.ref ?: throw IllegalStateException("无法从beanName=[$referencedBeanName]的ServiceBean当中获取到ref")
        }
    }
}