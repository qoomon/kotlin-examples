package me.qoomon.enhancements.archunit

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaConstructor
import com.tngtech.archunit.core.domain.JavaField
import com.tngtech.archunit.core.domain.JavaMember
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.core.domain.JavaPackage
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.jvm.kotlinProperty

fun haveValidPackageInternalAnnotations(
    packageInternalAnnotation: KClass<out Annotation>,
): ArchCondition<JavaClass> =
    object : ArchCondition<JavaClass>("have valid package internal annotations") {
        override fun check(javaClass: JavaClass, events: ConditionEvents) {
            if (javaClass.isAnnotatedWith(packageInternalAnnotation.java)) {
                events.add(
                    SimpleConditionEvent(
                        javaClass,
                        javaClass.isInternal(),
                        "${javaClass.description} in ${javaClass.sourceCodeLocation}",
                    ),
                )
            }
            javaClass.members.forEach {
                if (it.isAnnotatedWith(packageInternalAnnotation.java)) {
                    events.add(
                        SimpleConditionEvent(
                            it,
                            it.isInternal(),
                            "${it.description} in ${it.sourceCodeLocation}",
                        ),
                    )
                }
            }
        }

        private fun JavaClass.isInternal() = try {
            reflect().kotlin.visibility == KVisibility.INTERNAL
        } catch (ex: UnsupportedOperationException) {
            false
        }

        private fun JavaMember.isInternal(): Boolean = try {
            when (val member = reflect()) {
                is Constructor<*> -> member.kotlinFunction?.visibility == KVisibility.INTERNAL
                is Field -> member.kotlinProperty?.visibility == KVisibility.INTERNAL
                is Method -> member.kotlinFunction?.visibility == KVisibility.INTERNAL
                else -> throw NotImplementedError("isInternal is not implemented for ${this::class.java}")
            }
        } catch (ex: UnsupportedOperationException) {
            false
        }
    }

fun notAccessPackageInternalElementsFromOutside(
    packageInternalAnnotation: KClass<out Annotation>,
): ArchCondition<JavaClass> =
    object : ArchCondition<JavaClass>("not access package internal elements from outside") {
        override fun check(javaClass: JavaClass, events: ConditionEvents) {
            javaClass.directDependenciesFromSelf
                .filter {
                    it.targetClass.isAnnotatedWith(packageInternalAnnotation.java) ||
                        it.targetClass.anyParentIsAnnotatedWith(packageInternalAnnotation.java)
                }
                .forEach {
                    val targetIsAnnotated = it.targetClass.isAnnotatedWith(packageInternalAnnotation.java)
                    val parentIsAnnotated = it.targetClass.anyParentIsAnnotatedWith(packageInternalAnnotation.java)
                    if (targetIsAnnotated || parentIsAnnotated) {
                        val isPackageInternalAccess = it.targetClass.isPartOf(it.originClass.`package`)
                        events.add(
                            SimpleConditionEvent(
                                it,
                                isPackageInternalAccess,
                                it.description.let { description ->
                                    if (targetIsAnnotated) {
                                        description
                                    } else {
                                        "$description - a parent class is package internal"
                                    }
                                },
                            ),
                        )
                    }
                }

            javaClass.accessesFromSelf
                .filterNot {
                    it.target.owner.anyParentIsAnnotatedWith(packageInternalAnnotation.java)
                }
                .forEach { access ->
                    val origin = access.origin
                    val target = access.target
                    val targetIsAnnotated = target.isAnnotatedWith(packageInternalAnnotation.java)
                    val parentIsAnnotated = target.resolveMember().orElse(null).let { member ->
                        when (member) {
                            is JavaConstructor -> member.anyParentIsAnnotatedWith(packageInternalAnnotation.java)
                            is JavaField -> member.anyParentIsAnnotatedWith(packageInternalAnnotation.java)
                            is JavaMethod -> member.anyParentIsAnnotatedWith(packageInternalAnnotation.java)
                            else -> false
                        }
                    }

                    if (targetIsAnnotated || parentIsAnnotated) {
                        val isSelfAccess = target.owner == origin.owner
                        val isPackageInternalAccess = target.owner.isPartOf(origin.owner.`package`)
                        val isConditionSatisfied = isSelfAccess || isPackageInternalAccess
                        events.add(
                            SimpleConditionEvent(
                                access,
                                isConditionSatisfied,
                                access.description.let { description ->
                                    if (targetIsAnnotated) {
                                        description
                                    } else {
                                        "$description - a parent definition is package internal"
                                    }
                                },
                            ),
                        )
                    }
                }
        }

        private fun JavaClass.anyParentIsAnnotatedWith(annotationType: Class<out Annotation>) =
            allRawSuperclasses.any { it.isAnnotatedWith(annotationType) }
    }

