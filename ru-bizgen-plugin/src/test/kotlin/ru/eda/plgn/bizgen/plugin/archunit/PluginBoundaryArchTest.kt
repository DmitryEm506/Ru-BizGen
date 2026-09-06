package ru.eda.plgn.bizgen.plugin.archunit

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

/**
 * Границы plugin-модуля: не зависит от mcp/perf и стека MCP/Ktor.
 *
 * Остальные модульные правила — в `:ru-bizgen-archunit`.
 */
@AnalyzeClasses(
  packages = ["ru.eda.plgn.bizgen.plugin"],
  importOptions = [ImportOption.DoNotIncludeTests::class],
)
internal class PluginBoundaryArchTest {

  @ArchTest
  val pluginMustNotDependOnMcpOrPerf: ArchRule = noClasses()
    .that().resideInAnyPackage("ru.eda.plgn.bizgen.plugin..")
    .should().dependOnClassesThat().resideInAnyPackage(
      "ru.eda.plgn.bizgen.mcp..",
      "ru.eda.plgn.bizgen.perf..",
    )
    .because("plugin не должен зависеть от mcp и perf")

  @ArchTest
  val pluginMustNotDependOnMcpStack: ArchRule = noClasses()
    .that().resideInAnyPackage("ru.eda.plgn.bizgen.plugin..")
    .should().dependOnClassesThat().resideInAnyPackage(
      "io.modelcontextprotocol..",
      "io.ktor..",
    )
    .because("plugin не должен зависеть от MCP SDK и Ktor")
}
