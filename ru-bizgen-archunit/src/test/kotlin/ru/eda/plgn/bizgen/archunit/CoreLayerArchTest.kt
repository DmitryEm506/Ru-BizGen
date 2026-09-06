package ru.eda.plgn.bizgen.archunit

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.layeredArchitecture

/**
 * ArchUnit-проверки соответствия слоёв в ядре через [ArchRule].
 *
 * @author Dmitry_Emelyanenko
 */
@Suppress("unused")
@AnalyzeClasses(
  packages = ["ru.eda.plgn.bizgen"],
  importOptions = [ImportOption.DoNotIncludeTests::class],
)
internal class CoreLayerArchTest {

  @ArchTest
  val coreLayersMustRespectDependencyDirection: ArchRule = layeredArchitecture()
    .consideringOnlyDependenciesInAnyPackage("ru.eda.plgn.bizgen.core..")
    .layer("Utils").definedBy("ru.eda.plgn.bizgen.core.utils..")
    .layer("Generator").definedBy("ru.eda.plgn.bizgen.core.generator..")
    .layer("GeneratorInfo").definedBy("ru.eda.plgn.bizgen.core.generator_info..")
    .whereLayer("Utils").mayOnlyBeAccessedByLayers("Generator", "GeneratorInfo")
    .whereLayer("Generator").mayOnlyBeAccessedByLayers("GeneratorInfo")
    .whereLayer("GeneratorInfo").mayNotBeAccessedByAnyLayer()
    .because("внутри core зависимости идут только Utils ← Generator ← GeneratorInfo")
}
