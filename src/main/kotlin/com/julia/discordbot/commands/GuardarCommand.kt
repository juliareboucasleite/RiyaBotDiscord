package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext

class GuardarCommand : BotCommand {
    override val name = "guardar"
    override val description = "Guarda uma conta tua com nota, nome e senha."
    override val usage = "guardar <nota> <nome> <senha>"
    override val aliases = setOf("savepass")

    override suspend fun execute(context: CommandContext) {
        val author = context.event.message.author
            ?: return context.reply("Nao consegui identificar o utilizador.")

        val note = context.args.getOrNull(0)?.trim().orEmpty()
        val name = context.args.getOrNull(1)?.trim().orEmpty()
        val password = context.joinedArgs(2)

        if (note.isBlank() || name.isBlank() || password.isBlank()) {
            context.reply("Uso: ${context.usage(this)}")
            return
        }

        context.services.passwordStore.save(author.id.value, note, name, password)
        context.reply("Conta guardada com a nota `$note`.")
    }
}
