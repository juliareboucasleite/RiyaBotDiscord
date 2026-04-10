package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import com.julia.discordbot.PasswordEntry
import kotlinx.coroutines.flow.firstOrNull

class ShowPasswordCommand : BotCommand {
    override val name = "mostrar"
    override val description = "Mostra todas as tuas contas por nota ou pede permissao ao dono para ver a de outro utilizador."
    override val usage = "mostrar <nota> [@utilizador]"
    override val aliases = setOf("senha", "versenha")

    override suspend fun execute(context: CommandContext) {
        val requester = context.event.message.author
            ?: return context.reply("Nao consegui identificar o utilizador.")

        val note = context.args.getOrNull(0)?.trim().orEmpty()
        if (note.isBlank()) {
            context.reply("Uso: ${context.usage(this)}")
            return
        }

        val ownEntries = context.services.passwordStore.findByOwnerAndNote(requester.id.value, note)
        if (ownEntries.isNotEmpty()) {
            context.reply(formatEntries(ownEntries))
            return
        }

        val mentionedUser = context.event.message.mentionedUsers.firstOrNull()
        val allMatches = context.services.passwordStore.findByNote(note).filter { it.ownerId != requester.id.value }
        if (allMatches.isEmpty()) {
            context.reply("Nao encontrei nenhuma conta com a nota `$note`.")
            return
        }

        val targetEntries = when {
            mentionedUser != null -> allMatches.filter { it.ownerId == mentionedUser.id.value }
                .takeIf { it.isNotEmpty() }
                ?: return context.reply("Esse utilizador nao tem nenhuma conta guardada com a nota `$note`.")

            allMatches.map { it.ownerId }.distinct().size == 1 -> allMatches
            else -> {
                context.reply("Existe mais de um utilizador com essa nota guardada. Usa `${context.usage(this)}` mencionando o dono.")
                return
            }
        }

        context.services.passwordStore.createShareRequest(
            ownerId = targetEntries.first().ownerId,
            requesterId = requester.id.value,
            note = targetEntries.first().note
        )

        val ownerMention = mentionedUser?.mention ?: "<@${targetEntries.first().ownerId}>"
        context.reply(
            """
            Pedido de acesso enviado para $ownerMention.
            O dono pode aprovar com `!aprovarsenha @${requester.username} ${targetEntries.first().note}`
            ou negar com `!negarsenha @${requester.username} ${targetEntries.first().note}`.
            """.trimIndent()
        )
    }

    private fun formatEntries(entries: List<PasswordEntry>): String {
        return buildString {
            appendLine("Contas encontradas:")
            entries.forEachIndexed { index, entry ->
                appendLine("${index + 1}. Nota: `${entry.note}` | Nome: `${entry.name}` | Senha: `${entry.password}`")
            }
        }.trim()
    }
}
