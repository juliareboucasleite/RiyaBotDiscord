package com.julia.discordbot

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on

private const val COMMAND_PREFIX = "!"

fun registerHandlers(kord: Kord, commandRegistry: CommandRegistry, services: BotServices) {
    registerMessageHandler(kord, commandRegistry, services)
    registerMemberJoinHandler(kord, services)
}

fun registerMessageHandler(kord: Kord, commandRegistry: CommandRegistry, services: BotServices) {
    kord.on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on

        val content = message.content.trim()
        if (!content.startsWith(COMMAND_PREFIX)) return@on

        val rawCommand = content.removePrefix(COMMAND_PREFIX)
        val commandName = rawCommand.substringBefore(" ").lowercase()
        val args = rawCommand
            .substringAfter(" ", "")
            .trim()
            .split(" ")
            .filter { it.isNotBlank() }

        val command = commandRegistry.find(commandName) ?: return@on
        command.execute(CommandContext(kord, this, args, commandRegistry, services))
    }
}

fun registerMemberJoinHandler(kord: Kord, services: BotServices) {
    kord.on<MemberJoinEvent> {
        val settings = services.settingsStore.get(guildId.value)
        val autoRoleId = settings.autoRoleId ?: return@on
        member.addRole(Snowflake(autoRoleId), "Cargo automatico configurado no bot")
    }
}
