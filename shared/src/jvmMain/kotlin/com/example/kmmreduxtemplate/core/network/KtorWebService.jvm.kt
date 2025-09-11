package com.example.kmmreduxtemplate.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.time.Duration

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

actual fun httpClient(logService: LogService, config: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Java) {
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
        
        // Configure Java engine
        engine {
            config {
                connectTimeout(Duration.ofSeconds(30))
            }
        }
    }
}
