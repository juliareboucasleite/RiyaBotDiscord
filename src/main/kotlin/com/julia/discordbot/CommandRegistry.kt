package com.julia.discordbot

import com.julia.discordbot.commands.AutoRoleCommand
import com.julia.discordbot.commands.AvatarCommand
import com.julia.discordbot.commands.BotCommand
import com.julia.discordbot.commands.BotInfoCommand
import com.julia.discordbot.commands.ClearCommand
import com.julia.discordbot.commands.ConfigCommand
import com.julia.discordbot.commands.DenyPasswordCommand
import com.julia.discordbot.commands.GuardarCommand
import com.julia.discordbot.commands.HelpCommand
import com.julia.discordbot.commands.PingCommand
import com.julia.discordbot.commands.ApprovePasswordCommand
import com.julia.discordbot.commands.ReportCommand
import com.julia.discordbot.commands.ServerInfoCommand
import com.julia.discordbot.commands.ShowPasswordCommand
import com.julia.discordbot.commands.SuggestCommand
import com.julia.discordbot.commands.TimeoutCommand
import com.julia.discordbot.commands.UserInfoCommand
import com.julia.discordbot.commands.WarnCommand

class CommandRegistry(val commands: List<BotCommand>) {
    fun find(name: String): BotCommand? {
        return commands.firstOrNull { command ->
            command.name == name || name in command.aliases
        }
    }

    companion object {
        fun createDefault(): CommandRegistry {
            val commands = listOf(
                PingCommand(),
                BotInfoCommand(),
                HelpCommand(),
                GuardarCommand(),
                ShowPasswordCommand(),
                ApprovePasswordCommand(),
                DenyPasswordCommand(),
                ConfigCommand(),
                UserInfoCommand(),
                ServerInfoCommand(),
                AvatarCommand(),
                ClearCommand(),
                TimeoutCommand(),
                WarnCommand(),
                SuggestCommand(),
                ReportCommand(),
                AutoRoleCommand()
            )
            return CommandRegistry(commands)
        }
    }
}
