plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.moko.resources)
}

android {
  namespace = "mehiz.abdallah.progres.core"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
  }
}

multiplatformResources {
  resourcesPackage.set("mehiz.abdallah.progres.i18n")
}
