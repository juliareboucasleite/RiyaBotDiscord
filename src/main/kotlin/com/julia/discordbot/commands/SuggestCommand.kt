package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext
import com.julia.discordbot.requireGuildMessage
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.MessageChannel

class SuggestCommand : BotCommand {
    override val name = "suggest"
    override val description = "Envia uma sugestao para o canal configurado."
    override val usage = "suggest <mensagem>"
    override val aliases = setOf("sugestao")

    override suspend fun execute(context: CommandContext) {
        if (!context.requireGuildMessage()) return

        val suggestion = context.joinedArgs()
        if (suggestion.isBlank()) {
            context.reply("Uso: ${context.usage(this)}")
            return
        }

        val guildId = context.event.message.getGuild().id.value
        val channelId = context.services.settingsStore.get(guildId).suggestionsChannelId
            ?: return context.reply("Configure o canal de sugestoes com `!config setchannel suggestions #canal`.")

        val channel = context.kord.getChannelOf<MessageChannel>(Snowflake(channelId))
            ?: return context.reply("Nao consegui acessar o canal de sugestoes configurado.")

        channel.createMessage(
            """
            Nova sugestao
            Autor: ${context.event.message.author?.mention}
            Mensagem: $suggestion
            """.trimIndent()
        )

        context.reply("Sugestao enviada.")
    }
}
