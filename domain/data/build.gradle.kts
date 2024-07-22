plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidLibrary)
}

android {
  namespace = "mehiz.abdallah.progres.data"

  compileSdk = libs.versions.android.compileSdk.get().toInt()

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

kotlin {
  androidTarget()
  iosX64()
  iosArm64()
  iosSimulatorArm64()
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}
