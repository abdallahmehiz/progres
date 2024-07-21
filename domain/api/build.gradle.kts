plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "mehiz.abdallah.progres.api"

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

  sourceSets {
    commonMain.dependencies {
      implementation(libs.bundles.ktor)
      implementation(libs.kodein.core)
    }
    androidMain.dependencies {
      implementation(libs.ktor.client.okhttp)
    }
    nativeMain.dependencies {
      implementation(libs.ktor.client.darwin)
    }
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}
