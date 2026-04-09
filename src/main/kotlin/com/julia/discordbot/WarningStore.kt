package com.julia.discordbot

import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant

data class WarningEntry(
    val guildId: ULong,
    val userId: ULong,
    val moderatorId: ULong,
    val reason: String,
    val createdAt: Instant
)

class WarningStore(dataDirectory: Path) {
    private val filePath = dataDirectory.resolve("warnings.tsv")

    init {
        Files.createDirectories(dataDirectory)
        if (!Files.exists(filePath)) {
            Files.createFile(filePath)
        }
    }

    @Synchronized
    fun add(entry: WarningEntry) {
        val line = listOf(
            entry.guildId.toString(),
            entry.userId.toString(),
            entry.moderatorId.toString(),
            entry.createdAt.toString(),
            sanitize(entry.reason)
        ).joinToString("\t")

        Files.writeString(filePath, "$line${System.lineSeparator()}", Charsets.UTF_8, java.nio.file.StandardOpenOption.APPEND)
    }

    @Synchronized
    fun countWarnings(guildId: ULong, userId: ULong): Int {
        return Files.readAllLines(filePath, Charsets.UTF_8)
            .asSequence()
            .filter { it.isNotBlank() }
            .mapNotNull { parse(it) }
            .count { it.guildId == guildId && it.userId == userId }
    }

    private fun parse(line: String): WarningEntry? {
        val parts = line.split("\t", limit = 5)
        if (parts.size < 5) return null

        return WarningEntry(
            guildId = parts[0].toULongOrNull() ?: return null,
            userId = parts[1].toULongOrNull() ?: return null,
            moderatorId = parts[2].toULongOrNull() ?: return null,
            createdAt = runCatching { Instant.parse(parts[3]) }.getOrNull() ?: return null,
            reason = parts[4]
        )
    }

    private fun sanitize(reason: String): String {
        return reason.replace("\t", " ").replace("\r", " ").replace("\n", " ").trim()
    }
}
