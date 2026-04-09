package com.julia.discordbot

import com.julia.discordbot.commands.BotCommand
import dev.kord.core.Kord
import dev.kord.core.entity.User
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.flow.firstOrNull

data class CommandContext(
    val kord: Kord,
    val event: MessageCreateEvent,
    val args: List<String>,
    val registry: CommandRegistry,
    val services: BotServices
) {
    suspend fun reply(content: String) {
        event.message.channel.createMessage(content)
    }

    fun usage(command: BotCommand): String = "`!${command.usage}`"

    fun joinedArgs(fromIndex: Int = 0): String {
        return args.drop(fromIndex).joinToString(" ").trim()
    }

    suspend fun resolveTargetUser(): User? {
        return event.message.mentionedUsers.firstOrNull() ?: event.message.author
    }
}
