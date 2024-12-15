plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.sqldelight)
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

  sourceSets {
    androidMain.dependencies {
      implementation(libs.sqldelight.driver.android)
    }
    commonMain.dependencies {
      api(libs.kotlinx.datetime)
      implementation(libs.koin.core)
      implementation(libs.sqldelight.coroutines)

      implementation(project(":core"))
    }
    nativeMain.dependencies {
      implementation(libs.sqldelight.driver.native)
    }
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

sqldelight {
  databases {
    create("ProgresDB") {
      packageName.set("mehiz.abdallah.progres.data.db")
      schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
      verifyMigrations = true
      verifyDefinitions = true
    }
  }
}
