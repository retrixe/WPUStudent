package xyz.retrixe.wpustudent.kmp

import io.ktor.client.engine.HttpClientEngineFactory

expect val PlatformHttpClientEngineFactory: HttpClientEngineFactory<*>
