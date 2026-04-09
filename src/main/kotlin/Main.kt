package com.julia.discordbot

import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.common.entity.PresenceStatus
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking

@OptIn(PrivilegedIntent::class)
fun main() = runBlocking {
    val kord = createKord(loadToken())
    val services = BotServices.create()
    val commandRegistry = CommandRegistry.createDefault()
    registerRuntimeHandlers(kord, commandRegistry, services)
    login(kord)
}

private suspend fun createKord(token: String): Kord = Kord(token)

private fun registerRuntimeHandlers(kord: Kord, commandRegistry: CommandRegistry, services: BotServices) {
    kord.on<ReadyEvent> {
        println("Logado como ${self.tag}")

        kord.editPresence {
            status = PresenceStatus.Online
            streaming("Assistindo minha criadora", "https://www.twitch.tv/leeksxyy")
        }
    }

    registerHandlers(kord, commandRegistry, services)
}

@OptIn(PrivilegedIntent::class)
private suspend fun login(kord: Kord) {
    kord.login {
        intents = Intents(Intent.Guilds, Intent.GuildMembers, Intent.GuildMessages, Intent.MessageContent)
    }
}
