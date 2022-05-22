package me.qoomon.examples

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import me.qoomon._Package
import me.qoomon.enhancements.archunit.ALL_PACKAGE_INTERNAL_ELEMENTS_SHOULD_BE_INTERNAL
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
    fun `ensure internal package elements are also marked as internal`(classes: JavaClasses) =
        ALL_PACKAGE_INTERNAL_ELEMENTS_SHOULD_BE_INTERNAL(PackageInternal::class)
            .check(classes)

    @ArchTest
    fun `ensure no illegal access to internal package elements`(classes: JavaClasses) {
        CLASSES_SHOULD_NOT_ACCESS_PACKAGE_INTERNAL_ELEMENTS_FROM_OUTSIDE(PackageInternal::class)
            .check(classes)
    }
}
