package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import com.julia.discordbot.dashboard.allCommandsInfo
import dev.kord.common.Color
import dev.kord.core.behavior.channel.createEmbed

class HelpCommand : BotCommand {
    override val name = "help"
    override val description = "Lista os comandos principais disponiveis."
    override val usage = "help"
    override val aliases = setOf("ajuda", "commands")

    override suspend fun execute(context: CommandContext) {
        val self = context.kord.getSelf()
        val guildId = context.event.guildId?.value
        val prefix = guildId?.let { context.services.settingsStore.get(it).prefix } ?: "!"

        val commandsByCategory = allCommandsInfo()
            .sortedBy { categoryOrder(it.category) }
            .groupBy { it.category }

        context.event.message.channel.createEmbed {
            title = "Ajuda da ${self.username}"
            description = buildString {
                appendLine("Oi! Aqui estao os comandos principais do bot.")
                appendLine()
                appendLine("Use `$prefix<comando>` para executar uma acao.")
                append("Exemplo: `${prefix}ping`")
            }
            color = Color(0x00d26a)

            self.avatar?.cdnUrl?.toUrl()?.let { avatarUrl ->
                thumbnail {
                    url = avatarUrl
                }
            }

            commandsByCategory.forEach { (category, commands) ->
                field(category, true) {
                    commands.joinToString("\n") { command ->
                        "`$prefix${command.usage}`\n${command.description}"
                    }
                }
            }

            footer {
                text = "Total de comandos: ${commandsByCategory.values.sumOf { it.size }}"
            }
        }
    }

    private fun categoryOrder(category: String): Int {
        return when (category) {
            "Geral" -> 0
            "Informacao" -> 1
            "Administracao" -> 2
            "Moderacao" -> 3
            "Comunidade" -> 4
            "Utilidades" -> 5
            else -> Int.MAX_VALUE
        }
    }
}
