import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "xyz.retrixe.wpustudent.composeApp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            // Custom
            freeCompilerArgs.addAll("-P", "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=xyz.retrixe.wpustudent.kmp.Parcelize")
        }

        androidResources {
            enable = true
        }

        optimization {
            consumerKeepRules.apply {
                publish = true
                file("proguard-rules.pro")
            }
        }

        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            // Custom dependencies
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.toolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            // Custom dependencies
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.material3.adaptiveNavigationSuite)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.ksoup)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            // implementation(libs.junit)
            // androidTestImplementation(libs.androidx.junit)
            // androidTestImplementation(libs.androidx.espresso.core)
            // androidTestImplementation(platform(libs.androidx.compose.bom))
            // androidTestImplementation(libs.androidx.ui.test.junit4)
            // debugImplementation(libs.androidx.ui.tooling)
            // debugImplementation(libs.androidx.ui.test.manifest)
        }
        // Custom dependencies
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            // Custom dependencies
            implementation(libs.ktor.client.java)
            implementation(libs.java.keyring)
            implementation(libs.appdirs)
        }
    }

    // Custom
    compilerOptions {
        // Common compiler options applied to all Kotlin source sets
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

dependencies {
    // Formerly debugImplementation (not 100% sure what's happening)
    androidRuntimeClasspath(libs.compose.ui.tooling)
}

buildkonfig {
    packageName = "xyz.retrixe.wpustudent"

    defaultConfigs {
        buildConfigField(STRING, "VERSION_NAME", project.property("app.version.name").toString())
        buildConfigField(INT, "VERSION_CODE", project.property("app.version.code").toString())
    }
}
