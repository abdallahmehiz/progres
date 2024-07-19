plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidLibrary)
}

android {
  namespace = "mehiz.abdallah.progres.api"

  compileSdk = libs.versions.android.compileSdk.get().toInt()
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
