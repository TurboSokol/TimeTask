import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    // SQLDelight plugin temporarily disabled for WASM compatibility
     alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm()
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // Coroutines & Flow
            implementation(libs.kotlinx.coroutines.core)
            
            // Serialization
            implementation(libs.kotlinx.serialization.json)
            
            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinxJson)
            implementation(libs.ktor.client.logging)
            
            // Koin DI
            implementation(libs.koin.core)
        }
        
        androidMain.dependencies {
            // Android-specific Ktor client
            implementation(libs.ktor.client.okhttp)
            
            // Android-specific Koin
            implementation(libs.koin.android)
            
            // SQLDelight for Android
            implementation(libs.sqldelight.driver.android)
            implementation(libs.sqldelight.runtime)
        }
        
        iosMain.dependencies {
            // iOS-specific Ktor client
            implementation(libs.ktor.client.darwin)
            
            // SQLDelight for iOS
             implementation(libs.sqldelight.driver.native)
             implementation(libs.sqldelight.runtime)
             implementation(libs.sqldelight.coroutines.extensions)
        }
        
        jvmMain.dependencies {
            // JVM-specific Ktor client
            implementation(libs.ktor.client.java)
            
            // SQLDelight for JVM
             implementation(libs.sqldelight.driver.jdbc)
             implementation(libs.sqldelight.runtime)
             implementation(libs.sqldelight.coroutines.extensions)
        }
        
        wasmJsMain.dependencies {
            // WASM/JS-specific Ktor client
            implementation(libs.ktor.client.js)
            
            // No SQLDelight dependencies for WASM - using in-memory storage only
            // SQLDelight doesn't support WASM yet
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.turbosokol.TimeTask.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

// SQLDelight configuration
sqldelight {
    databases {
        create("TaskDatabase") {
            packageName.set("com.turbosokol.TimeTask.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight"))
        }
    }
}
