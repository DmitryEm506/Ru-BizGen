package ru.eda.plgn.bizgen.archunit

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices

/**
 * Границы модулей и запрет сторонних зависимостей.
 *
 * Правила «plugin ↛ mcp/perf/Ktor» живут в `ru-bizgen-plugin` ([PluginBoundaryArchTest]), чтобы arch-модуль не тянул IntelliJ Platform.
 *
 * @author Dmitry_Emelyanenko
 */
@Suppress("unused")
@AnalyzeClasses(
  packages = ["ru.eda.plgn.bizgen"],
  importOptions = [ImportOption.DoNotIncludeTests::class],
)
internal class ModuleBoundaryArchTest {

  @ArchTest
  val coreMustNotDependOnAdapters: ArchRule = noClasses()
    .that().resideInAnyPackage("ru.eda.plgn.bizgen.core..")
    .should().dependOnClassesThat().resideInAnyPackage(
      "ru.eda.plgn.bizgen.plugin..",
      "ru.eda.plgn.bizgen.mcp..",
      "ru.eda.plgn.bizgen.perf..",
    )
    .because("ядро не должно зависеть от адаптеров (plugin, mcp, perf)")

  @ArchTest
  val mcpMustNotDependOnPluginOrPerf: ArchRule = noClasses()
    .that().resideInAnyPackage("ru.eda.plgn.bizgen.mcp..")
    .should().dependOnClassesThat().resideInAnyPackage(
      "ru.eda.plgn.bizgen.plugin..",
      "ru.eda.plgn.bizgen.perf..",
    )
    .because("mcp не должен зависеть от plugin и perf")

  @ArchTest
  val perfMustNotDependOnPluginOrMcp: ArchRule = noClasses()
    .that().resideInAnyPackage("ru.eda.plgn.bizgen.perf..")
    .should().dependOnClassesThat().resideInAnyPackage(
      "ru.eda.plgn.bizgen.plugin..",
      "ru.eda.plgn.bizgen.mcp..",
    )
    .because("perf не должен зависеть от plugin и mcp")

  @ArchTest
  val coreMustNotDependOnForbiddenThirdParty: ArchRule = noClasses()
    .that().resideInAnyPackage("ru.eda.plgn.bizgen.core..")
    .should().dependOnClassesThat().resideInAnyPackage(
      "com.intellij..",
      "com.jetbrains..",
      "io.ktor..",
      "io.modelcontextprotocol..",
      "org.openjdk.jmh..",
      "org.kodein..",
    )
    .because("ядро не должно зависеть от фреймворков адаптеров и инструментов бенчмаркинга")

  @ArchTest
  val mcpMustNotDependOnIntelliJ: ArchRule = noClasses()
    .that().resideInAnyPackage("ru.eda.plgn.bizgen.mcp..")
    .should().dependOnClassesThat().resideInAnyPackage(
      "com.intellij..",
      "com.jetbrains..",
    )
    .because("mcp не должен зависеть от IntelliJ Platform")

  @ArchTest
  val perfMustNotDependOnAdapters: ArchRule = noClasses()
    .that().resideInAnyPackage("ru.eda.plgn.bizgen.perf..")
    .should().dependOnClassesThat().resideInAnyPackage(
      "com.intellij..",
      "com.jetbrains..",
      "io.modelcontextprotocol..",
      "io.ktor..",
    )
    .because("perf не должен зависеть от IntelliJ Platform, MCP SDK и Ktor")

  @ArchTest
  val modulesMustBeFreeOfCycles: ArchRule = slices()
    .matching("ru.eda.plgn.bizgen.(*)..")
    .should().beFreeOfCycles()
    .because("модули ru.eda.plgn.bizgen не должны образовывать циклических зависимостей")
}
