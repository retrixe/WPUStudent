# https://github.com/JetBrains/compose-multiplatform/blob/419f82c073906f40063c162810d72e6d7fb6f1b5/examples/imageviewer/desktopApp/rules.pro#L4
# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { volatile <fields>; }
-dontnote io.ktor.**

# Java Keyring
-keep class com.github.javakeyring.** { *; }
-dontwarn com.github.javakeyring.**
-dontnote com.github.javakeyring.**

# JNA
-keep class com.sun.jna.** { *; }
-dontwarn com.sun.jna.**
-dontnote com.sun.jna.**

# SLF4J
-keep class org.slf4j.** { *; }
-dontwarn org.slf4j.**
-dontnote org.slf4j.**

# Freedesktop DBus/Secret
-keep class org.freedesktop.dbus.** { *; }
-keep class org.freedesktop.secret.** { *; }
-dontwarn org.freedesktop.dbus.**
-dontwarn org.freedesktop.secret.**
-dontnote org.freedesktop.dbus.**
-dontnote org.freedesktop.secret.**
