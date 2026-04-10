package com.julia.discordbot

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Instant

data class PasswordEntry(
    val ownerId: ULong,
    val note: String,
    val name: String,
    val password: String
)

data class PasswordShareRequest(
    val ownerId: ULong,
    val requesterId: ULong,
    val note: String,
    val createdAt: Instant
)

class PasswordStore(dataDirectory: Path) {
    private val passwordsFilePath = dataDirectory.resolve("passwords.tsv")
    private val requestsFilePath = dataDirectory.resolve("password-share-requests.tsv")

    init {
        Files.createDirectories(dataDirectory)
        ensureFileExists(passwordsFilePath)
        ensureFileExists(requestsFilePath)
    }

    @Synchronized
    fun save(ownerId: ULong, note: String, name: String, password: String) {
        val entries = loadEntries().toMutableList()
        entries += PasswordEntry(
            ownerId = ownerId,
            note = normalizeValue(note),
            name = sanitizeValue(name),
            password = sanitizeValue(password)
        )
        writeEntries(entries)
    }

    @Synchronized
    fun findByOwnerAndNote(ownerId: ULong, note: String): List<PasswordEntry> {
        val normalizedNote = normalizeValue(note)
        return loadEntries().filter { it.ownerId == ownerId && it.note.equals(normalizedNote, ignoreCase = true) }
    }

    @Synchronized
    fun findByNote(note: String): List<PasswordEntry> {
        val normalizedNote = normalizeValue(note)
        return loadEntries().filter { it.note.equals(normalizedNote, ignoreCase = true) }
    }

    @Synchronized
    fun createShareRequest(ownerId: ULong, requesterId: ULong, note: String): PasswordShareRequest {
        val normalizedNote = normalizeValue(note)
        val requests = loadRequests()
            .filterNot { it.ownerId == ownerId && it.requesterId == requesterId && it.note.equals(normalizedNote, ignoreCase = true) }
            .toMutableList()

        val request = PasswordShareRequest(
            ownerId = ownerId,
            requesterId = requesterId,
            note = normalizedNote,
            createdAt = Instant.now()
        )
        requests += request
        writeRequests(requests)
        return request
    }

    @Synchronized
    fun takeShareRequest(ownerId: ULong, requesterId: ULong, note: String): PasswordShareRequest? {
        val normalizedNote = normalizeValue(note)
        val requests = loadRequests().toMutableList()
        val index = requests.indexOfFirst {
            it.ownerId == ownerId && it.requesterId == requesterId && it.note.equals(normalizedNote, ignoreCase = true)
        }
        if (index < 0) return null

        val request = requests.removeAt(index)
        writeRequests(requests)
        return request
    }

    @Synchronized
    fun removeShareRequest(ownerId: ULong, requesterId: ULong, note: String): Boolean {
        val normalizedNote = normalizeValue(note)
        val requests = loadRequests().toMutableList()
        val removed = requests.removeIf {
            it.ownerId == ownerId && it.requesterId == requesterId && it.note.equals(normalizedNote, ignoreCase = true)
        }
        if (removed) {
            writeRequests(requests)
        }
        return removed
    }

    private fun loadEntries(): List<PasswordEntry> {
        return Files.readAllLines(passwordsFilePath, Charsets.UTF_8)
            .asSequence()
            .filter { it.isNotBlank() }
            .mapNotNull(::parseEntry)
            .toList()
    }

    private fun writeEntries(entries: List<PasswordEntry>) {
        val contents = entries.joinToString(System.lineSeparator()) { entry ->
            listOf(
                entry.ownerId.toString(),
                sanitizeValue(entry.note),
                sanitizeValue(entry.name),
                sanitizeValue(entry.password)
            ).joinToString("\t")
        }
        val fileContents = if (contents.isBlank()) "" else "$contents${System.lineSeparator()}"
        Files.writeString(passwordsFilePath, fileContents, Charsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)
    }

    private fun loadRequests(): List<PasswordShareRequest> {
        return Files.readAllLines(requestsFilePath, Charsets.UTF_8)
            .asSequence()
            .filter { it.isNotBlank() }
            .mapNotNull(::parseRequest)
            .toList()
    }

    private fun writeRequests(requests: List<PasswordShareRequest>) {
        val contents = requests.joinToString(System.lineSeparator()) { request ->
            listOf(
                request.ownerId.toString(),
                request.requesterId.toString(),
                sanitizeValue(request.note),
                request.createdAt.toString()
            ).joinToString("\t")
        }
        val fileContents = if (contents.isBlank()) "" else "$contents${System.lineSeparator()}"
        Files.writeString(requestsFilePath, fileContents, Charsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)
    }

    private fun parseEntry(line: String): PasswordEntry? {
        val parts = line.split("\t", limit = 4)
        if (parts.size < 4) return null

        return PasswordEntry(
            ownerId = parts[0].toULongOrNull() ?: return null,
            note = parts[1],
            name = parts[2],
            password = parts[3]
        )
    }

    private fun parseRequest(line: String): PasswordShareRequest? {
        val parts = line.split("\t", limit = 4)
        if (parts.size < 4) return null

        return PasswordShareRequest(
            ownerId = parts[0].toULongOrNull() ?: return null,
            requesterId = parts[1].toULongOrNull() ?: return null,
            note = parts[2],
            createdAt = runCatching { Instant.parse(parts[3]) }.getOrNull() ?: return null
        )
    }

    private fun normalizeValue(value: String): String {
        return sanitizeValue(value)
    }

    private fun sanitizeValue(value: String): String {
        return value.replace("\t", " ").replace("\r", " ").replace("\n", " ").trim()
    }

    private fun ensureFileExists(path: Path) {
        if (!Files.exists(path)) {
            Files.createFile(path)
        }
    }
}
