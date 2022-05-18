package me.qoomon.examples

import com.tngtech.archunit.core.domain.AccessTarget.MethodCallTarget
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaField
import com.tngtech.archunit.core.domain.JavaMember
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.CompositeArchRule
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.lang.syntax.elements.ClassesShouldConjunction
import com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.jvm.kotlinProperty

@AnalyzeClasses(packagesOf = [_Package::class])
class ArchUnitTest {

    @ArchTest
    fun `ensure no cycles`(classes: JavaClasses) = slices().matching("(**)").should().beFreeOfCycles().check(classes)

    @ArchTest
    fun `ensure no classes depend on upper packages`(classes: JavaClasses) =
        NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES.check(classes)

    // --- @PackageInternal --------------------------------------------------------------------------------------------
    @ArchTest
    fun `ensure internal package elements are also marked as internal`(classes: JavaClasses) =
        ALL_PACKAGE_INTERNAL_ELEMENTS_SHOULD_BE_INTERNAL.check(classes)

    @ArchTest
    fun `ensure no illegal access to internal package elements`(classes: JavaClasses) {
        NO_PACKAGE_INTERNAL_ELEMENTS_SHOULD_BE_ACCESSED_FROM_OUTSIDE_ITS_PACKAGE.check(classes)
    }
}

val ALL_PACKAGE_INTERNAL_ELEMENTS_SHOULD_BE_INTERNAL: ArchRule =
    CompositeArchRule.of(
        listOf(
            classes().that().areAnnotatedWith(PackageInternal::class.java).should(beInternal()),
            constructors().that().areAnnotatedWith(PackageInternal::class.java).should(beInternal()),
            fields().that().areAnnotatedWith(PackageInternal::class.java).should(beInternal()),
            methods().that().areAnnotatedWith(PackageInternal::class.java).should(beInternal()),
        )
    ).`as`("@${PackageInternal::class.java.simpleName} elements should also be marked as internal")
        .allowEmptyShould(true)

val NO_PACKAGE_INTERNAL_ELEMENTS_SHOULD_BE_ACCESSED_FROM_OUTSIDE_ITS_PACKAGE: ClassesShouldConjunction =
    noClasses().should(accessPackageInternalElementsIllegally())

private fun accessPackageInternalElementsIllegally(): ArchCondition<JavaClass> =
    object : ArchCondition<JavaClass>("access package internal elements") {
        override fun check(javaClass: JavaClass, events: ConditionEvents) {
            javaClass.accessesFromSelf.forEach { access ->
                val target = access.target
                if (
                    target.isAnnotatedWith(PackageInternal::class.java) ||
                    (
                        target is MethodCallTarget &&
                        target.backingField()?.isAnnotatedWith(PackageInternal::class.java) == true
                    ) ||
                    target.owner.classHierarchy.any { c -> c.isAnnotatedWith(PackageInternal::class.java) }
                ) {
                    val isSelfAccess = access.targetOwner == access.originOwner
                    val isPackageInternalAccess = "${access.targetOwner.packageName}."
                        .startsWith("${access.originOwner.packageName}.")
                    val isConditionSatisfied = !isSelfAccess && !isPackageInternalAccess
                    events.add(SimpleConditionEvent(access, isConditionSatisfied, access.description))
                }
            }
        }
    }

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

private fun JavaClass.isKotlinClass() = isMetaAnnotatedWith("kotlin.Metadata")

private inline fun <reified T : Any> beInternal() =
    object : ArchCondition<T>("annotated with @PackageInternal should be internal") {
        override fun check(item: T, events: ConditionEvents) {
            when (item) {
                is JavaClass -> events.add(
                    SimpleConditionEvent(
                        item,
                        item.reflect().isInternal(),
                        "${item.description} in ${item.sourceCodeLocation}"
                    )
                )
                is JavaMember -> events.add(
                    SimpleConditionEvent(
                        item,
                        item.reflect().isInternal(),
                        "${item.description} in ${item.sourceCodeLocation}"
                    )
                )
                else -> throw NotImplementedError("isInternal is not implemented for ${item::class.java}")
            }
        }

        private fun Class<*>.isInternal() = try {
            this.kotlin.visibility == KVisibility.INTERNAL
        } catch (ex: UnsupportedOperationException) {
            false
        }

        private fun Member.isInternal() = when (this) {
            is Constructor<*> -> isInternal()
            is Field -> isInternal()
            is Method -> isInternal()
            else -> throw NotImplementedError("isInternal is not implemented for ${this::class.java}")
        }

        private fun Constructor<*>.isInternal() = try {
            this.kotlinFunction?.visibility == KVisibility.INTERNAL
        } catch (ex: UnsupportedOperationException) {
            false
        }

        private fun Field.isInternal() = try {
            this.kotlinProperty?.visibility == KVisibility.INTERNAL
        } catch (ex: UnsupportedOperationException) {
            false
        }

        private fun Method.isInternal() = try {
            this.kotlinFunction?.visibility == KVisibility.INTERNAL
        } catch (ex: UnsupportedOperationException) {
            false
        }
    }
