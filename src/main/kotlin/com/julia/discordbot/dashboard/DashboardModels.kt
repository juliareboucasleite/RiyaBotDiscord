package com.julia.discordbot.dashboard

import kotlinx.serialization.Serializable

// ===== API Responses =====

@Serializable
data class BotStatusResponse(
    val botName: String,
    val botAvatar: String?,
    val guildCount: Int,
    val uptime: String,
    val status: String
)

@Serializable
data class GuildInfoResponse(
    val id: String,
    val name: String,
    val memberCount: Int,
    val iconUrl: String?
)

@Serializable
data class GuildSettingsResponse(
    val guildId: String,
    val guildName: String,
    val prefix: String,
    val logsChannelId: String?,
    val welcomeChannelId: String?,
    val suggestionsChannelId: String?,
    val reportsChannelId: String?,
    val autoRoleId: String?,
    val welcomeEmbed: EmbedConfigResponse?,
    val disabledCommands: List<String>
)

@Serializable
data class EmbedConfigResponse(
    val enabled: Boolean = false,
    val title: String = "",
    val description: String = "",
    val color: String = "#7c3aed",
    val footer: String = "",
    val thumbnailUrl: String = "",
    val imageUrl: String = ""
)

@Serializable
data class ChannelResponse(
    val id: String,
    val name: String,
    val type: String
)

@Serializable
data class RoleResponse(
    val id: String,
    val name: String,
    val color: String
)

@Serializable
data class WarningResponse(
    val guildId: String,
    val userId: String,
    val userName: String?,
    val moderatorId: String,
    val moderatorName: String?,
    val reason: String,
    val createdAt: String
)

@Serializable
data class DashboardStats(
    val totalGuilds: Int,
    val totalMembers: Int,
    val totalCommands: Int,
    val botName: String,
    val botAvatar: String?
)

@Serializable
data class CommandInfoResponse(
    val name: String,
    val description: String,
    val usage: String,
    val aliases: List<String>,
    val category: String,
    val enabled: Boolean
)

// ===== API Requests =====

@Serializable
data class UpdateChannelSettingRequest(
    val channelType: String,
    val channelId: String?
)

@Serializable
data class UpdateAutoRoleRequest(
    val roleId: String?
)

@Serializable
data class UpdatePrefixRequest(
    val prefix: String
)

@Serializable
data class UpdateWelcomeEmbedRequest(
    val enabled: Boolean,
    val title: String,
    val description: String,
    val color: String,
    val footer: String,
    val thumbnailUrl: String,
    val imageUrl: String
)

@Serializable
data class SendEmbedRequest(
    val channelId: String,
    val title: String,
    val description: String,
    val color: String,
    val footer: String,
    val thumbnailUrl: String,
    val imageUrl: String
)

@Serializable
data class ToggleCommandRequest(
    val commandName: String,
    val enabled: Boolean
)

@Serializable
data class ApiSuccessResponse(
    val success: Boolean = true,
    val message: String = "OK"
)
