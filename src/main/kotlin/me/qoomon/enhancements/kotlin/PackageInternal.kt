package me.qoomon.enhancements.kotlin

import kotlin.annotation.AnnotationRetention.BINARY

/**
 * @see [me.qoomon.enhancements.archunit.RulesKt]
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
)
@Retention(BINARY)
annotation class PackageInternal
