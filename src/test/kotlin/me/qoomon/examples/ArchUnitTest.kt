package me.qoomon.examples

import VisibilityPackage
import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaAccess
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaCodeUnit
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import java.lang.reflect.Method
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.kotlinFunction

@AnalyzeClasses(packagesOf = [_Package::class])
class ArchUnitTest {

    @ArchTest
    fun `ensure no cycles`(classes: JavaClasses) =
        slices().matching("(**)")
            .should().beFreeOfCycles()
            .check(classes)

    @ArchTest
    fun `ensure no classes depend on upper packages`(classes: JavaClasses) =
        NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES
            .check(classes)

    // TODO still WIP
    @ArchTest
    fun `ensure internal package usage`(classes: JavaClasses) {
        // noClasses()

        // .should(object : ArchCondition<JavaClass>("access package private") {
        //     override fun check(item: JavaClass, events: ConditionEvents?) {
        //         for (dependency in item.methodReferencesFromSelf) {
        //             val dependencyOnUpperPackage =
        //                 isDependencyOnUpperPackage(dependency.originClass, dependency.targetClass)
        //             events.add(SimpleConditionEvent(dependency, dependencyOnUpperPackage, dependency.description))
        //         }
        //     }
        //
        //     private fun isDependencyOnUpperPackage(origin: JavaClass, target: JavaClass): Boolean {
        //         val originPackageName = origin.packageName
        //         val targetSubPackagePrefix = target.packageName + "."
        //         return originPackageName.startsWith(targetSubPackagePrefix)
        //     }
        // })
        // .check(classes)

        classes()
            .should().onlyCallCodeUnitsThat(object : DescribedPredicate<JavaCodeUnit>("1") {
                override fun apply(input: JavaCodeUnit): Boolean {
                    return !input.isAnnotatedWith(VisibilityPackage::class.java)
                }
            })
            .check(classes)

        noClasses()
            .should().accessTargetWhere(object : DescribedPredicate<JavaAccess<*>>("1") {
                override fun apply(input: JavaAccess<*>): Boolean {
                    if (input.targetOwner == input.sourceCodeLocation.sourceClass) {
                        println("${input.targetOwner} -- ${input.originOwner}")
                        return false
                    }

                    return input.target.isAnnotatedWith(VisibilityPackage::class.java)
                }
            })
            .check(classes)
    }
}

// fun isKotlinInternalMethod() = object : DescribedPredicate<JavaCodeUnit>("Kotlin internal class") {
//     override fun apply(input: JavaCodeUnit) = input.reflect().isKotlinInternal()
//
//     private fun Method.isKotlinInternal() = isInternal()
// }

fun isKotlinInternalClass() = object : DescribedPredicate<JavaClass>("Kotlin internal class") {
    override fun apply(input: JavaClass) = input.reflect().isKotlinInternal()

    private fun Class<*>.isKotlinInternal() = isKotlinClass() && isInternal()
}

fun isKotlinNotInternal() = object : DescribedPredicate<JavaClass>("Kotlin not-internal class") {
    override fun apply(input: JavaClass) = input.reflect().isKotlinNotInternal()

    private fun Class<*>.isKotlinNotInternal() = isKotlinClass() && !isInternal()
}

fun Method.isInternal() = try {
    kotlinFunction?.visibility == KVisibility.INTERNAL
} catch (ex: UnsupportedOperationException) {
    false
}

fun Class<*>.isInternal() = try {
    kotlin.visibility == KVisibility.INTERNAL
} catch (ex: UnsupportedOperationException) {
    false
}

private fun Class<*>.isKotlinClass() = declaredAnnotations.any {
    it.annotationClass.qualifiedName == "kotlin.Metadata"
}
