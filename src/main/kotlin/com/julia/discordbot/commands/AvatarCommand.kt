package com.julia.discordbot.commands

import com.julia.discordbot.CommandContext

class AvatarCommand : BotCommand {
    override val name = "avatar"
    override val description = "Mostra o avatar do usuario informado ou do autor."
    override val usage = "avatar [@usuario]"
    override val aliases = emptySet<String>()

    override suspend fun execute(context: CommandContext) {
        val user = context.resolveTargetUser()
            ?: return context.reply("Nao consegui resolver o usuario.")

        val avatarUrl = user.avatar?.cdnUrl?.toUrl()
            ?: user.defaultAvatar.cdnUrl.toUrl()

        context.reply("Avatar de **${user.tag}**: $avatarUrl")
    }
}
