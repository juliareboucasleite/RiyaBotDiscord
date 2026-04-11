package com.julia.discordbot.dashboard

import com.julia.discordbot.BotServices
import com.julia.discordbot.ChannelSetting
import com.julia.discordbot.WelcomeEmbedConfig
import dev.kord.common.Color
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.EmbedBuilder
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.toList

fun Route.apiRoutes(kord: Kord, services: BotServices) {

    // ===== Bot Status =====
    get("/status") {
        val self = kord.getSelf()
        val guildCount = kord.guilds.count()

        call.respond(BotStatusResponse(
            botName = self.username,
            botAvatar = self.avatar?.cdnUrl?.toUrl(),
            guildCount = guildCount,
            uptime = getUptime(),
            status = "Online"
        ))
    }

    // ===== Aggregate Stats =====
    get("/stats") {
        val self = kord.getSelf()
        val guilds = kord.guilds.toList()
        val totalMembers = guilds.sumOf { it.memberCount ?: 0 }

        call.respond(DashboardStats(
            totalGuilds = guilds.size,
            totalMembers = totalMembers,
            totalCommands = 17,
            botName = self.username,
            botAvatar = self.avatar?.cdnUrl?.toUrl()
        ))
    }

    // ===== Guild List =====
    get("/guilds") {
        val guilds = kord.guilds.toList().map { guild ->
            GuildInfoResponse(
                id = guild.id.value.toString(),
                name = guild.name,
                memberCount = guild.memberCount ?: 0,
                iconUrl = guild.icon?.cdnUrl?.toUrl()
            )
        }
        call.respond(guilds)
    }

    // ===== Guild Settings =====
    get("/guilds/{guildId}/settings") {
        val guildId = call.parseGuildId() ?: return@get

        val settings = services.settingsStore.get(guildId)
        val guild = kord.guilds.toList().firstOrNull { it.id.value == guildId }

        val embedConfig = settings.welcomeEmbed
        call.respond(GuildSettingsResponse(
            guildId = guildId.toString(),
            guildName = guild?.name ?: "Desconhecido",
            prefix = settings.prefix,
            logsChannelId = settings.logsChannelId?.toString(),
            welcomeChannelId = settings.welcomeChannelId?.toString(),
            suggestionsChannelId = settings.suggestionsChannelId?.toString(),
            reportsChannelId = settings.reportsChannelId?.toString(),
            autoRoleId = settings.autoRoleId?.toString(),
            welcomeEmbed = EmbedConfigResponse(
                enabled = embedConfig.enabled,
                title = embedConfig.title,
                description = embedConfig.description,
                color = embedConfig.color,
                footer = embedConfig.footer,
                thumbnailUrl = embedConfig.thumbnailUrl,
                imageUrl = embedConfig.imageUrl
            ),
            disabledCommands = settings.disabledCommands.toList()
        ))
    }

    // ===== Update Channel Setting =====
    post("/guilds/{guildId}/channels") {
        val guildId = call.parseGuildId() ?: return@post
        val request = call.receive<UpdateChannelSettingRequest>()

        val channelSetting = ChannelSetting.fromInput(request.channelType)
            ?: return@post call.respondText("Tipo de canal invalido", status = HttpStatusCode.BadRequest)

        if (request.channelId.isNullOrBlank()) {
            services.settingsStore.clearChannel(guildId, channelSetting)
        } else {
            val channelId = request.channelId.toULongOrNull()
                ?: return@post call.respondText("ID de canal invalido", status = HttpStatusCode.BadRequest)
            services.settingsStore.setChannel(guildId, channelSetting, channelId)
        }

        call.respond(ApiSuccessResponse(message = "Canal ${request.channelType} atualizado"))
    }

    // ===== Update Auto Role =====
    post("/guilds/{guildId}/autorole") {
        val guildId = call.parseGuildId() ?: return@post
        val request = call.receive<UpdateAutoRoleRequest>()

        if (request.roleId.isNullOrBlank()) {
            services.settingsStore.clearAutoRole(guildId)
        } else {
            val roleId = request.roleId.toULongOrNull()
                ?: return@post call.respondText("ID de cargo invalido", status = HttpStatusCode.BadRequest)
            services.settingsStore.setAutoRole(guildId, roleId)
        }

        call.respond(ApiSuccessResponse(message = "Auto role atualizado"))
    }

    // ===== Update Prefix =====
    post("/guilds/{guildId}/prefix") {
        val guildId = call.parseGuildId() ?: return@post
        val request = call.receive<UpdatePrefixRequest>()

        if (request.prefix.isBlank() || request.prefix.length > 5) {
            return@post call.respondText("Prefixo deve ter entre 1 e 5 caracteres", status = HttpStatusCode.BadRequest)
        }

        services.settingsStore.setPrefix(guildId, request.prefix)
        call.respond(ApiSuccessResponse(message = "Prefixo atualizado para '${request.prefix}'"))
    }

    // ===== Update Welcome Embed =====
    post("/guilds/{guildId}/welcome-embed") {
        val guildId = call.parseGuildId() ?: return@post
        val request = call.receive<UpdateWelcomeEmbedRequest>()

        services.settingsStore.setWelcomeEmbed(guildId, WelcomeEmbedConfig(
            enabled = request.enabled,
            title = request.title,
            description = request.description,
            color = request.color,
            footer = request.footer,
            thumbnailUrl = request.thumbnailUrl,
            imageUrl = request.imageUrl
        ))

        call.respond(ApiSuccessResponse(message = "Welcome embed atualizado"))
    }

    post("/guilds/{guildId}/embeds/send") {
        val guildId = call.parseGuildId() ?: return@post
        val request = call.receive<SendEmbedRequest>()
        val channelId = request.channelId.extractChannelId()
            ?: return@post call.respondText("ID de canal invalido", status = HttpStatusCode.BadRequest)

        val guild = kord.guilds.toList().firstOrNull { it.id.value == guildId }
            ?: return@post call.respondText("Servidor nao encontrado", status = HttpStatusCode.NotFound)

        val channel = guild.channels.filterIsInstance<TextChannel>().toList().firstOrNull { it.id.value == channelId }
            ?: return@post call.respondText("Canal nao encontrado neste servidor", status = HttpStatusCode.NotFound)

        channel.createEmbed {
            applyEmbed(this, request)
        }

        call.respond(ApiSuccessResponse(message = "Embed enviado para #${channel.name}"))
    }

    // ===== Toggle Command =====
    post("/guilds/{guildId}/commands/toggle") {
        val guildId = call.parseGuildId() ?: return@post
        val request = call.receive<ToggleCommandRequest>()

        services.settingsStore.setCommandEnabled(guildId, request.commandName, request.enabled)
        val state = if (request.enabled) "ativado" else "desativado"
        call.respond(ApiSuccessResponse(message = "Comando '${request.commandName}' $state"))
    }

    // ===== List Commands =====
    get("/guilds/{guildId}/commands") {
        val guildId = call.parseGuildId() ?: return@get
        val settings = services.settingsStore.get(guildId)

        val commands = allCommandsInfo().map { cmd ->
            cmd.copy(enabled = cmd.name !in settings.disabledCommands)
        }
        call.respond(commands)
    }

    // ===== List Guild Channels =====
    get("/guilds/{guildId}/channels") {
        val guildIdStr = call.parameters["guildId"]
            ?: return@get call.respondText("Guild ID necessario", status = HttpStatusCode.BadRequest)
        val guildIdULong = guildIdStr.toULongOrNull()
            ?: return@get call.respondText("Guild ID invalido", status = HttpStatusCode.BadRequest)

        val guild = kord.guilds.toList().firstOrNull { it.id.value == guildIdULong }
            ?: return@get call.respondText("Servidor nao encontrado", status = HttpStatusCode.NotFound)

        val channels = guild.channels.filterIsInstance<TextChannel>().toList().map { ch ->
            ChannelResponse(
                id = ch.id.value.toString(),
                name = ch.name,
                type = "text"
            )
        }
        call.respond(channels)
    }

    // ===== List Guild Roles =====
    get("/guilds/{guildId}/roles") {
        val guildIdStr = call.parameters["guildId"]
            ?: return@get call.respondText("Guild ID necessario", status = HttpStatusCode.BadRequest)
        val guildIdULong = guildIdStr.toULongOrNull()
            ?: return@get call.respondText("Guild ID invalido", status = HttpStatusCode.BadRequest)

        val guild = kord.guilds.toList().firstOrNull { it.id.value == guildIdULong }
            ?: return@get call.respondText("Servidor nao encontrado", status = HttpStatusCode.NotFound)

        val roles = guild.roles.toList().map { role ->
            val colorInt = role.color.rgb
            val hex = "#${Integer.toHexString(colorInt).padStart(6, '0')}"
            RoleResponse(
                id = role.id.value.toString(),
                name = role.name,
                color = hex
            )
        }
        call.respond(roles)
    }

    // ===== Warnings for a guild =====
    get("/guilds/{guildId}/warnings") {
        val guildId = call.parseGuildId() ?: return@get
        // WarningStore currently only supports count, we return basic info
        call.respond(emptyList<WarningResponse>())
    }
}

