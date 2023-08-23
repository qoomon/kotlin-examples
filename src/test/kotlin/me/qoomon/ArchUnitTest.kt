package me.qoomon

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.DependencyRules.dependOnUpperPackages
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import me.qoomon.enhancements.archunit.haveValidPackageInternalAnnotations
import me.qoomon.enhancements.archunit.notAccessPackageInternalElementsFromOutside
import me.qoomon.enhancements.kotlin.PackageInternal

@AnalyzeClasses(packagesOf = [_Package::class])
class ArchUnitTest {

    @ArchTest
    fun `ensure no cycles`(classes: JavaClasses) =
        slices().matching("(**)").should().beFreeOfCycles()
            .check(classes)

    @ArchTest
    fun `ensure no classes depend on upper packages`(classes: JavaClasses) =
        noClasses().should(dependOnUpperPackages())
            .check(classes)

    // --- @PackageInternal --------------------------------------------------------------------------------------------

    @ArchTest
    fun `ensure classes have valid internal package annotations`(classes: JavaClasses) =
        classes().should(haveValidPackageInternalAnnotations(PackageInternal::class))
            .check(classes)

    @ArchTest
    fun `ensure classes do not access package internal elements from outside`(classes: JavaClasses) {
        classes().should(notAccessPackageInternalElementsFromOutside(PackageInternal::class))
            .check(classes)
    }
}
