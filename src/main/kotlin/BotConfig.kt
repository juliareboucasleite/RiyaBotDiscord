package com.julia.discordbot

import java.io.File
import java.io.FileInputStream
import java.util.Properties

private const val SECRETS_FILE = "secrets.properties"

fun loadToken(): String {
    val props = Properties()
    val file = File(SECRETS_FILE)

    if (!file.exists()) {
        error("Arquivo $SECRETS_FILE nao encontrado. Diretorio atual: ${File(".").absolutePath}")
    }

    FileInputStream(file).use { props.load(it) }
    return props.getProperty("DISCORD_TOKEN")
        ?: error("DISCORD_TOKEN nao encontrado em $SECRETS_FILE")
}
