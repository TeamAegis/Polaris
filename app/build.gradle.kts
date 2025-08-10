import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    serialization
    alias(libs.plugins.jetbrains.kotlin.serialization)
//    ksp
    alias(libs.plugins.ksp)
//    secrets-gradle-plugin
    alias(libs.plugins.secrets.gradle)

//    google-services
    alias(libs.plugins.google.services)
}

android {
    namespace = "appcup.uom.polaris"
    compileSdk = 36

    defaultConfig {
        val supabaseApiKey = project.loadLocalProperty(
            path = "local.properties",
            propertyName = "SUPABASE_API_KEY",
        )
        val supabaseUrl = project.loadLocalProperty(
            path = "local.properties",
            propertyName = "SUPABASE_URL",
        )
        val geminiLiveApiKey = project.loadLocalProperty(
            path = "local.properties",
            propertyName = "GEMINI_LIVE_API_KEY"
        )
        val mapsAppApiKey = project.loadLocalProperty(
            path = "local.properties",
            propertyName = "MAPS_API_KEY"
        )


        buildConfigField("String", "SUPABASE_API_KEY", supabaseApiKey)
        buildConfigField("String", "SUPABASE_URL", supabaseUrl)
        buildConfigField("String", "GEMINI_LIVE_API_KEY", geminiLiveApiKey)
        buildConfigField("String", "MAPS_APP_API_KEY", mapsAppApiKey)

        applicationId = "appcup.uom.polaris"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    applicationVariants.forEach { variant ->
        variant.sourceSets.forEach {
            it.javaDirectories += files("build/generated/ksp/${variant.name}/kotlin")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.kotlin.reflect)

//    splashscreen
    implementation(libs.core.splashscreen)

//    coroutines
    implementation(libs.kotlinx.coroutines.android)

//    navigation
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

//    serialization
    implementation(libs.kotlinx.serialization.core)

//    datastore
    implementation(libs.datastore)
    implementation(libs.datastore.preferences)

//    sqldelight
    implementation(libs.sqldelight.android)

//    koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

//    ktor
    implementation(libs.bundles.ktor)

//    coil
    implementation(libs.bundles.coil)

//    icons
    implementation(libs.material.icons.extended)

//    supabase
    implementation(project.dependencies.platform(libs.supabase))
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
    implementation(libs.supabase.storage)

//    pipecat
    implementation(libs.pipecat.android)

//    kolor
    implementation(libs.material.kolor)

//    mat
    implementation(libs.androidx.material3.android)
//    implementation(libs.material3.adaptive.navigation.suite)

//    maps
    implementation(libs.maps.compose)
    implementation(libs.places)
    implementation(libs.places.compose)
    implementation(libs.play.services.maps)
    implementation(libs.android.maps.utils)
    implementation(libs.play.services.location)

//    firebase ai
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)

//    camera
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)

}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

ksp {
    arg("KOIN_USE_COMPOSE_VIEWMODEL","true")
    arg("KOIN_CONFIG_CHECK","true")
}

secrets {
    propertiesFileName = "local.properties"

}

fun Project.loadLocalProperty(
    path: String,
    propertyName: String,
): String {
    val localProperties = Properties()
    val localPropertiesFile = project.rootProject.file(path)
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
        return localProperties.getProperty(propertyName)
    } else {
        throw GradleException("can not find property : $propertyName")
    }

}