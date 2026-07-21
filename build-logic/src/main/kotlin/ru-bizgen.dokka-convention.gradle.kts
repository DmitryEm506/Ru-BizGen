import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
  id("org.jetbrains.dokka")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dokka {
  moduleName.set(project.name)
  moduleVersion.set(version.toString())

  dokkaSourceSets.getByName("main") {
    jdkVersion.set(libs.findVersion("java").get().requiredVersion.toInt())
    languageVersion.set(libs.findVersion("kotlin").get().requiredVersion)
    reportUndocumented.set(true)

    documentedVisibilities(VisibilityModifier.Public, VisibilityModifier.Protected)

    sourceLink {
      localDirectory.set(file("src/main/kotlin"))
      remoteUrl("https://github.com/DmitryEm506/Ru-BizGen/blob/dev/${project.name}/src/main/kotlin")
      remoteLineSuffix.set("#L")
    }
  }

  dokkaPublications.getByName("html") {
    suppressInheritedMembers.set(true)
    offlineMode.set(true)
  }
}
