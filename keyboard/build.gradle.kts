plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.ktlint)
  `maven-publish`
}

group = "com.github.NuclleaR"
version = providers.environmentVariable("VERSION").orNull ?: "local"

android {
  namespace = "io.github.nucllear.tvkeyboard"
  compileSdk = libs.versions.sdk.compile.get().toInt()
  defaultConfig {
    minSdk = libs.versions.sdk.min.get().toInt()
    consumerProguardFiles()
  }
  compileOptions {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.bytecode.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.bytecode.get())
  }
  buildFeatures {
    compose = true
  }
  testOptions {
    unitTests.isIncludeAndroidResources = true
  }
  publishing {
    singleVariant("release") {
      withSourcesJar()
    }
  }
}

kotlin {
  jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
  explicitApi()
  compilerOptions {
    allWarningsAsErrors.set(true)
  }
}

ktlint {
  ktlintVersion.set(libs.versions.ktlint.core.get())
}

dependencies {
  "ktlintRuleSet"(libs.ktlint.compose)
  implementation(platform(libs.compose.bom))
  api(libs.compose.ui)
  api(libs.tv.material)
  implementation(libs.compose.foundation)
  implementation(libs.coroutines.core)
  implementation(libs.compose.ui.tooling.preview)
  debugImplementation(libs.compose.ui.tooling)

  testImplementation(platform(libs.compose.bom))
  testImplementation(libs.junit)
  testImplementation(libs.kotlin.testJunit)
  testImplementation(libs.robolectric)
  testImplementation(libs.androidx.test.junit)
  testImplementation(libs.compose.ui.test.junit4)
  testImplementation(libs.compose.ui.test.manifest)
}

// JitPack builds a tag as com.github.NuclleaR:tv-keyboard:<tag>
publishing {
  publications {
    register<MavenPublication>("release") {
      artifactId = "tv-keyboard"
      afterEvaluate { from(components["release"]) }
    }
  }
}
