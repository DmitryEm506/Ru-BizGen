rootProject.name = "ru-bizgen"

includeBuild("build-logic")

include(
  "ru-bizgen-core",
  "ru-bizgen-mcp",
  "ru-bizgen-perf",
  "ru-bizgen-plugin",
  "ru-bizgen-perf-validation"
)