package com.wanna.spring.dubbo.annotation

import com.wanna.framework.beans.BeanFactoryAware
import com.wanna.framework.beans.BeansException
import com.wanna.framework.beans.PropertyValues
import com.wanna.framework.beans.factory.BeanFactory
import com.wanna.framework.beans.factory.InjectionMetadata
import com.wanna.framework.beans.factory.config.ConfigurableListableBeanFactory
import com.wanna.framework.beans.factory.support.DisposableBean
import com.wanna.framework.beans.factory.support.definition.RootBeanDefinition
import com.wanna.framework.context.annotation.AnnotationAttributes
import com.wanna.framework.context.annotation.AnnotationAttributesUtils
import com.wanna.framework.context.aware.BeanClassLoaderAware
import com.wanna.framework.context.aware.EnvironmentAware
import com.wanna.framework.context.exception.BeanCreationException
import com.wanna.framework.context.processor.beans.InstantiationAwareBeanPostProcessor
import com.wanna.framework.context.processor.beans.MergedBeanDefinitionPostProcessor
import com.wanna.framework.core.Ordered
import com.wanna.framework.core.PriorityOrdered
import com.wanna.framework.core.annotation.AnnotatedElementUtils
import com.wanna.framework.core.environment.Environment
import com.wanna.framework.util.ReflectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.Throws

/**
 * Dubbo的抽象的注解处理器，用来完成Dubbo的注解的自动注入功能
 *
 * @see ReferenceAnnotationBeanPostProcessor
 * @see DubboReference
 *
 * @param annotationTypes 需要去进行处理的注解列表
 */
