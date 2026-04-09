package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext

class UserInfoCommand : BotCommand {
    override val name = "userinfo"
    override val description = "Mostra informacoes basicas do usuario."
    override val usage = "userinfo [@usuario]"
    override val aliases = setOf("user")

    override suspend fun execute(context: CommandContext) {
        val user = context.resolveTargetUser()
            ?: return context.reply("Nao consegui resolver o usuario.")

        context.reply(
            """
            Usuario: **${user.tag}**
            ID: `${user.id.value}`
            Bot: `${user.isBot}`
            Avatar: ${user.avatar?.cdnUrl?.toUrl() ?: user.defaultAvatar.cdnUrl.toUrl()}
            """.trimIndent()
        )
    }
}
