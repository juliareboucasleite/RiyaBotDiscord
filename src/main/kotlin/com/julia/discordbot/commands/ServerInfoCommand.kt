package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import com.julia.discordbot.requireGuildMessage

class ServerInfoCommand : BotCommand {
    override val name = "serverinfo"
    override val description = "Mostra informacoes basicas do servidor."
    override val usage = "serverinfo"
    override val aliases = setOf("server")

    override suspend fun execute(context: CommandContext) {
        if (!context.requireGuildMessage()) return

        val guild = context.event.message.getGuild()
        val owner = guild.getOwner()

        context.reply(
            """
            Servidor: **${guild.name}**
            ID: `${guild.id.value}`
            Dono: ${owner.mention}
            Membros: `${guild.memberCount ?: 0}`
            """.trimIndent()
        )
    }
}
