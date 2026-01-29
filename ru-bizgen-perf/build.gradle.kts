import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
  alias(libs.plugins.jmh)
  alias(libs.plugins.dokka)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.kotlin.allopen)
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
  implementation(project(":ru-bizgen-core"))
  implementation(libs.kotlinx.serialization.json)

  jmhImplementation(libs.jmh.core)
  jmhAnnotationProcessor(libs.jmh.proc)
}

// 1. Создаем конфигурацию для экспорта
val jmhApiByConf by configurations.creating {
  isCanBeResolved = false
  isCanBeConsumed = true
  // Подтягиваем зависимости, чтобы в модуле валидации не было ClassNotFound для JMH аннотаций
  extendsFrom(configurations.jmhRuntimeClasspath.get())
}

// 2. Используем существующую задачу jmhJar вместо создания новой. Настраиваем артефакты, привязывая их к выходу задачи jmhJar
artifacts {
  add(jmhApiByConf.name, tasks.named("jmhJar"))
}

allOpen {
  annotation("org.openjdk.jmh.annotations.State")
}

jmh {
  includes = listOf("ru.eda.plgn.bizgen.perf.bench.*Benchmark.*")
  resultFormat = "JSON"
  resultsFile = layout.buildDirectory.file("reports/jmh/jmh-result.json")
  duplicateClassesStrategy = DuplicatesStrategy.WARN
  profilers = listOf("gc", "stack")
}

// Documentation
dokka {
  moduleName.set(project.name)
  moduleVersion.set(version.toString())

  dokkaSourceSets.main {
    jdkVersion.set(libs.versions.java.get().toInt())
    languageVersion.set(libs.versions.kotlin.get())
    reportUndocumented.set(true)

    documentedVisibilities(VisibilityModifier.Public, VisibilityModifier.Protected)

    sourceRoots.from(sourceSets.jmh.get().kotlin.srcDirs)

    sourceLink {
      localDirectory.set(file("src/jmh/kotlin"))
      remoteUrl("https://github.com/DmitryEm506/Plugin_EDA_Bizgen/blob/${project.name}/main/src/jmh/kotlin")
      remoteLineSuffix.set("#L")
    }
  }

  dokkaPublications.html {
    suppressInheritedMembers.set(true)
    offlineMode.set(true)
  }
}

tasks {
  register<JavaExec>("jmhMarkdownReport") {
    group = "benchmark"
    description = "Generate Markdown report from JMH JSON"

    dependsOn("jmh")

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("ru.eda.plgn.bizgen.perf.jmh.report.JmhMarkdownReportKt")

    workingDir = projectDir
  }
}