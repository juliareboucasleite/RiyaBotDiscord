package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import kotlinx.coroutines.flow.firstOrNull

class ApprovePasswordCommand : BotCommand {
    override val name = "aprovarsenha"
    override val description = "Aprova um pedido pendente para mostrar contas tuas por nota."
    override val usage = "aprovarsenha <@utilizador> <nota>"
    override val aliases = setOf("permitirsenha")

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

        val request = context.services.passwordStore.takeShareRequest(owner.id.value, requester.id.value, note)
            ?: return context.reply("Nao encontrei um pedido pendente desse utilizador para `$note`.")

        val entries = context.services.passwordStore.findByOwnerAndNote(owner.id.value, request.note)
        if (entries.isEmpty()) {
            return context.reply("Nao encontrei a conta `$note` para aprovar.")
        }

        val message = buildString {
            appendLine("Pedido aprovado.")
            appendLine("${requester.mention} pode ver:")
            entries.forEachIndexed { index, entry ->
                appendLine("${index + 1}. Nota: `${entry.note}` | Nome: `${entry.name}` | Senha: `${entry.password}`")
            }
        }
        context.reply(message.trim())
    }
}
