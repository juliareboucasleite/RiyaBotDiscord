package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext

class PingCommand : BotCommand {
    override val name = "ping"
    override val description = "Responde com pong para testar se o bot esta online."
    override val usage = "ping"
    override val aliases = emptySet<String>()

    override suspend fun execute(context: CommandContext) {
        context.reply("pong")
    }
}
