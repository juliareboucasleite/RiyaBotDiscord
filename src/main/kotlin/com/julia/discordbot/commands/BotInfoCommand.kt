package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import kotlinx.coroutines.flow.toList

private const val DASHBOARD_URL = "http://localhost:8080"

class BotInfoCommand : BotCommand {
    override val name = "botinfo"
    override val description = "Mostra informacoes do bot e o localhost do dashboard."
    override val usage = "botinfo"
    override val aliases = setOf("bot")

    override suspend fun execute(context: CommandContext) {
        val self = context.kord.getSelf()
        val guildCount = context.kord.guilds.toList().size

        context.reply(
            """
            Bot: **${self.username}**
            ID: `${self.id.value}`
            Servidores: `${guildCount}`
            Dashboard local: `$DASHBOARD_URL`
            """.trimIndent()
        )
    }
}
