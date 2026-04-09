package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext

class HelpCommand : BotCommand {
    override val name = "help"
    override val description = "Lista os comandos principais disponiveis."
    override val usage = "help"
    override val aliases = setOf("ajuda", "commands")

    override suspend fun execute(context: CommandContext) {
        val helpMessage = buildString {
            appendLine("Comandos disponiveis:")
            context.registry.commands.forEach { command ->
                appendLine("`!${command.usage}` - ${command.description}")
            }
        }

        context.reply(helpMessage.trim())
    }
}
