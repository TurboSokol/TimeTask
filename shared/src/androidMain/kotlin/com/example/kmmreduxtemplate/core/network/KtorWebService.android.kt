package com.example.kmmreduxtemplate.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Android-specific HttpClient implementation using OkHttp engine
 */
actual fun httpClient(logService: LogService, config: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(OkHttp) {
        // Apply custom config
        config()
        
        // Install Content Negotiation for JSON
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
        
        // Configure OkHttp engine
        engine {
            config {
                retryOnConnectionFailure(true)
            }
        }
    }
}
