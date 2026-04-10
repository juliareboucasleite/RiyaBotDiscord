package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import kotlinx.coroutines.flow.firstOrNull

class DenyPasswordCommand : BotCommand {
    override val name = "negarsenha"
    override val description = "Recusa um pedido pendente para mostrar contas tuas por nota."
    override val usage = "negarsenha <@utilizador> <nota>"
    override val aliases = setOf("recusarsenha")

    override suspend fun execute(context: CommandContext) {
        val owner = context.event.message.author
            ?: return context.reply("Nao consegui identificar o utilizador.")

        val requester = context.event.message.mentionedUsers.firstOrNull()
            ?: return context.reply("Uso: ${context.usage(this)}")
        val note = context.args.drop(1).joinToString(" ").trim()
        if (note.isBlank()) {
            context.reply("Uso: ${context.usage(this)}")
            return
        }

        val removed = context.services.passwordStore.removeShareRequest(owner.id.value, requester.id.value, note)
        if (!removed) {
            context.reply("Nao encontrei um pedido pendente desse utilizador para `$note`.")
            return
        }

        context.reply("Pedido para `${requester.username}` recusado.")
    }
}
