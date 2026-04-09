package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import com.julia.discordbot.requireGuildMessage
import com.julia.discordbot.requireModeratorPermission

class AutoRoleCommand : BotCommand {
    override val name = "autorole"
    override val description = "Define ou remove o cargo automatico para novos membros."
    override val usage = "autorole <@cargo|id|off>"
    override val aliases = setOf("setautorole")

    override suspend fun execute(context: CommandContext) {
        if (!context.requireGuildMessage()) return
        if (!context.requireModeratorPermission()) return

        val guildId = context.event.message.getGuild().id.value
        val value = context.args.firstOrNull()?.lowercase()
            ?: return context.reply("Uso: ${context.usage(this)}")

        if (value == "off") {
            context.services.settingsStore.clearAutoRole(guildId)
            context.reply("Autorole removido.")
            return
        }

        val roleId = parseRoleId(context.args.first())
            ?: return context.reply("Informe um cargo valido: mencao ou ID.")

        context.services.settingsStore.setAutoRole(guildId, roleId)
        context.reply("Autorole definido para o cargo `$roleId`.")
    }

    private fun parseRoleId(value: String): ULong? {
        return value.removePrefix("<@&").removeSuffix(">").toULongOrNull()
            ?: value.toULongOrNull()
    }
}