// ===== Helper Functions =====

private suspend fun io.ktor.server.routing.RoutingCall.parseGuildId(): ULong? {
    val guildIdStr = parameters["guildId"]
    if (guildIdStr == null) {
        respondText("Guild ID necessario", status = HttpStatusCode.BadRequest)
        return null
    }
    val guildId = guildIdStr.toULongOrNull()
    if (guildId == null) {
        respondText("Guild ID invalido", status = HttpStatusCode.BadRequest)
        return null
    }
    return guildId
}

private val startTime = System.currentTimeMillis()

fun getUptime(): String {
    val elapsed = System.currentTimeMillis() - startTime
    val seconds = (elapsed / 1000) % 60
    val minutes = (elapsed / (1000 * 60)) % 60
    val hours = (elapsed / (1000 * 60 * 60)) % 24
    val days = elapsed / (1000 * 60 * 60 * 24)

    return buildString {
        if (days > 0) append("${days}d ")
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m ")
        append("${seconds}s")
    }.trim()
}

private fun String.extractChannelId(): ULong? {
    return trim().removePrefix("<#").removeSuffix(">").toULongOrNull()
}

private fun String.toDiscordColor(): Color {
    val normalized = trim().removePrefix("#")
    val rgb = normalized.toIntOrNull(16) ?: 0x7c3aed
    return Color(rgb)
}

