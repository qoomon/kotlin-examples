package me.qoomon.examples

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import me.qoomon._Package
import me.qoomon.enhancements.archunit.CLASSES_SHOULD_HAVE_VALID_INTERNAL_PACKAGE_ANNOTATIONS
import me.qoomon.enhancements.archunit.CLASSES_SHOULD_NOT_ACCESS_PACKAGE_INTERNAL_ELEMENTS_FROM_OUTSIDE
import me.qoomon.enhancements.kotlin.PackageInternal

@AnalyzeClasses(packagesOf = [_Package::class])
class ArchUnitTest {

    @ArchTest
    fun `ensure no cycles`(classes: JavaClasses) =
        slices().matching("(**)").should().beFreeOfCycles()
            .check(classes)

    @ArchTest
    fun `ensure no classes depend on upper packages`(classes: JavaClasses) =
        NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES
            .check(classes)

    // --- @PackageInternal --------------------------------------------------------------------------------------------

    @ArchTest
    fun `ensure classes have valid internal package annotations`(classes: JavaClasses) =
        CLASSES_SHOULD_HAVE_VALID_INTERNAL_PACKAGE_ANNOTATIONS(PackageInternal::class)
            .check(classes)

    @ArchTest
    fun `ensure classes do not access package internal elements from outside`(classes: JavaClasses) {
        CLASSES_SHOULD_NOT_ACCESS_PACKAGE_INTERNAL_ELEMENTS_FROM_OUTSIDE(PackageInternal::class)
            .check(classes)
    }
}
