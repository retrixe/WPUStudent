import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    dependencies {
        implementation(projects.composeApp)
        implementation(compose.desktop.currentOs)
        implementation(libs.kotlinx.coroutinesSwing)
        // Custom dependencies
        implementation(libs.androidx.datastore.preferences)
    }
}

compose.desktop {
    application {
        mainClass = "xyz.retrixe.wpustudent.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, // macOS
                TargetFormat.Msi, TargetFormat.Exe, // Windows
                TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.AppImage // Linux
            )

            modules(
                "java.instrument",
                "java.management",
                "java.net.http",
                "jdk.security.auth",
                "jdk.unsupported"
            )

            packageName = "WPUStudent"
            packageVersion = project.property("app.version.name").toString()
            description = "A useful app for MIT-WPU students with various tools like attendance tracking."
            copyright = "Copyright © 2026 Ibrahim Ansari. Licensed under MPL-2.0."
            vendor = "retrixe"
            licenseFile.set(rootProject.file("LICENSE"))

            // TODO: https://kotlinlang.org/docs/multiplatform/compose-native-distribution.html#platform-specific-options
            /* windows {
                dirChooser = true
                upgradeUuid = ""
                shortcut = true
                menu = true
                iconFile.set(rootProject.file("resources/icon.ico"))
                menuGroup = "WPUStudent"
            }
            macOS {
                bundleID = "xyz.retrixe.wpustudent"
                packageName = rootProject.name
                iconFile.set(rootProject.file("resources/icon.icns"))
            }
            linux {
                iconFile.set(rootProject.file("resources/icon.png"))
            } */
        }

        buildTypes.release.proguard {
            configurationFiles.from(
                rootProject.file("composeApp/proguard-rules.pro"),
                project.file("compose-desktop.pro"))
        }
    }
}

// http://youtrack.jetbrains.com/issue/CMP-1675
tasks.withType<Zip>().named {
    it.matches(Regex("package(Release)?UberJarForCurrentOS"))
}.configureEach {
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
}
