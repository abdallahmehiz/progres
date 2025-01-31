import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.firebase.performance)
  alias(libs.plugins.firebase.crashlytics)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.aboutlibraries)
  alias(libs.plugins.buildkonfig)
  alias(libs.plugins.detekt)
  alias(libs.plugins.gms)
}

kotlin {
  androidTarget {
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
      implementation(libs.androidx.material)
      implementation(libs.androidx.workmanager)
      implementation(libs.androidx.splashscreen)
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.credentials.core)
      implementation(libs.androidx.credentials.compat)

      // implementation(libs.koin.androidx.startup)
      implementation(libs.koin.androidx.workmanager)

      implementation(libs.bundles.firebase)
    }
    commonMain.dependencies {
      implementation(project(":i18n"))
      implementation(project(":domain"))
      implementation(project(":core"))
      implementation(project(":core:update-check"))

      implementation(compose.ui)
      implementation(compose.runtime)
      implementation(compose.animation)
      implementation(compose.material3)
      implementation(compose.foundation)
      implementation(compose.animationGraphics)
      implementation(compose.components.resources)
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
      implementation(libs.placeholder)
      implementation(libs.qrscan)
      implementation(libs.compottie)
      implementation(libs.bundles.aboutlibraries)

      implementation(libs.kotlinx.datetime)

      api(libs.bundles.koin)
      api(libs.bundles.datastore)
    }
  }
}

val appVersionName = libs.versions.app.version.name.get()
val appVersionCode = libs.versions.app.version.code.get().toInt()
val appPackageName = "mehiz.abdallah.progres"

android {
  namespace = appPackageName
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")

  defaultConfig {
    applicationId = appPackageName
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = appVersionCode
    versionName = appVersionName
    multiDexEnabled = true
    vectorDrawables.useSupportLibrary = true
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
      excludes += "**/dokka/**"
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
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
  implementation(platform(libs.firebase.bom))
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

compose.resources {
  generateResClass = always
}

tasks["copyNonXmlValueResourcesForCommonMain"].dependsOn("exportLibraryDefinitions")
