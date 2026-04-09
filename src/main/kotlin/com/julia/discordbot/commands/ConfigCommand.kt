package com.julia.discordbot.commands

import com.julia.discordbot.ChannelSetting
import com.julia.discordbot.CommandContext
import com.julia.discordbot.requireGuildMessage
import com.julia.discordbot.requireModeratorPermission

class ConfigCommand : BotCommand {
    override val name = "config"
    override val description = "Configura canais importantes do servidor."
    override val usage = "config setchannel <logs|welcome|suggestions|reports> <#canal>"
    override val aliases = setOf("setchannel")

    override suspend fun execute(context: CommandContext) {
        if (!context.requireGuildMessage()) return
        if (!context.requireModeratorPermission()) return

        val guildId = context.event.message.getGuild().id.value

        if (context.args.isEmpty()) {
            val settings = context.services.settingsStore.get(guildId)
            context.reply(
                """
                Configuracao atual:
                logs: ${formatId(settings.logsChannelId)}
                welcome: ${formatId(settings.welcomeChannelId)}
                suggestions: ${formatId(settings.suggestionsChannelId)}
                reports: ${formatId(settings.reportsChannelId)}
                autorole: ${settings.autoRoleId?.let { "<@&$it>" } ?: "nao definido"}
                """.trimIndent()
            )
            return
        }

        val mode = context.args.first().lowercase()
        if (mode != "setchannel") {
            context.reply("Uso: ${context.usage(this)}")
            return
        }

        val channelType = context.args.getOrNull(1)?.let(ChannelSetting::fromInput)
            ?: return context.reply("Tipos validos: logs, welcome, suggestions, reports.")

        val channelId = context.args.getOrNull(2)?.let(::parseChannelId)
            ?: return context.reply("Informe um canal valido usando mencao ou ID.")

        context.services.settingsStore.setChannel(guildId, channelType, channelId)
        context.reply("Canal `${channelType.key}` configurado para <#$channelId>.")
    }

    private fun parseChannelId(value: String): ULong? {
        return value.removePrefix("<#").removeSuffix(">").toULongOrNull()
            ?: value.toULongOrNull()
    }

    private fun formatId(id: ULong?): String {
        return id?.let { "<#$it>" } ?: "nao definido"
    }
}
