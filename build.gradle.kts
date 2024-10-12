plugins {
  alias(libs.plugins.firebase.crashlytics) apply false
  alias(libs.plugins.firebase.performance) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.jetbrains.compose) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.moko.resources) apply false
  alias(libs.plugins.aboutlibraries) apply false
  alias(libs.plugins.buildkonfig) apply false
  alias(libs.plugins.sqldelight) apply false
  alias(libs.plugins.detekt) apply false
  alias(libs.plugins.gms) apply false
}

buildscript {
  dependencies {
    classpath(libs.moko.resources.generator)
    classpath(libs.buildkonfig)
  }
}
