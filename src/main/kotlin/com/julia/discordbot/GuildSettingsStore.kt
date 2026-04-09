package com.julia.discordbot

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

data class GuildSettings(
    val logsChannelId: ULong? = null,
    val welcomeChannelId: ULong? = null,
    val suggestionsChannelId: ULong? = null,
    val reportsChannelId: ULong? = null,
    val autoRoleId: ULong? = null
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

        return GuildSettings(
            logsChannelId = readULong("logsChannelId"),
            welcomeChannelId = readULong("welcomeChannelId"),
            suggestionsChannelId = readULong("suggestionsChannelId"),
            reportsChannelId = readULong("reportsChannelId"),
            autoRoleId = readULong("autoRoleId")
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
