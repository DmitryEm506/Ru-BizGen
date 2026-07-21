plugins {
  id("ru-bizgen.kotlin-convention")
  id("ru-bizgen.dokka-convention")
  alias(libs.plugins.jmh)
  alias(libs.plugins.kotlin.allopen)
  alias(libs.plugins.kotlin.serialization)
}

dependencies {
  implementation(project(":ru-bizgen-core"))
  implementation(libs.kotlinx.serialization.json)

  jmhImplementation(libs.jmh.core)
  jmhAnnotationProcessor(libs.jmh.proc)
}

val jmhApiByConf = configurations.create("jmhApiByConf") {
  isCanBeResolved = false
  isCanBeConsumed = true
  extendsFrom(configurations.jmhRuntimeClasspath.get())
}

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

dokka {
  dokkaSourceSets.getByName("main") {
    sourceRoots.from(sourceSets.jmh.get().kotlin.srcDirs)

    sourceLink {
      localDirectory.set(file("src/jmh/kotlin"))
      remoteUrl("https://github.com/DmitryEm506/Ru-BizGen/blob/dev/${project.name}/src/jmh/kotlin")
      remoteLineSuffix.set("#L")
    }
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
