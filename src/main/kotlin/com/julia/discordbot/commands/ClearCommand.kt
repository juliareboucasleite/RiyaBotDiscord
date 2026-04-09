package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import com.julia.discordbot.requireGuildMessage
import com.julia.discordbot.requireModeratorPermission
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.flow.toList

class ClearCommand : BotCommand {
    override val name = "clear"
    override val description = "Apaga ate 100 mensagens recentes do canal."
    override val usage = "clear <1-100>"
    override val aliases = setOf("purge")

    override suspend fun execute(context: CommandContext) {
        if (!context.requireGuildMessage()) return
        if (!context.requireModeratorPermission()) return

        val amount = context.args.firstOrNull()?.toIntOrNull()
            ?: return context.reply("Uso: ${context.usage(this)}")

        if (amount !in 1..100) {
            context.reply("Informe uma quantidade entre 1 e 100.")
            return
        }

        val channel = context.event.message.channel.asChannelOf<TextChannel>()
        val messages = channel.getMessagesBefore(context.event.message.id, amount).toList()
        if (messages.isEmpty()) {
            context.reply("Nenhuma mensagem encontrada para apagar.")
            return
        }

        val messageIds = messages.map { it.id }
        if (messageIds.size == 1) {
            channel.deleteMessage(messageIds.first(), "Limpeza solicitada por comando")
        } else {
            channel.bulkDelete(messageIds, true, "Limpeza solicitada por comando")
        }
        context.event.message.delete("Removendo comando de limpeza")
    }
}
