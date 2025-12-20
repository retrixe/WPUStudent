package xyz.retrixe.wpustudent.kmp

import io.ktor.client.engine.android.Android
import io.ktor.client.engine.HttpClientEngineFactory

actual val PlatformHttpClientEngineFactory: HttpClientEngineFactory<*> = Android
