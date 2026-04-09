package com.julia.discordbot

import java.nio.file.Path

data class BotServices(
    val settingsStore: GuildSettingsStore,
    val warningStore: WarningStore
) {
    companion object {
        fun create(dataDirectory: Path = Path.of("data")): BotServices {
            return BotServices(
                settingsStore = GuildSettingsStore(dataDirectory),
                warningStore = WarningStore(dataDirectory)
            )
        }
    }
}
