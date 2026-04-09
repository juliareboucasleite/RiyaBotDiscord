package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext

interface BotCommand {
    val name: String
    val description: String
    val usage: String
    val aliases: Set<String>

    suspend fun execute(context: CommandContext)
}
