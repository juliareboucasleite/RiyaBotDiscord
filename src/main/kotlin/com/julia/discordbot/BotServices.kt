package com.julia.discordbot

import java.nio.file.Path

data class BotServices(
    val settingsStore: GuildSettingsStore,
    val warningStore: WarningStore,
    val passwordStore: PasswordStore
) {
    companion object {
        fun create(dataDirectory: Path = Path.of("data")): BotServices {
            return BotServices(
                settingsStore = GuildSettingsStore(dataDirectory),
                warningStore = WarningStore(dataDirectory),
                passwordStore = PasswordStore(dataDirectory)
            )
        }
    }
}
