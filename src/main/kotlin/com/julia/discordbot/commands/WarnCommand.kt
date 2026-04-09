package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import com.julia.discordbot.WarningEntry
import com.julia.discordbot.requireGuildMessage
import com.julia.discordbot.requireModeratorPermission
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant

class WarnCommand : BotCommand {
    override val name = "warn"
    override val description = "Registra um aviso simples para um membro."
    override val usage = "warn <@usuario> <motivo>"
    override val aliases = emptySet<String>()

    override suspend fun execute(context: CommandContext) {
        if (!context.requireGuildMessage()) return
        if (!context.requireModeratorPermission()) return

        val target = context.event.message.mentionedUsers.firstOrNull()
            ?: return context.reply("Uso: ${context.usage(this)}")
        val reason = context.joinedArgs(1)
        if (reason.isBlank()) {
            context.reply("Informe o motivo do aviso.")
            return
        }

        val guildId = context.event.message.getGuild().id.value
        val moderatorId = context.event.message.author?.id?.value
            ?: return context.reply("Nao consegui identificar o moderador.")

        context.services.warningStore.add(
            WarningEntry(
                guildId = guildId,
                userId = target.id.value,
                moderatorId = moderatorId,
                reason = reason,
                createdAt = Instant.now()
            )
        )

        val totalWarnings = context.services.warningStore.countWarnings(guildId, target.id.value)
        context.reply("${target.mention} recebeu um aviso. Total registrado: `$totalWarnings`.")
    }
}
