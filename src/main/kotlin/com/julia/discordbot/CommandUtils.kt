package com.julia.discordbot

import dev.kord.common.entity.Permission

suspend fun CommandContext.requireGuildMessage(): Boolean {
    val guildId = event.message.data.guildId.value
    if (guildId == null) {
        reply("Esse comando so pode ser usado dentro de um servidor.")
        return false
    }
    return true
}

suspend fun CommandContext.requireModeratorPermission(): Boolean {
    val member = event.message.getAuthorAsMemberOrNull()
    if (member == null) {
        reply("Nao consegui validar suas permissoes.")
        return false
    }

    val permissions = member.getPermissions()
    val allowed = Permission.ManageMessages in permissions || Permission.Administrator in permissions
    if (!allowed) {
        reply("Voce precisa de permissao de moderacao para usar esse comando.")
    }

    return allowed
}
