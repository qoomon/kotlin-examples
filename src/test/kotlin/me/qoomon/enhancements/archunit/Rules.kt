package me.qoomon.enhancements.archunit

import com.tngtech.archunit.core.domain.AccessTarget.ConstructorCallTarget
import com.tngtech.archunit.core.domain.AccessTarget.FieldAccessTarget
import com.tngtech.archunit.core.domain.AccessTarget.MethodCallTarget
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaField
import com.tngtech.archunit.core.domain.JavaMember
import com.tngtech.archunit.core.domain.JavaPackage
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.CompositeArchRule
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import com.tngtech.archunit.lang.syntax.elements.ClassesShouldConjunction
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.jvm.kotlinProperty

fun ALL_PACKAGE_INTERNAL_ELEMENTS_SHOULD_BE_INTERNAL(packageInternalAnnotation: KClass<out Annotation>): ArchRule =
    CompositeArchRule.of(
        listOf(
            classes().that().areAnnotatedWith(packageInternalAnnotation.java).should(beInternal()),
            constructors().that().areAnnotatedWith(packageInternalAnnotation.java).should(beInternalMember()),
            fields().that().areAnnotatedWith(packageInternalAnnotation.java).should(beInternalMember()),
            methods().that().areAnnotatedWith(packageInternalAnnotation.java).should(beInternalMember()),
        )
    ).`as`("@${packageInternalAnnotation.simpleName} elements should also be marked as internal")
        .allowEmptyShould(true)

fun CLASSES_SHOULD_NOT_ACCESS_PACKAGE_INTERNAL_ELEMENTS_FROM_OUTSIDE(
    packageInternalAnnotation: KClass<out Annotation>,
): ClassesShouldConjunction =
    classes().should(notAccessPackageInternalElementsFromOutsidePackageHierarchy(packageInternalAnnotation))

private fun notAccessPackageInternalElementsFromOutsidePackageHierarchy(
    packageInternalAnnotation: KClass<out Annotation>,
): ArchCondition<JavaClass> =
    object : ArchCondition<JavaClass>("not access package internal elements") {
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
                                    if (targetIsAnnotated) description
                                    else "$description - a parent class is package internal"
                                }
                            )
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
                    val parentIsAnnotated = target.owner.allRawSuperclasses.any { parentClass ->
                        when (target) {
                            is ConstructorCallTarget -> {
                                val parameters = target.parameterTypes.map { it.name }.toTypedArray()
                                parentClass.tryGetConstructor(*parameters).orElse(null)
                                    ?.isAnnotatedWith(packageInternalAnnotation.java) ?: false
                            }
                            is FieldAccessTarget -> {
                                parentClass.tryGetField(target.name).orElse(null)
                                    ?.isAnnotatedWith(packageInternalAnnotation.java) ?: false
                            }
                            is MethodCallTarget -> {
                                run {
                                    val parameters = target.parameterTypes.map { it.name }.toTypedArray()
                                    parentClass.tryGetMethod(target.name, *parameters).orElse(null)
                                        ?.isAnnotatedWith(packageInternalAnnotation.java) ?: false
                                } ||
                                run {
                                    target.backingField()?.let {
                                        parentClass.tryGetField(it.name).orElse(null)
                                            ?.isAnnotatedWith(packageInternalAnnotation.java) ?: false
                                    } ?: false
                                }
                            }
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
                                    if (targetIsAnnotated) description
                                    else "$description - a parent definition is package internal"
                                }
                            )
                        )
                    }
                }
        }

        private fun JavaClass.anyParentIsAnnotatedWith(annotationType: Class<out Annotation>) =
            allRawSuperclasses.any { it.isAnnotatedWith(annotationType) }
    }

private fun beInternal(): ArchCondition<JavaClass> =
    object : ArchCondition<JavaClass>("should be internal") {
        override fun check(item: JavaClass, events: ConditionEvents) {
            events.add(
                SimpleConditionEvent(
                    item, item.reflect().isInternal(), "${item.description} in ${item.sourceCodeLocation}"
                )
            )
        }

        private fun Class<*>.isInternal() = try {
            this.kotlin.visibility == KVisibility.INTERNAL
        } catch (ex: UnsupportedOperationException) {
            false
        }
    }

private fun beInternalMember(): ArchCondition<JavaMember> =
    object : ArchCondition<JavaMember>("should be internal") {
        override fun check(item: JavaMember, events: ConditionEvents) {
            events.add(
                SimpleConditionEvent(
                    item, item.reflect().isInternal(), "${item.description} in ${item.sourceCodeLocation}"
                )
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

private fun JavaClass.isKotlinClass() = isMetaAnnotatedWith("kotlin.Metadata")

private fun MethodCallTarget.backingField(): JavaField? {
    if (owner.isKotlinClass()) {
        val fieldMethodMatch = Regex("^(?<fieldAction>get|set)(?<fieldName>[A-Z][^$]*).*").matchEntire(name)
        if (fieldMethodMatch != null) {
            val fieldAction = fieldMethodMatch.groups["fieldAction"]!!.value
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
