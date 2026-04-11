package com.julia.discordbot.dashboard

import com.julia.discordbot.BotServices
import dev.kord.core.Kord
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import kotlinx.html.*

fun Route.dashboardPages(kord: Kord, services: BotServices) {

    // ===== Home Page =====
    get("/") {
        val self = kord.getSelf()
        val guilds = kord.guilds.toList()
        val totalMembers = guilds.sumOf { it.memberCount ?: 0 }
        val avatarUrl = self.avatar?.cdnUrl?.toUrl()

        call.respondHtml {
            dashboardLayout("Dashboard", "dashboard", self.username, avatarUrl) {
                header("content-header") {
                    h1 { +"Dashboard" }
                    p("subtitle") { +"Visao geral do RiyaBot" }
                }

                // Stats cards
                div("stats-grid") {
                    statsCard("fas fa-server", "Servidores", guilds.size.toString(), "card-purple")
                    statsCard("fas fa-users", "Membros", totalMembers.toString(), "card-blue")
                    statsCard("fas fa-terminal", "Comandos", "17", "card-pink")
                    statsCard("fas fa-clock", "Uptime", getUptime(), "card-green")
                }

                // Guilds section
                section("section") {
                    div("section-header") {
                        h2 { +"Servidores" }
                        a(href = "/guilds", classes = "view-all") { +"Ver todos →" }
                    }
                    div("guilds-grid") {
                        for (guild in guilds.take(6)) {
                            val iconUrl = guild.icon?.cdnUrl?.toUrl()
                            guildCard(guild.name, guild.memberCount ?: 0, iconUrl, guild.id.value.toString())
                        }
                    }
                }
            }
        }
    }

    // ===== Guild List Page =====
    get("/guilds") {
        val self = kord.getSelf()
        val guilds = kord.guilds.toList()
        val avatarUrl = self.avatar?.cdnUrl?.toUrl()

        call.respondHtml {
            dashboardLayout("Servidores", "guilds", self.username, avatarUrl) {
                header("content-header") {
                    h1 { +"Servidores" }
                    p("subtitle") { +"Todos os servidores conectados (${guilds.size})" }
                }

                div("guilds-grid full-grid") {
                    for (guild in guilds) {
                        val iconUrl = guild.icon?.cdnUrl?.toUrl()
                        guildCard(guild.name, guild.memberCount ?: 0, iconUrl, guild.id.value.toString())
                    }
                }
            }
        }
    }

    // ===== Guild Detail Page =====
    get("/guilds/{guildId}") {
        val guildIdStr = call.parameters["guildId"]
            ?: return@get call.respondHtml { body { p { +"Guild ID necessario" } } }
        val guildId = guildIdStr.toULongOrNull()
            ?: return@get call.respondHtml { body { p { +"Guild ID invalido" } } }

        val self = kord.getSelf()
        val guild = kord.guilds.toList().firstOrNull { it.id.value == guildId }
            ?: return@get call.respondHtml { body { p { +"Servidor nao encontrado" } } }

        val settings = services.settingsStore.get(guildId)
        val avatarUrl = self.avatar?.cdnUrl?.toUrl()
        val guildIconUrl = guild.icon?.cdnUrl?.toUrl()

        call.respondHtml {
            dashboardLayout("${guild.name}", "guilds", self.username, avatarUrl) {
                header("content-header") {
                    div("breadcrumb") {
                        a(href = "/guilds") { +"Servidores" }
                        span { +" / " }
                        span("current") { +guild.name }
                    }
                    div("guild-header-info") {
                        if (guildIconUrl != null) {
                            img(src = guildIconUrl, alt = guild.name, classes = "guild-header-icon")
                        } else {
                            div("guild-icon-placeholder large") {
                                +guild.name.take(2).uppercase()
                            }
                        }
                        div {
                            h1 { +guild.name }
                            p("subtitle") {
                                i("fas fa-users") {}
                                +" ${guild.memberCount ?: 0} membros"
                            }
                        }
                    }
                }

                // Navigation tabs
                div("tab-nav") {
                    a(href = "/guilds/$guildIdStr", classes = "tab active") {
                        i("fas fa-cog") {}
                        +" Geral"
                    }
                    a(href = "/guilds/$guildIdStr/commands", classes = "tab") {
                        i("fas fa-terminal") {}
                        +" Comandos"
                    }
                    a(href = "/guilds/$guildIdStr/embeds", classes = "tab") {
                        i("fas fa-palette") {}
                        +" Embeds"
                    }
                    a(href = "/guilds/$guildIdStr/moderation", classes = "tab") {
                        i("fas fa-shield-alt") {}
                        +" Moderacao"
                    }
                }

                // Prefix section
                section("section") {
                    div("section-header") {
                        h2 {
                            i("fas fa-hashtag") {}
                            +" Prefixo"
                        }
                    }
                    div("form-card") {
                        div("form-group") {
                            label { +"Prefixo do Bot" }
                            div("input-row") {
                                input(InputType.text, classes = "input") {
                                    id = "prefix-input"
                                    value = settings.prefix
                                    maxLength = "5"
                                    placeholder = "!"
                                }
                                button(classes = "btn btn-primary") {
                                    id = "save-prefix-btn"
                                    attributes["data-guild-id"] = guildIdStr
                                    i("fas fa-save") {}
                                    +" Salvar"
                                }
                            }
                            span("form-hint") { +"O prefixo pode ter ate 5 caracteres. Exemplo: !, ?, r!" }
                        }
                    }
                }

                // Channel Settings
                section("section") {
                    div("section-header") {
                        h2 {
                            i("fas fa-hashtag") {}
                            +" Canais Configurados"
                        }
                    }
                    div("settings-grid") {
                        settingCard("fas fa-scroll", "Canal de Logs", settings.logsChannelId?.toString(), "logs", guildIdStr)
                        settingCard("fas fa-door-open", "Canal de Boas-vindas", settings.welcomeChannelId?.toString(), "welcome", guildIdStr)
                        settingCard("fas fa-lightbulb", "Canal de Sugestoes", settings.suggestionsChannelId?.toString(), "suggestions", guildIdStr)
                        settingCard("fas fa-flag", "Canal de Reports", settings.reportsChannelId?.toString(), "reports", guildIdStr)
                    }
                }

                // Auto Role
                section("section") {
                    div("section-header") {
                        h2 {
                            i("fas fa-user-tag") {}
                            +" Auto Role"
                        }
                    }
                    div("form-card") {
                        div("form-group") {
                            label { +"Cargo automatico para novos membros" }
                            div("input-row") {
                                input(InputType.text, classes = "input") {
                                    id = "autorole-input"
                                    value = settings.autoRoleId?.toString() ?: ""
                                    placeholder = "ID do cargo ou deixe vazio para desativar"
                                }
                                button(classes = "btn btn-primary") {
                                    id = "save-autorole-btn"
                                    attributes["data-guild-id"] = guildIdStr
                                    i("fas fa-save") {}
                                    +" Salvar"
                                }
                            }
                            span("form-hint") { +"Deixe vazio e salve para remover o auto role" }
                        }
                    }
                }

                // JavaScript for interactive forms
                script { unsafe { raw(guildDetailScript(guildIdStr)) } }
            }
        }
    }

    // ===== Commands Page =====
    get("/guilds/{guildId}/commands") {
        val guildIdStr = call.parameters["guildId"]
            ?: return@get call.respondHtml { body { p { +"Guild ID necessario" } } }
        val guildId = guildIdStr.toULongOrNull()
            ?: return@get call.respondHtml { body { p { +"Guild ID invalido" } } }

        val self = kord.getSelf()
        val guild = kord.guilds.toList().firstOrNull { it.id.value == guildId }
            ?: return@get call.respondHtml { body { p { +"Servidor nao encontrado" } } }
        val settings = services.settingsStore.get(guildId)
        val avatarUrl = self.avatar?.cdnUrl?.toUrl()
        val commands = allCommandsInfo().map { it.copy(enabled = it.name !in settings.disabledCommands) }
        val categories = commands.groupBy { it.category }

        call.respondHtml {
            dashboardLayout("Comandos - ${guild.name}", "guilds", self.username, avatarUrl) {
                header("content-header") {
                    div("breadcrumb") {
                        a(href = "/guilds") { +"Servidores" }
                        span { +" / " }
                        a(href = "/guilds/$guildIdStr") { +guild.name }
                        span { +" / " }
                        span("current") { +"Comandos" }
                    }
                    h1 { +"Gerenciar Comandos" }
                    p("subtitle") { +"Ative ou desative comandos para este servidor" }
                }

                // Navigation tabs
                div("tab-nav") {
                    a(href = "/guilds/$guildIdStr", classes = "tab") {
                        i("fas fa-cog") {}
                        +" Geral"
                    }
                    a(href = "/guilds/$guildIdStr/commands", classes = "tab active") {
                        i("fas fa-terminal") {}
                        +" Comandos"
                    }
                    a(href = "/guilds/$guildIdStr/embeds", classes = "tab") {
                        i("fas fa-palette") {}
                        +" Embeds"
                    }
                    a(href = "/guilds/$guildIdStr/moderation", classes = "tab") {
                        i("fas fa-shield-alt") {}
                        +" Moderacao"
                    }
                }

                for ((category, cmds) in categories) {
                    section("section") {
                        div("section-header") {
                            h2 {
                                val icon = when (category) {
                                    "Geral" -> "fas fa-home"
                                    "Moderacao" -> "fas fa-shield-alt"
                                    "Administracao" -> "fas fa-crown"
                                    "Informacao" -> "fas fa-info-circle"
                                    "Utilidades" -> "fas fa-toolbox"
                                    "Comunidade" -> "fas fa-heart"
                                    else -> "fas fa-terminal"
                                }
                                i(icon) {}
                                +" $category"
                            }
                        }
                        div("commands-list") {
                            for (cmd in cmds) {
                                div("command-card") {
                                    div("command-info") {
                                        div("command-header-row") {
                                            span("command-name") { +"${settings.prefix}${cmd.name}" }
                                            if (cmd.aliases.isNotEmpty()) {
                                                span("command-aliases") {
                                                    +cmd.aliases.joinToString(", ") { "${settings.prefix}$it" }
                                                }
                                            }
                                        }
                                        p("command-desc") { +cmd.description }
                                        code("command-usage") { +"${settings.prefix}${cmd.usage}" }
                                    }
                                    div("command-toggle") {
                                        label("switch") {
                                            input(InputType.checkBox) {
                                                checked = cmd.enabled
                                                attributes["data-command"] = cmd.name
                                                attributes["data-guild-id"] = guildIdStr
                                                attributes["onchange"] = "toggleCommand(this)"
                                            }
                                            span("slider") {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                script { unsafe { raw(commandsPageScript()) } }
            }
        }
    }

    // ===== Embeds Page =====
    get("/guilds/{guildId}/embeds") {
        val guildIdStr = call.parameters["guildId"]
            ?: return@get call.respondHtml { body { p { +"Guild ID necessario" } } }
        val guildId = guildIdStr.toULongOrNull()
            ?: return@get call.respondHtml { body { p { +"Guild ID invalido" } } }

        val self = kord.getSelf()
        val guild = kord.guilds.toList().firstOrNull { it.id.value == guildId }
            ?: return@get call.respondHtml { body { p { +"Servidor nao encontrado" } } }
        val settings = services.settingsStore.get(guildId)
        val avatarUrl = self.avatar?.cdnUrl?.toUrl()
        val embed = settings.welcomeEmbed

        call.respondHtml {
            dashboardLayout("Embeds - ${guild.name}", "guilds", self.username, avatarUrl) {
                header("content-header") {
                    div("breadcrumb") {
                        a(href = "/guilds") { +"Servidores" }
                        span { +" / " }
                        a(href = "/guilds/$guildIdStr") { +guild.name }
                        span { +" / " }
                        span("current") { +"Embeds" }
                    }
                    h1 { +"Configurar Embeds" }
                    p("subtitle") { +"Personalize as mensagens embed do bot" }
                }

                // Navigation tabs
                div("tab-nav") {
                    a(href = "/guilds/$guildIdStr", classes = "tab") {
                        i("fas fa-cog") {}
                        +" Geral"
                    }
                    a(href = "/guilds/$guildIdStr/commands", classes = "tab") {
                        i("fas fa-terminal") {}
                        +" Comandos"
                    }
                    a(href = "/guilds/$guildIdStr/embeds", classes = "tab active") {
                        i("fas fa-palette") {}
                        +" Embeds"
                    }
                    a(href = "/guilds/$guildIdStr/moderation", classes = "tab") {
                        i("fas fa-shield-alt") {}
                        +" Moderacao"
                    }
                }

                div("embed-editor-layout") {
                    // Editor form
                    div("embed-form") {
                        section("section") {
                            h2 {
                                i("fas fa-door-open") {}
                                +" Editor de Embed"
                            }

                            div("form-card") {
                                div("form-group") {
                                    div("toggle-row") {
                                        label { +"Ativar Welcome Embed" }
                                        label("switch") {
                                            input(InputType.checkBox) {
                                                id = "embed-enabled"
                                                checked = embed.enabled
                                            }
                                            span("slider") {}
                                        }
                                    }
                                }

                                div("form-group") {
                                    label { +"Canal para envio manual" }
                                    select(classes = "input") {
                                        id = "embed-target-channel"
                                    }
                                    span("form-hint") { +"Escolha qualquer canal do servidor para enviar o embed agora. Salvar continua atualizando o embed de welcome." }
                                }

                                div("form-group") {
                                    label { +"Titulo" }
                                    input(InputType.text, classes = "input") {
                                        id = "embed-title"
                                        value = embed.title
                                        placeholder = "Bem-vindo(a)!"
                                        attributes["oninput"] = "updatePreview()"
                                    }
                                }

                                div("form-group") {
                                    label { +"Descricao" }
                                    textArea(classes = "input textarea") {
                                        id = "embed-description"
                                        placeholder = "Bem-vindo(a) ao servidor, {user}!"
                                        attributes["oninput"] = "updatePreview()"
                                        +embed.description
                                    }
                                    span("form-hint") { +"Variaveis: {user} = mencao, {username} = nome, {server} = nome do servidor, {membercount} = n. membros" }
                                }

                                div("form-group") {
                                    label { +"Cor (hex)" }
                                    div("color-row") {
                                        input(InputType.color, classes = "color-picker") {
                                            id = "embed-color-picker"
                                            value = embed.color
                                            attributes["oninput"] = "document.getElementById('embed-color').value = this.value; updatePreview()"
                                        }
                                        input(InputType.text, classes = "input") {
                                            id = "embed-color"
                                            value = embed.color
                                            placeholder = "#7c3aed"
                                            attributes["oninput"] = "document.getElementById('embed-color-picker').value = this.value; updatePreview()"
                                        }
                                    }
                                }

                                div("form-group") {
                                    label { +"Footer" }
                                    input(InputType.text, classes = "input") {
                                        id = "embed-footer"
                                        value = embed.footer
                                        placeholder = "Divirta-se no servidor!"
                                        attributes["oninput"] = "updatePreview()"
                                    }
                                }

                                div("form-group") {
                                    label { +"URL da Thumbnail" }
                                    input(InputType.text, classes = "input") {
                                        id = "embed-thumbnail"
                                        value = embed.thumbnailUrl
                                        placeholder = "https://exemplo.com/imagem.png"
                                        attributes["oninput"] = "updatePreview()"
                                    }
                                }

                                div("form-group") {
                                    label { +"URL da Imagem" }
                                    input(InputType.text, classes = "input") {
                                        id = "embed-image"
                                        value = embed.imageUrl
                                        placeholder = "https://exemplo.com/banner.png"
                                        attributes["oninput"] = "updatePreview()"
                                    }
                                }

                                div("input-row") {
                                    button(classes = "btn btn-primary") {
                                        id = "save-embed-btn"
                                        attributes["data-guild-id"] = guildIdStr
                                        i("fas fa-save") {}
                                        +" Salvar Welcome"
                                    }
                                    button(classes = "btn btn-secondary") {
                                        id = "send-embed-btn"
                                        attributes["data-guild-id"] = guildIdStr
                                        i("fas fa-paper-plane") {}
                                        +" Enviar Agora"
                                    }
                                }
                            }
                        }
                    }

                    // Live preview
                    div("embed-preview-container") {
                        h3("preview-title") {
                            i("fas fa-eye") {}
                            +" Preview"
                        }
                        div("discord-embed-preview") {
                            id = "embed-preview"
                            div("embed-color-bar") {
                                id = "preview-color-bar"
                                style = "background: ${embed.color}"
                            }
                            div("embed-content") {
                                div("embed-body") {
                                    h4("embed-preview-title") {
                                        id = "preview-title"
                                        +(embed.title.ifBlank { "Titulo do Embed" })
                                    }
                                    p("embed-preview-desc") {
                                        id = "preview-description"
                                        +(embed.description.ifBlank { "Descricao do embed..." })
                                    }
                                    div("embed-preview-image-wrapper") {
                                        id = "preview-image-wrapper"
                                        if (embed.imageUrl.isNotBlank()) {
                                            img(src = embed.imageUrl, classes = "embed-preview-image") {
                                                id = "preview-image"
                                            }
                                        }
                                    }
                                    div("embed-preview-footer") {
                                        id = "preview-footer-wrapper"
                                        if (embed.footer.isNotBlank()) {
                                            span {
                                                id = "preview-footer"
                                                +embed.footer
                                            }
                                        }
                                    }
                                }
                                div("embed-thumbnail-wrapper") {
                                    id = "preview-thumbnail-wrapper"
                                    if (embed.thumbnailUrl.isNotBlank()) {
                                        img(src = embed.thumbnailUrl, classes = "embed-preview-thumbnail") {
                                            id = "preview-thumbnail"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                script { unsafe { raw(embedsPageScript()) } }
            }
        }
    }

    // ===== Moderation Page =====
    get("/guilds/{guildId}/moderation") {
        val guildIdStr = call.parameters["guildId"]
            ?: return@get call.respondHtml { body { p { +"Guild ID necessario" } } }
        val guildId = guildIdStr.toULongOrNull()
            ?: return@get call.respondHtml { body { p { +"Guild ID invalido" } } }

        val self = kord.getSelf()
        val guild = kord.guilds.toList().firstOrNull { it.id.value == guildId }
            ?: return@get call.respondHtml { body { p { +"Servidor nao encontrado" } } }
        val avatarUrl = self.avatar?.cdnUrl?.toUrl()

        call.respondHtml {
            dashboardLayout("Moderacao - ${guild.name}", "guilds", self.username, avatarUrl) {
                header("content-header") {
                    div("breadcrumb") {
                        a(href = "/guilds") { +"Servidores" }
                        span { +" / " }
                        a(href = "/guilds/$guildIdStr") { +guild.name }
                        span { +" / " }
                        span("current") { +"Moderacao" }
                    }
                    h1 { +"Moderacao" }
                    p("subtitle") { +"Ferramentas de moderacao do servidor" }
                }

                // Navigation tabs
                div("tab-nav") {
                    a(href = "/guilds/$guildIdStr", classes = "tab") {
                        i("fas fa-cog") {}
                        +" Geral"
                    }
                    a(href = "/guilds/$guildIdStr/commands", classes = "tab") {
                        i("fas fa-terminal") {}
                        +" Comandos"
                    }
                    a(href = "/guilds/$guildIdStr/embeds", classes = "tab") {
                        i("fas fa-palette") {}
                        +" Embeds"
                    }
                    a(href = "/guilds/$guildIdStr/moderation", classes = "tab active") {
                        i("fas fa-shield-alt") {}
                        +" Moderacao"
                    }
                }

                // Moderation features info
                section("section") {
                    h2 {
                        i("fas fa-gavel") {}
                        +" Comandos de Moderacao"
                    }

                    div("mod-features-grid") {
                        modFeatureCard("fas fa-broom", "Clear / Purge",
                            "Apaga ate 100 mensagens de um canal. Uso: !clear <1-100>",
                            "Disponivel")
                        modFeatureCard("fas fa-clock", "Timeout / Mute",
                            "Aplica timeout temporario em membros. Uso: !timeout @user <min>",
                            "Disponivel")
                        modFeatureCard("fas fa-exclamation-triangle", "Warn",
                            "Registra avisos para membros. Os avisos sao permanentes. Uso: !warn @user <motivo>",
                            "Disponivel")
                        modFeatureCard("fas fa-flag", "Report",
                            "Membros podem reportar outros. Reports vao para o canal configurado.",
                            "Disponivel")
                    }
                }

                section("section") {
                    h2 {
                        i("fas fa-history") {}
                        +" Log de Acoes"
                    }
                    div("empty-state") {
                        i("fas fa-inbox") {}
                        p { +"O log de acoes de moderacao sera exibido aqui quando houver dados." }
                    }
                }
            }
        }
    }
}

// ===== Reusable Components =====

private fun FlowContent.statsCard(icon: String, label: String, value: String, colorClass: String) {
    div("stat-card $colorClass") {
        div("stat-icon") {
            i(icon) {}
        }
        div("stat-info") {
            span("stat-value") {
                if (label == "Uptime") attributes["data-stat"] = "uptime"
                +value
            }
            span("stat-label") { +label }
        }
    }
}

private fun FlowContent.guildCard(name: String, memberCount: Int, iconUrl: String?, guildId: String) {
    div("guild-card") {
        div("guild-card-header") {
            if (iconUrl != null) {
                img(src = iconUrl, alt = name, classes = "guild-icon")
            } else {
                div("guild-icon-placeholder") {
                    +name.take(2).uppercase()
                }
            }
            div("guild-meta") {
                h3 { +name }
                p {
                    i("fas fa-users") {}
                    +" $memberCount membros"
                }
            }
        }
        a(href = "/guilds/$guildId", classes = "guild-link") {
            +"Gerenciar"
            i("fas fa-arrow-right") {}
        }
    }
}

private fun FlowContent.settingCard(icon: String, label: String, value: String?, settingKey: String, guildId: String) {
    div("setting-card") {
        div("setting-icon") {
            i(icon) {}
        }
        div("setting-info") {
            span("setting-label") { +label }
            span("setting-value") {
                if (value != null) {
                    +value
                } else {
                    span("not-configured") { +"Nao configurado" }
                }
            }
        }
        div("setting-actions") {
            button(classes = "btn btn-sm btn-secondary edit-channel-btn") {
                attributes["data-setting"] = settingKey
                attributes["data-guild-id"] = guildId
                attributes["data-current"] = value ?: ""
                i("fas fa-edit") {}
            }
        }
    }
}

private fun FlowContent.modFeatureCard(icon: String, title: String, description: String, status: String) {
    div("mod-feature-card") {
        div("mod-feature-icon") {
            i(icon) {}
        }
        div("mod-feature-info") {
            h3 { +title }
            p { +description }
        }
        span("mod-feature-status") {
            i("fas fa-check-circle") {}
            +" $status"
        }
    }
}

// ===== Layout Template =====

private fun HTML.dashboardLayout(
    pageTitle: String,
    activePage: String,
    botName: String,
    botAvatarUrl: String?,
    content: MAIN.() -> Unit
) {
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title("$pageTitle - RiyaBot Dashboard")
        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") { attributes["crossorigin"] = "" }
        link(
            href = "https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600&display=swap",
            rel = "stylesheet"
        )
        link(
            href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css",
            rel = "stylesheet"
        )
        style { unsafe { raw(dashboardCSS()) } }
    }
    body {
        div("app") {
            // Sidebar
            nav("sidebar") {
                div("sidebar-header") {
                    if (botAvatarUrl != null) {
                        img(src = botAvatarUrl, alt = "Bot Avatar", classes = "bot-avatar")
                    }
                    div("bot-info") {
                        h2 { +botName }
                        span("status-badge") {
                            i("fas fa-circle") {}
                            +" Online"
                        }
                    }
                }
                ul("nav-links") {
                    navItem("fas fa-home", "Dashboard", "/", activePage == "dashboard")
                    navItem("fas fa-server", "Servidores", "/guilds", activePage == "guilds")
                    navItem("fas fa-terminal", "Comandos", "#", activePage == "commands")
                    navItem("fas fa-cog", "Configuracoes", "#", activePage == "settings")
                }
            }

            // Main content
            main("content") {
                content()
            }
        }

        // Toast notification container
        div("toast-container") {
            id = "toast-container"
        }

        // Base scripts
        script { unsafe { raw(baseScript()) } }
    }
}

private fun UL.navItem(icon: String, label: String, href: String, active: Boolean) {
    li(if (active) "nav-item active" else "nav-item") {
        a(href = href) {
            i(icon) {}
            span { +label }
        }
    }
}