abstract class AbstractAnnotationBeanPostProcessor(private val annotationTypes: Array<Class<out Annotation>>) :
    MergedBeanDefinitionPostProcessor, BeanFactoryAware, EnvironmentAware, PriorityOrdered, BeanClassLoaderAware,
    InstantiationAwareBeanPostProcessor, DisposableBean {

    /**
     * Logger
     */
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * BeanFactory
     */
    private var beanFactory: ConfigurableListableBeanFactory? = null

    /**
     * ClassLoader
     */
    private var classLoader: ClassLoader? = null

    /**
     * Environment
     */
    private var environment: Environment? = null

    /**
     * Order
     */
    private var order: Int = Ordered.ORDER_LOWEST

    /**
     * InjectionMetadataCache，需要去完成注入；Key是beanName
     */
    private val injectionMetadataCache = ConcurrentHashMap<String, AnnotatedInjectionMetadata>(32)

    /**
     * 已经完成注入的对象Cache(Key是CacheKey，交给子类去进行生成)
     */
    private val injectedObjectsCache = ConcurrentHashMap<String, Any>(32)

    /**
     * 在处理MergedBeanDefinition时，提前去构建出来InjectionMetadata
     *
     * @param beanName beanName
     * @param beanType beanType
     * @param beanDefinition beanDefinition
     */
    override fun postProcessMergedBeanDefinition(
        beanDefinition: RootBeanDefinition,
        beanType: Class<*>,
        beanName: String
    ) {
        findInjectionMetadata(beanName, beanType, null)
    }

    /**
     * 对属性值去提供自动注入功能
     *
     * @param pvs 原始的PropertyValues
     * @param bean bean
     * @param beanName beanName
     * @return 经过处理之后的PropertyValues
     * @throws BeansException 如果处理注入的过程当中出现了异常的话
     */
    @Throws(BeanCreationException::class)
    override fun postProcessProperties(pvs: PropertyValues?, bean: Any, beanName: String): PropertyValues? {
        val metadata = findInjectionMetadata(beanName, bean::class.java, pvs)
        try {
            metadata.inject(bean, beanName, pvs)
        } catch (ex: BeanCreationException) {
            throw ex
        } catch (ex: Throwable) {
            throw BeanCreationException("处理[${annotationTypes.map { it.simpleName }}]的依赖注入失败", ex, beanName)
        }
        return pvs
    }

    /**
     * 从beanClass上去寻找需要去进行注入的元素
     *
     * @param beanName beanName
     * @param beanClass beanClass
     * @param pvs pvs
     */
    private fun findInjectionMetadata(beanName: String, beanClass: Class<*>, pvs: PropertyValues?): InjectionMetadata {
        var injectionMetadata = injectionMetadataCache[beanName]
        if (injectionMetadata == null) {
            injectionMetadata = injectionMetadataCache[beanName]
            if (injectionMetadata == null) {
                injectionMetadata = buildInjectionMetadata(beanClass)
                injectionMetadataCache[beanName] = injectionMetadata
            }
        }
        return injectionMetadata
    }

    /**
     * 根据beanClass去构建出来需要去进行注入的AnnotatedInjectionMetadata
     *
     * @param beanClass 待寻找注入元素的beanClass
     * @return 构建好的AnnotatedInjectionMetadata(字段、方法层面的元素的merge结果)
     */
    private fun buildInjectionMetadata(beanClass: Class<*>): AnnotatedInjectionMetadata {
        val fieldAnnotationMetadata = findFieldAnnotationMetadata(beanClass)
        val methodAnnotationMetadata = findMethodAnnotationMetadata(beanClass)
        return AnnotatedInjectionMetadata(beanClass, fieldAnnotationMetadata, methodAnnotationMetadata)
    }

    /**
     * 根据beanClass去构建出来需要去进行注入的字段元素
     *
     * @param beanClass beanClass
     * @return 从beanClass上需要去进行注入的字段元素列表
     */
    private fun findFieldAnnotationMetadata(beanClass: Class<*>): Collection<AnnotatedFieldElement> {
        val elements = ArrayList<AnnotatedFieldElement>()
        ReflectionUtils.doWithFields(beanClass) { field ->
            getAnnotationTypes().forEach { annotationType ->
                val annotation = AnnotatedElementUtils.getMergedAnnotation(field, annotationType) ?: return@forEach
                val attributes = AnnotationAttributesUtils.asAnnotationAttributes(annotation)!!

                if (Modifier.isStatic(field.modifiers)) {
                    if (logger.isWarnEnabled) {
                        logger.warn("@[${annotationType.name}]注解不支持标注在static字段[${field.name}]上")
                    }
                    return@forEach
                }
                elements += AnnotatedFieldElement(field, attributes)
            }
        }
        return elements
    }

    /**
     * 根据beanClass去构建出来需要去进行注入的方法元素
     *
     * @param beanClass beanClass
     * @return 从beanClass上需要去进行注入的方法元素列表
     */
    private fun findMethodAnnotationMetadata(beanClass: Class<*>): Collection<AnnotatedMethodElement> {
        val elements = ArrayList<AnnotatedMethodElement>()
        ReflectionUtils.doWithMethods(beanClass) { method ->
            getAnnotationTypes().forEach { annotationType ->
                val annotation = AnnotatedElementUtils.getMergedAnnotation(method, annotationType) ?: return@forEach
                val attributes = AnnotationAttributesUtils.asAnnotationAttributes(annotation)!!

                if (Modifier.isStatic(method.modifiers)) {
                    if (logger.isWarnEnabled) {
                        logger.warn("@[${annotationType.name}]注解不支持标注在static方法[${method.name}]上")
                    }
                    return@forEach
                }
                elements += AnnotatedMethodElement(method, attributes)
            }
        }
        return elements
    }

    /**
     * 获取到用来对给定的InjectedElement去提供注入的对象。
     * 如果缓存当中存在有的话，我们直接根据cacheKey去进行获取；
     * 如果缓存当中没有的话，我们交给子类去进行寻找，并加入到缓存当中。
     *
     * @param attributes 注解属性信息
     * @param bean bean
     * @param beanName beanName
     * @param injectedElement 描述一个用来进行注入的元素
     * @param injectedType 需要去进行注入的类型
     */
    protected open fun getInjectedObject(
        attributes: AnnotationAttributes,
        bean: Any,
        beanName: String,
        injectedType: Class<*>,
        injectedElement: InjectionMetadata.InjectedElement
    ): Any? {
        val cacheKey =
            buildInjectedObjectCacheKey(attributes, bean, beanName, injectedType, injectedElement)
        var injectedObject = injectedObjectsCache[cacheKey]
        if (injectedObject == null) {
            injectedObject = doGetInjectedBean(attributes, bean, beanName, injectedType, injectedElement)
            injectedObjectsCache[cacheKey] = injectedObject
        }
        return injectedObject
    }

    /**
     * 真正地去获取到需要去用来注入的对象
     *
     * @param attributes 注解属性信息
     * @param bean bean
     * @param beanName beanName
     * @param injectedElement 描述一个用来进行注入的元素
     * @param injectedType 需要去进行注入的类型
     */
    @Throws(Exception::class)
    protected abstract fun doGetInjectedBean(
        attributes: AnnotationAttributes,
        bean: Any,
        beanName: String,
        injectedType: Class<*>,
        injectedElement: InjectionMetadata.InjectedElement
    ): Any

    /**
     * 根据某一个要去进行注入的元素去构建出来用来添加到缓存当中的CacheKey
     *
     * @param attributes 注解属性信息
     * @param bean bean
     * @param beanName beanName
     * @param injectedElement 描述一个用来进行注入的元素
     * @param injectedType 需要去进行注入的类型
     */
    protected abstract fun buildInjectedObjectCacheKey(
        attributes: AnnotationAttributes,
        bean: Any,
        beanName: String,
        injectedType: Class<*>,
        injectedElement: InjectionMetadata.InjectedElement
    ): String

    override fun setBeanFactory(beanFactory: BeanFactory) {
        if (beanFactory is ConfigurableListableBeanFactory) {
            this.beanFactory = beanFactory
        } else {
            throw IllegalStateException("仅仅支持ConfigurableListableBeanFactory，但是给定的是[${beanFactory.javaClass.name}]")
        }
    }

    /**
     * 当Bean被摧毁时，需要清理掉内部的全部缓存
     */
    override fun destroy() {

        // 对所有的Bean去进行摧毁
        this.injectedObjectsCache.values.forEach {
            if (it is DisposableBean) {
                it.destroy()
            }
        }

        // clearCache
        this.injectedObjectsCache.clear()
        this.injectionMetadataCache.clear()
    }

    protected open fun getBeanFactory(): ConfigurableListableBeanFactory =
        this.beanFactory ?: throw IllegalStateException("BeanFactory不能为null")

    override fun setBeanClassLoader(classLoader: ClassLoader) {
        this.classLoader = classLoader
    }

    protected open fun getBeanClassLoader(): ClassLoader =
        this.classLoader ?: throw IllegalStateException("beanClassLoader不能为null")

    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    protected open fun getEnvironment(): Environment =
        this.environment ?: throw IllegalStateException("Environment不能为null")

    open fun setOrder(order: Int) {
        this.order = order
    }

    override fun getOrder() = this.order

    /**
     * 获取全部需要去进行处理的注解
     *
     * @return 全部需要去进行处理的注解列表
     */
    protected fun getAnnotationTypes(): Array<Class<out Annotation>> = this.annotationTypes

    /**
     * 将多个列表当中的InjectedElement去进行merge
     *
     * @param elements 待聚合的InjectedElement列表
     * @return 聚合得到的大的InjectedElement列表
     */
    private fun combine(vararg elements: Collection<InjectionMetadata.InjectedElement>): Collection<InjectionMetadata.InjectedElement> {
        val combined = ArrayList<InjectionMetadata.InjectedElement>()
        elements.forEach(combined::addAll)
        return combined
    }

    /**
     * 维护一个待去进行注入的类当中的元信息，因为我们需要对Field和Method去进行分类，因此我们重新实现了一个[InjectionMetadata]
     *
     * @param targetClass targetClass
     * @param fieldElements 待注入的字段元素列表
     * @param methodElements 待注入的方法元素列表
     */
    private inner class AnnotatedInjectionMetadata(
        targetClass: Class<*>,
        val fieldElements: Collection<AnnotatedFieldElement>,
        val methodElements: Collection<AnnotatedMethodElement>
    ) : InjectionMetadata(targetClass, combine(fieldElements, methodElements))

    /**
     * 一个提供方法注入的InjectedElement
     *
     * @param method 待注入的方法
     * @param attributes 寻找到的注解属性信息
     */
    private inner class AnnotatedMethodElement(
        private val method: Method,
        private val attributes: AnnotationAttributes
    ) : InjectionMetadata.InjectedElement(method) {

        var bean: Any? = null

        override fun getResourceToInject(bean: Any, beanName: String): Any? {
            return getInjectedObject(attributes, bean, beanName, method.returnType, this)
        }
    }

    /**
     * 一个提供字段注入的InjectedElement
     *
     * @param field 待注入的字段
     * @param attributes 注解的属性信息
     */
    private inner class AnnotatedFieldElement(private val field: Field, private val attributes: AnnotationAttributes) :
        InjectionMetadata.InjectedElement(field) {
        var bean: Any? = null

        override fun getResourceToInject(bean: Any, beanName: String): Any? {
            return getInjectedObject(attributes, bean, beanName, field.type, this)
        }
    }
}