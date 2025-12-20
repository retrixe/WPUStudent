package xyz.retrixe.wpustudent.kmp

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual val PlatformHttpClientEngineFactory: HttpClientEngineFactory<*> = Darwin
