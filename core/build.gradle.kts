plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.multiplatform)
}

android {
  namespace = "mehiz.abdallah.progres.core"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
}

kotlin {
  androidTarget()
  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { target ->
    target.binaries.framework {
      baseName = "composeApp"
      isStatic = true
    }
  }
  dependencies {
    commonMainApi(libs.logger)
    commonMainApi(libs.immutable.collections)
  }
}