fun beInternal(): ArchCondition<JavaClass> =
    object : ArchCondition<JavaClass>("should be internal") {
        override fun check(item: JavaClass, events: ConditionEvents) {
            events.add(
                SimpleConditionEvent(
                    item,
                    item.reflect().isInternal(),
                    "${item.description} in ${item.sourceCodeLocation}",
                ),
            )
        }

        private fun Class<*>.isInternal() = try {
            this.kotlin.visibility == KVisibility.INTERNAL
        } catch (ex: UnsupportedOperationException) {
            false
        }
    }

fun beInternalMember(): ArchCondition<JavaMember> =
    object : ArchCondition<JavaMember>("should be internal") {
        override fun check(item: JavaMember, events: ConditionEvents) {
            events.add(
                SimpleConditionEvent(
                    item,
                    item.reflect().isInternal(),
                    "${item.description} in ${item.sourceCodeLocation}",
                ),
            )
        }

        private fun Member.isInternal(): Boolean = try {
            when (this) {
                is Constructor<*> -> kotlinFunction?.visibility == KVisibility.INTERNAL
                is Field -> kotlinProperty?.visibility == KVisibility.INTERNAL
                is Method -> kotlinFunction?.visibility == KVisibility.INTERNAL
                else -> throw NotImplementedError("isInternal is not implemented for ${this::class.java}")
            }
        } catch (ex: UnsupportedOperationException) {
            false
        }
    }

// ---------------------------------------------------------------------------------------------------------------------

private fun JavaConstructor.anyParentIsAnnotatedWith(annotationType: Class<out Annotation>) =
    owner.allRawSuperclasses.any { parentClass ->
        val parameters = parameterTypes.map { it.name }.toTypedArray()
        parentClass.tryGetConstructor(*parameters).orElse(null)
            ?.isAnnotatedWith(annotationType) ?: false
    }

private fun JavaField.anyParentIsAnnotatedWith(annotationType: Class<out Annotation>) =
    owner.allRawSuperclasses.any { parentClass ->
        parentClass.tryGetField(name).orElse(null)
            ?.isAnnotatedWith(annotationType) ?: false
    }

private fun JavaMethod.anyParentIsAnnotatedWith(annotationType: Class<out Annotation>) =
    owner.allRawSuperclasses.any { parentClass ->
        run {
            val parameters = parameterTypes.map { it.name }.toTypedArray()
            parentClass.tryGetMethod(name, *parameters).orElse(null)
                ?.isAnnotatedWith(annotationType) ?: false
        } ||
            backingField()?.anyParentIsAnnotatedWith(annotationType) ?: false
    }

private fun JavaClass.isKotlinClass() = isMetaAnnotatedWith("kotlin.Metadata")

private fun JavaMethod.backingField(): JavaField? {
    if (owner.isKotlinClass()) {
        val fieldMethodMatch = Regex("^(?<fieldAction>get|is|set)(?<fieldName>[A-Z][^$]*).*").matchEntire(name)
        if (fieldMethodMatch != null) {
            val fieldAction = fieldMethodMatch.groups["fieldAction"]!!.value.let {
                when (it) {
                    "is" -> "get"
                    else -> it
                }
            }
            val fieldName = fieldMethodMatch.groups["fieldName"]!!.value.replaceFirstChar { it.lowercase() }
            val field: JavaField? = owner.tryGetField(fieldName).orElse(null)
            if (field != null) {
                when (fieldAction) {
                    "get" -> if (returnType == field.type) return field
                    "set" -> if (parameterTypes.size == 1 && parameterTypes[0] == field.type) return field
                }
            }
        }
    }
    return null
}

private fun JavaClass.isPartOf(`package`: JavaPackage) = "${this.packageName}.".startsWith("${`package`.name}.")
