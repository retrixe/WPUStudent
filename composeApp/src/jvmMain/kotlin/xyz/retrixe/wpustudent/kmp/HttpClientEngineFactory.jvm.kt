package xyz.retrixe.wpustudent.kmp

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.java.Java

actual val PlatformHttpClientEngineFactory: HttpClientEngineFactory<*> = Java
