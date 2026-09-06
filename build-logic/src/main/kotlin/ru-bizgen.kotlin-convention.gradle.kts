import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
  id("org.jetbrains.kotlin.jvm")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

kotlin {
  jvmToolchain(libs.findVersion("java").get().requiredVersion.toInt())
}
