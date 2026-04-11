package com.julia.discordbot.dashboard

import com.julia.discordbot.BotServices
import dev.kord.core.Kord
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class DashboardServer(
    private val kord: Kord,
    private val services: BotServices,
    private val port: Int = 8080
) {
    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    fun start() {
        server = embeddedServer(Netty, port = port) {
            configureSerialization()
            configureCors()
            configureStatusPages()
            configureCallLogging()
            configureRouting(kord, services)
        }.start(wait = false)

        println("Dashboard iniciado em http://localhost:$port")
    }

    fun stop() {
        server?.stop(1000, 2000)
    }
}

private fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

private fun Application.configureCors() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
}

private fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(
                text = "Erro interno: ${cause.localizedMessage}",
                status = HttpStatusCode.InternalServerError
            )
        }
    }
}

private fun Application.configureCallLogging() {
    install(CallLogging)
}
