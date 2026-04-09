package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import com.julia.discordbot.requireGuildMessage
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.MessageChannel
import kotlinx.coroutines.flow.firstOrNull

class ReportCommand : BotCommand {
    override val name = "report"
    override val description = "Envia um reporte para o canal configurado."
    override val usage = "report <@usuario> <motivo>"
    override val aliases = setOf("ticket")

    override suspend fun execute(context: CommandContext) {
        if (!context.requireGuildMessage()) return

        val guildId = context.event.message.getGuild().id.value
        val settings = context.services.settingsStore.get(guildId)
        val channelId = settings.reportsChannelId ?: settings.logsChannelId
            ?: return context.reply("Configure `reports` ou `logs` com `!config setchannel ...` antes de usar report.")

        val target = context.event.message.mentionedUsers.firstOrNull()
            ?: return context.reply("Uso: ${context.usage(this)}")
        val reason = context.joinedArgs(1)
        if (reason.isBlank()) {
            context.reply("Informe o motivo do report.")
            return
        }

        val channel = context.kord.getChannelOf<MessageChannel>(Snowflake(channelId))
            ?: return context.reply("Nao consegui acessar o canal de reports configurado.")

        channel.createMessage(
            """
            Novo report recebido
            Autor: ${context.event.message.author?.mention}
            Alvo: ${target.mention}
            Motivo: $reason
            Canal de origem: ${context.event.message.channel.mention}
            """.trimIndent()
        )

        context.reply("Report enviado para a equipe.")
    }
}
