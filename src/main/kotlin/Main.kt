import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on

suspend fun main() {
    val kord = Kord("MTQ4NTk2MjA1NzQxMjI1MTY4OA.GrjKQK.PeQ-KCs6uDxwT5Z_BEOSqd6U-NbEowwdOXNM9w")

    kord.on<MessageCreateEvent> {
        if (message.content == "!ping") {
            message.channel.createMessage("Pong!")
        }
    }

    kord.login()
}