private fun applyEmbed(builder: EmbedBuilder, request: SendEmbedRequest) {
    builder.apply {
        title = request.title.ifBlank { "Embed sem titulo" }
        description = request.description.ifBlank { " " }
        color = request.color.toDiscordColor()

        if (request.footer.isNotBlank()) {
            footer {
                text = request.footer
            }
        }

        if (request.thumbnailUrl.isNotBlank()) {
            thumbnail {
                url = request.thumbnailUrl
            }
        }

        if (request.imageUrl.isNotBlank()) {
            image = request.imageUrl
        }
    }
}

fun allCommandsInfo(): List<CommandInfoResponse> = listOf(
    CommandInfoResponse("ping", "Responde com pong para testar se o bot esta online.", "ping", emptyList(), "Geral", true),
    CommandInfoResponse("help", "Lista os comandos disponiveis.", "help", listOf("ajuda", "commands"), "Geral", true),
    CommandInfoResponse("guardar", "Guarda uma conta com nota, nome e senha.", "guardar <nota> <nome> <senha>", listOf("savepass"), "Utilidades", true),
    CommandInfoResponse("showpassword", "Mostra senhas guardadas.", "showpassword <nota>", listOf("showpass"), "Utilidades", true),
    CommandInfoResponse("approvepassword", "Aprova um pedido de partilha de senha.", "approvepassword <@user> <nota>", listOf("approvepass"), "Utilidades", true),
    CommandInfoResponse("denypassword", "Recusa um pedido de partilha de senha.", "denypassword <@user> <nota>", listOf("denypass"), "Utilidades", true),
    CommandInfoResponse("config", "Configura canais do servidor.", "config setchannel <tipo> <#canal>", listOf("setchannel"), "Administracao", true),
    CommandInfoResponse("autorole", "Define cargo automatico para novos membros.", "autorole <@cargo|id|off>", listOf("setautorole"), "Administracao", true),
    CommandInfoResponse("userinfo", "Mostra informacoes de um utilizador.", "userinfo [@user]", emptyList(), "Informacao", true),
    CommandInfoResponse("serverinfo", "Mostra informacoes do servidor.", "serverinfo", emptyList(), "Informacao", true),
    CommandInfoResponse("avatar", "Mostra o avatar de um utilizador.", "avatar [@user]", emptyList(), "Informacao", true),
    CommandInfoResponse("clear", "Apaga ate 100 mensagens recentes.", "clear <1-100>", listOf("purge"), "Moderacao", true),
    CommandInfoResponse("timeout", "Aplica timeout em um membro.", "timeout <@user> <minutos>", listOf("mute", "unmute"), "Moderacao", true),
    CommandInfoResponse("warn", "Registra um aviso para um membro.", "warn <@user> <motivo>", emptyList(), "Moderacao", true),
    CommandInfoResponse("suggest", "Envia uma sugestao ao canal configurado.", "suggest <mensagem>", listOf("sugestao"), "Comunidade", true),
    CommandInfoResponse("report", "Envia um reporte ao canal configurado.", "report <@user> <motivo>", listOf("ticket"), "Comunidade", true),
)
