plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.moko.resources)
}

android {
  namespace = "mehiz.abdallah.progres.i18n"
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
    implementation(project(":core"))

    commonMainApi(libs.moko.resources)
    commonMainApi(libs.moko.resources.compose)
  }
}

multiplatformResources {
  resourcesPackage.set("mehiz.abdallah.progres.i18n")
}
