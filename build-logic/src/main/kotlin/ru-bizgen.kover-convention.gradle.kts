plugins {
  id("org.jetbrains.kotlinx.kover")
}

kover {
  reports {
    total {
      xml { onCheck = true }
      html { onCheck = true }
    }
  }
}
