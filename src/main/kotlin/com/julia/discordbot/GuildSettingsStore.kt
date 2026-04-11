package com.julia.discordbot

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

data class WelcomeEmbedConfig(
    val enabled: Boolean = false,
    val title: String = "Bem-vindo(a)!",
    val description: String = "Bem-vindo(a) ao servidor, {user}!",
    val color: String = "#7c3aed",
    val footer: String = "",
    val thumbnailUrl: String = "",
    val imageUrl: String = ""
)

data class GuildSettings(
    val prefix: String = "!",
    val logsChannelId: ULong? = null,
    val welcomeChannelId: ULong? = null,
    val suggestionsChannelId: ULong? = null,
    val reportsChannelId: ULong? = null,
    val autoRoleId: ULong? = null,
    val welcomeEmbed: WelcomeEmbedConfig = WelcomeEmbedConfig(),
    val disabledCommands: Set<String> = emptySet()
)

enum class ChannelSetting(val key: String) {
    LOGS("logs"),
    WELCOME("welcome"),
    SUGGESTIONS("suggestions"),
    REPORTS("reports");

    companion object {
        fun fromInput(value: String): ChannelSetting? {
            return entries.firstOrNull { it.key == value.lowercase() }
        }
    }
}

class GuildSettingsStore(dataDirectory: Path) {
    private val filePath = dataDirectory.resolve("guild-settings.properties")

    init {
        Files.createDirectories(dataDirectory)
        if (!Files.exists(filePath)) {
            Files.createFile(filePath)
        }
    }

    @Synchronized
    fun get(guildId: ULong): GuildSettings {
        val properties = loadProperties()

        fun readULong(key: String): ULong? {
            return properties.getProperty("${guildId}.$key")?.toULongOrNull()
        }

        fun readString(key: String, default: String = ""): String {
            return properties.getProperty("${guildId}.$key") ?: default
        }

        fun readBoolean(key: String, default: Boolean = false): Boolean {
            return properties.getProperty("${guildId}.$key")?.toBooleanStrictOrNull() ?: default
        }

        val disabledRaw = readString("disabledCommands")
        val disabledCommands = if (disabledRaw.isBlank()) emptySet()
        else disabledRaw.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()

        return GuildSettings(
            prefix = readString("prefix", "!").ifBlank { "!" },
            logsChannelId = readULong("logsChannelId"),
            welcomeChannelId = readULong("welcomeChannelId"),
            suggestionsChannelId = readULong("suggestionsChannelId"),
            reportsChannelId = readULong("reportsChannelId"),
            autoRoleId = readULong("autoRoleId"),
            welcomeEmbed = WelcomeEmbedConfig(
                enabled = readBoolean("welcomeEmbed.enabled"),
                title = readString("welcomeEmbed.title", "Bem-vindo(a)!"),
                description = readString("welcomeEmbed.description", "Bem-vindo(a) ao servidor, {user}!"),
                color = readString("welcomeEmbed.color", "#7c3aed"),
                footer = readString("welcomeEmbed.footer"),
                thumbnailUrl = readString("welcomeEmbed.thumbnailUrl"),
                imageUrl = readString("welcomeEmbed.imageUrl")
            ),
            disabledCommands = disabledCommands
        )
    }

    @Synchronized
    fun setChannel(guildId: ULong, channelSetting: ChannelSetting, channelId: ULong) {
        val properties = loadProperties()
        val propertyKey = when (channelSetting) {
            ChannelSetting.LOGS -> "logsChannelId"
            ChannelSetting.WELCOME -> "welcomeChannelId"
            ChannelSetting.SUGGESTIONS -> "suggestionsChannelId"
            ChannelSetting.REPORTS -> "reportsChannelId"
        }
        properties.setProperty("${guildId}.$propertyKey", channelId.toString())
        saveProperties(properties)
    }

    @Synchronized
    fun clearChannel(guildId: ULong, channelSetting: ChannelSetting) {
        val properties = loadProperties()
        val propertyKey = when (channelSetting) {
            ChannelSetting.LOGS -> "logsChannelId"
            ChannelSetting.WELCOME -> "welcomeChannelId"
            ChannelSetting.SUGGESTIONS -> "suggestionsChannelId"
            ChannelSetting.REPORTS -> "reportsChannelId"
        }
        properties.remove("${guildId}.$propertyKey")
        saveProperties(properties)
    }

    @Synchronized
    fun setAutoRole(guildId: ULong, roleId: ULong) {
        val properties = loadProperties()
        properties.setProperty("${guildId}.autoRoleId", roleId.toString())
        saveProperties(properties)
    }

    @Synchronized
    fun clearAutoRole(guildId: ULong) {
        val properties = loadProperties()
        properties.remove("${guildId}.autoRoleId")
        saveProperties(properties)
    }

    @Synchronized
    fun setPrefix(guildId: ULong, prefix: String) {
        val properties = loadProperties()
        properties.setProperty("${guildId}.prefix", prefix.take(5))
        saveProperties(properties)
    }

    @Synchronized
    fun setWelcomeEmbed(guildId: ULong, config: WelcomeEmbedConfig) {
        val properties = loadProperties()
        properties.setProperty("${guildId}.welcomeEmbed.enabled", config.enabled.toString())
        properties.setProperty("${guildId}.welcomeEmbed.title", config.title)
        properties.setProperty("${guildId}.welcomeEmbed.description", config.description)
        properties.setProperty("${guildId}.welcomeEmbed.color", config.color)
        properties.setProperty("${guildId}.welcomeEmbed.footer", config.footer)
        properties.setProperty("${guildId}.welcomeEmbed.thumbnailUrl", config.thumbnailUrl)
        properties.setProperty("${guildId}.welcomeEmbed.imageUrl", config.imageUrl)
        saveProperties(properties)
    }

    @Synchronized
    fun setCommandEnabled(guildId: ULong, commandName: String, enabled: Boolean) {
        val properties = loadProperties()
        val disabledRaw = properties.getProperty("${guildId}.disabledCommands") ?: ""
        val disabled = disabledRaw.split(",").map { it.trim() }.filter { it.isNotBlank() }.toMutableSet()

        if (enabled) {
            disabled.remove(commandName)
        } else {
            disabled.add(commandName)
        }

        properties.setProperty("${guildId}.disabledCommands", disabled.joinToString(","))
        saveProperties(properties)
    }

    fun isCommandEnabled(guildId: ULong, commandName: String): Boolean {
        return commandName !in get(guildId).disabledCommands
    }

    private fun loadProperties(): Properties {
        val properties = Properties()
        readFile(filePath) { input -> properties.load(input) }
        return properties
    }

    private fun saveProperties(properties: Properties) {
        writeFile(filePath) { output ->
            properties.store(output, "Guild settings")
        }
    }

    private fun readFile(path: Path, block: (InputStream) -> Unit) {
        Files.newInputStream(path).use(block)
    }

    private fun writeFile(path: Path, block: (OutputStream) -> Unit) {
        Files.newOutputStream(path).use(block)
    }
}
