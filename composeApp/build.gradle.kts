import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.buildkonfig)
  alias(libs.plugins.detekt)
}

kotlin {
  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(compose.preview)
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.material)
      implementation(libs.androidx.splashscreen)
      implementation(libs.androidx.credentials.core)
      implementation(libs.androidx.credentials.compat)
      implementation(libs.koin.androidx.startup)
    }
    commonMain.dependencies {
      implementation(project(":i18n"))
      implementation(project(":domain"))
      implementation(project(":core"))

      implementation(compose.ui)
      implementation(compose.runtime)
      implementation(compose.animation)
      implementation(compose.material3)
      implementation(compose.foundation)
      implementation(compose.animationGraphics)
      implementation(compose.materialIconsExtended)
      implementation(compose.components.uiToolingPreview)
      implementation(libs.compose.constraintlayout)
      implementation(libs.bundles.coil)
      implementation(libs.qr.code)
      implementation(libs.bundles.compose.settings)
      implementation(libs.compose.calendar)
      implementation(libs.jetlime)
      implementation(libs.bundles.voyager)
      implementation(libs.bundles.reveal)
      implementation(libs.ptr)
      implementation(libs.compose.icons.simpleicons)
      implementation(libs.bundles.connectivity)
      implementation(libs.sonner)

      implementation(libs.immutable.collections)
      implementation(libs.kotlinx.datetime)

      api(libs.bundles.koin)
      api(libs.bundles.datastore)
    }
    getByName("commonMain") {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
      }
    }
  }
}

val appVersionName = "0.1.0"
val appVersionCode = 2
val appPackageName = "mehiz.abdallah.progres"

android {
  namespace = appPackageName
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  defaultConfig {
    applicationId = appPackageName
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = appVersionCode
    versionName = appVersionName
    multiDexEnabled = true
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "src/androidMain/proguard-rules.pro",
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    isCoreLibraryDesugaringEnabled = true
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  bundle {
    language {
      enableSplit = false
    }
  }
  dependencies {
    debugImplementation(compose.uiTooling)
  }
}

dependencies {
  coreLibraryDesugaring(libs.desugar.jdk.libs)
  implementation(libs.detekt.gradle.plugin)
  detektPlugins(libs.detekt.formatter)
  detektPlugins(libs.detekt.rules.compose)
}

detekt {
  parallel = true
  allRules = false
  buildUponDefaultConfig = true
  config.setFrom("$rootDir/config/detekt/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
  setSource(files(rootDir))
  exclude("**/build/**")
  reports {
    html.required.set(true)
    md.required.set(true)
  }
}

buildkonfig {
  packageName = "$appPackageName.BuildKonfig"

  defaultConfigs {
    buildConfigField(STRING, "VERSION_NAME", appVersionName)
  }
}
