package me.qoomon.examples

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaMember
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.CompositeArchRule
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import me.qoomon.PackageInternal
import me.qoomon._Package
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

    @ArchTest // TODO still WIP
    fun `ensure permitted access for internal package elements`(classes: JavaClasses) {
        noClasses().should(accessPackageInternalElements()).check(classes)
    }

    @ArchTest
    fun `ensure internal package elements are internal`(classes: JavaClasses) =
        CompositeArchRule.of(
            listOf(
                classes().that().areAnnotatedWith(PackageInternal::class.java).should(beInternal()),
                constructors().that().areAnnotatedWith(PackageInternal::class.java).should(beInternal()),
                fields().that().areAnnotatedWith(PackageInternal::class.java).should(beInternal()),
                methods().that().areAnnotatedWith(PackageInternal::class.java).should(beInternal()),
            )
        ).`as`("elements that are annotated with @${PackageInternal::class.simpleName} should be internal")
            .allowEmptyShould(true)
            .check(classes)
}

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

private fun beInternalClass() =
    object : ArchCondition<JavaClass>("annotated with @PackageInternal should be internal") {
        override fun check(item: JavaClass, events: ConditionEvents) {
            events.add(
                SimpleConditionEvent(
                    item,
                    item.reflect().isInternal(),
                    "${item.description} in ${item.sourceCodeLocation}"
                )
            )
        }

        private fun Class<*>.isInternal() = try {
            this.kotlin.visibility == KVisibility.INTERNAL
        } catch (ex: UnsupportedOperationException) {
            false
        }
    }

private fun beInternalMember() =
    object : ArchCondition<JavaMember>("annotated with @PackageInternal should be internal") {
        override fun check(item: JavaMember, events: ConditionEvents) {
            events.add(
                SimpleConditionEvent(
                    item,
                    item.reflect().isInternal(),
                    "${item.description} in ${item.sourceCodeLocation}"
                )
            )
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

private fun accessPackageInternalElements(): ArchCondition<JavaClass> =
    object : ArchCondition<JavaClass>("access package private elements") {

        override fun check(javaClass: JavaClass, events: ConditionEvents) {
            javaClass.accessesFromSelf.forEach {
                if (it.targetOwner.classHierarchy.any { c -> c.isAnnotatedWith(PackageInternal::class.java) } || it.target.isAnnotatedWith(
                        PackageInternal::class.java
                    )
                ) {
                    val selfAccess = it.targetOwner == it.originOwner
                    val packageInternalAccess =
                        "${it.targetOwner.packageName}.".startsWith("${it.originOwner.packageName}.")
                    val conditionSatisfied = !selfAccess && !packageInternalAccess
                    events.add(SimpleConditionEvent(it, conditionSatisfied, it.description))
                }
            }
        }
    }

// private fun Class<*>.isKotlinClass() = declaredAnnotations.any {
//     it.annotationClass.qualifiedName == "kotlin.Metadata"
// }
