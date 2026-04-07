import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.common.entity.PresenceStatus
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.util.Properties

@OptIn(PrivilegedIntent::class)
fun main() = runBlocking {
    val kord = createKord(loadToken())
    registerHandlers(kord)
    login(kord)
}

private suspend fun createKord(token: String): Kord = Kord(token)

private fun registerHandlers(kord: Kord) {
    kord.on<ReadyEvent> {
        println("Logado como ${self.tag}")
        kord.editPresence {
            status = PresenceStatus.Online
            streaming("Assistindo minha criadora", "https://www.twitch.tv/leeksxyy")
        }
    }

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        if (message.content.trim() == "!ping") {
            message.channel.createMessage("pong")
        }
    }
}

@OptIn(PrivilegedIntent::class)
private suspend fun login(kord: Kord) {
    kord.login {
        intents = Intents(
            Intent.Guilds,
            Intent.GuildMessages,
            Intent.MessageContent
        )
    }
}

private fun loadToken(): String {
    val props = Properties()
    val path = "secrets.properties"
    val file = File(path)
    if (!file.exists()) {
        error("Arquivo $path nao encontrado. Diretorio atual: ${File(".").absolutePath}")
    }
    FileInputStream(file).use { props.load(it) }

    return props.getProperty("DISCORD_TOKEN")
        ?: error("DISCORD_TOKEN nao encontrado em $path")
}
