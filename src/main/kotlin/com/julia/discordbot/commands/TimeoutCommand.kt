package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import com.julia.discordbot.requireGuildMessage
import com.julia.discordbot.requireModeratorPermission
import dev.kord.core.behavior.edit
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.firstOrNull

class TimeoutCommand : BotCommand {
    override val name = "timeout"
    override val description = "Aplica timeout em um membro por alguns minutos."
    override val usage = "timeout <@usuario> <minutos>"
    override val aliases = setOf("mute", "unmute")

    override suspend fun execute(context: CommandContext) {
        if (!context.requireGuildMessage()) return
        if (!context.requireModeratorPermission()) return

        val member = context.event.message.mentionedUsers.firstOrNull()?.asMember(context.event.message.getGuild().id)
            ?: return context.reply("Uso: ${context.usage(this)}")

        val invokedAs = context.event.message.content.removePrefix("!").substringBefore(" ").lowercase()
        if (invokedAs == "unmute") {
            member.edit {
                communicationDisabledUntil = null
            }
            context.reply("Timeout removido de ${member.mention}.")
            return
        }

        val minutes = context.args.getOrNull(1)?.toIntOrNull()
            ?: return context.reply("Informe a duracao em minutos.")

        member.edit {
            communicationDisabledUntil = Clock.System.now() + minutes.minutes
        }

        context.reply("${member.mention} recebeu timeout de `$minutes` minuto(s).")
    }
}
