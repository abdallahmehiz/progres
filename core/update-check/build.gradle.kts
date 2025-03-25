import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.buildkonfig)
}

android {
  namespace = "mehiz.abdallah.progres.update_check"

  compileSdk = libs.versions.android.compileSdk.get().toInt()

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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
      implementation(libs.koin.core)

      implementation(project(":core"))
    }
    androidMain.dependencies {
      implementation(libs.ktor.client.okhttp)
    }
    nativeMain.dependencies {
      implementation(libs.ktor.client.darwin)
    }
  }
}

buildkonfig {
  packageName = "mehiz.abdallah.progres.update_check.BuildKonfig"

  defaultConfigs {
    buildConfigField(STRING, "tag", "v${libs.versions.app.version.name.get()}")
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}
