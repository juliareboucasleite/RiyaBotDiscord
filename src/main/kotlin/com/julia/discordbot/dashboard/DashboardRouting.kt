package com.julia.discordbot.dashboard

import com.julia.discordbot.BotServices
import dev.kord.core.Kord
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(kord: Kord, services: BotServices) {
    routing {
        // Frontend pages
        dashboardPages(kord, services)

        // API endpoints
        route("/api") {
            apiRoutes(kord, services)
        }
    }
}
