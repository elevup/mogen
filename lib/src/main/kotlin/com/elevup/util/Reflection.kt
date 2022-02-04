package com.elevup.util

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType

val <T : Any> KClass<T>.safeDeclaredMemberProperties: Collection<KProperty1<T, *>>
    get() = try {
        declaredMemberProperties
    } catch (e: UnsupportedOperationException) {
        emptyList()
    }


val <T : Any> KClass<T>.safeObjectInstance: T?
    get() = try {
        objectInstance
    } catch (e: Throwable) {
        null
    }


/**
 * Extracts all annotations from property
 */
fun KProperty<*>.getAnnotations(parent: KClass<*>): Iterable<Annotation> {
    val kotlinAnnotations = annotations
    val fieldAnnotations = javaField?.annotations ?: emptyArray()
    val constructorAnnotations = parent.constructors.mapNotNull {
        it.parameters.firstOrNull { it.name == name && it.annotations.isNotEmpty() }?.annotations
    }.flatten()

    return kotlinAnnotations + constructorAnnotations + fieldAnnotations
}

/**
 * Checks if given type is a function
 */
val Type.isFunctionType: Boolean
    get() = this is KCallable<*>
            || typeName.startsWith("kotlin.jvm.functions.")
            || (this is ParameterizedType && rawType.isFunctionType)

/**
 * Filters class properties removing functions and publicly invisible props
 */
val KClass<*>.exportableMemberProperties
    get() = safeDeclaredMemberProperties
        .filter { !it.returnType.javaType.isFunctionType }
        .filter { it.visibility == KVisibility.PUBLIC }
