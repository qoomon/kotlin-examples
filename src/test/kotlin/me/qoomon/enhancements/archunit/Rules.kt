package me.qoomon.enhancements.archunit

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
import me.qoomon.enhancements.kotlin.PackageInternal
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.jvm.kotlinProperty

// TODO check if super classes or interfaces are package internal
val ALL_PACKAGE_INTERNAL_ELEMENTS_SHOULD_BE_INTERNAL: ArchRule =
    CompositeArchRule.of(
        listOf(
            classes().that().areAnnotatedWith(PackageInternal::class.java).should(beInternal()),
            constructors().that().areAnnotatedWith(PackageInternal::class.java).should(beInternalMember()),
            fields().that().areAnnotatedWith(PackageInternal::class.java).should(beInternalMember()),
            methods().that().areAnnotatedWith(PackageInternal::class.java).should(beInternalMember()),
        )
    ).`as`("@${PackageInternal::class.java.simpleName} elements should also be marked as internal")
        .allowEmptyShould(true)

// val ALL_IMPLICIT_PACKAGE_INTERNAL_CLASSES_SHOULD_BE_ANNOTATED: ArchRule =

val CLASSES_SHOULD_NOT_ACCESS_PACKAGE_INTERNAL_ELEMENTS_FROM_OUTSIDE_ITS_PACKAGE: ClassesShouldConjunction =
    classes().should(notAccessPackageInternalElementsFromOutsidePackageHierarchy())

private fun notAccessPackageInternalElementsFromOutsidePackageHierarchy(): ArchCondition<JavaClass> =
    object : ArchCondition<JavaClass>("access package internal elements") {
        override fun check(javaClass: JavaClass, events: ConditionEvents) {
            javaClass.directDependenciesFromSelf.forEach { dependency ->
                if (
                    dependency.targetClass.isAnnotatedWith(PackageInternal::class.java) ||
                    dependency.targetClass.allRawSuperclasses.any { c -> c.isAnnotatedWith(PackageInternal::class.java) }
                ) {
                    val isPackageInternalAccess = dependency.targetClass.isPartOf(dependency.originClass.`package`)
                    events.add(
                        SimpleConditionEvent(
                            dependency,
                            isPackageInternalAccess,
                            dependency.description
                        )
                    )
                }
            }
            javaClass.accessesFromSelf
                .filter { it.target.owner.allRawSuperclasses.any { c -> c.isAnnotatedWith(PackageInternal::class.java) } }
                .forEach { access ->
                    val origin = access.origin
                    val target = access.target
                    val overridePackageInternalMember = when (target) {
                        is MethodCallTarget -> {
                            val superMethodIsPackageInternal =
                                target.owner.allRawSuperclasses.any { superClass ->
                                    val methodParameters =
                                        target.rawParameterTypes.map { it.reflect() }.toTypedArray()
                                    superClass.tryGetMethod(target.name, *methodParameters).orElse(null)
                                        ?.isAnnotatedWith(PackageInternal::class.java) == true
                                }
                            val superBackingFieldIsPackageInternal =
                                target.owner.allRawSuperclasses.any { superClass ->
                                    val backingField = target.backingField()
                                    if (backingField != null) {
                                        superClass.tryGetField(backingField.name).orElse(null)
                                            ?.isAnnotatedWith(PackageInternal::class.java) == true
                                    } else false
                                }
                            superMethodIsPackageInternal || superBackingFieldIsPackageInternal
                        }
                        is FieldAccessTarget -> {
                            val superFieldIsPackageInternal =
                                target.owner.allRawSuperclasses.any { superClass ->
                                    superClass.tryGetField(target.name).orElse(null)
                                        ?.isAnnotatedWith(PackageInternal::class.java) == true
                                }
                            superFieldIsPackageInternal
                        }
                        else -> false
                    }

                    if (overridePackageInternalMember) {
                        val isSelfAccess = target.owner == origin.owner
                        val isPackageInternalAccess = target.owner.isPartOf(origin.owner.`package`)
                        val isConditionSatisfied = isSelfAccess || isPackageInternalAccess
                        events.add(
                            SimpleConditionEvent(
                                access,
                                isConditionSatisfied,
                                access.description + " overridePackageInternalMember"
                            )
                        )
                    }
                }
        }
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
