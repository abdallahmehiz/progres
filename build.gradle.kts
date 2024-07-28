plugins {
  alias(libs.plugins.kotlinMultiplatform) apply false
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.jetbrainsCompose) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.androidLibrary) apply false
  alias(libs.plugins.moko.resources) apply false
  alias(libs.plugins.sqldelight) apply false
  alias(libs.plugins.detekt) apply false
}

buildscript {
  dependencies {
    classpath(libs.moko.resources.generator)
  }
}
