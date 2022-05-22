package me.qoomon.enhancements.kotlin

/**
 * @see [me.qoomon.examples.ArchUnitTestKt.getALL_PACKAGE_INTERNAL_ELEMENTS_SHOULD_BE_INTERNAL]
 * @see [me.qoomon.examples.ArchUnitTestKt.getNO_PACKAGE_INTERNAL_ELEMENTS_SHOULD_BE_ACCESSED_FROM_OUTSIDE_ITS_PACKAGE]
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
annotation class PackageInternal
