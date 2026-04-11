package com.julia.discordbot.dashboard

fun baseScript(): String = """
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    toast.innerHTML = '<i class="fas fa-' + (type === 'success' ? 'check-circle' : 'exclamation-circle') + '"></i> ' + message;
    container.appendChild(toast);
    setTimeout(() => toast.classList.add('show'), 10);
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

async function apiPost(url, data) {
    try {
        const res = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!res.ok) {
            const text = await res.text();
            showToast(text || 'Erro ao salvar', 'error');
            return null;
        }
        const result = await res.json();
        showToast(result.message || 'Salvo com sucesso!');
        return result;
    } catch (e) {
        showToast('Erro de conexao', 'error');
        return null;
    }
}
""".trimIndent()

fun guildDetailScript(guildId: String): String = """
document.getElementById('save-prefix-btn')?.addEventListener('click', async () => {
    const prefix = document.getElementById('prefix-input').value;
    await apiPost('/api/guilds/$guildId/prefix', { prefix });
});

document.getElementById('save-autorole-btn')?.addEventListener('click', async () => {
    const roleId = document.getElementById('autorole-input').value.trim();
    await apiPost('/api/guilds/$guildId/autorole', { roleId: roleId || null });
});

document.querySelectorAll('.edit-channel-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const setting = btn.dataset.setting;
        const current = btn.dataset.current;
        const newVal = prompt('Novo ID do canal para "' + setting + '":\n(Deixe vazio para remover)', current);
        if (newVal === null) return;
        apiPost('/api/guilds/$guildId/channels', {
            channelType: setting,
            channelId: newVal.trim() || null
        }).then(r => { if (r) setTimeout(() => location.reload(), 500); });
    });
});
""".trimIndent()

fun commandsPageScript(): String = """
async function toggleCommand(checkbox) {
    const cmd = checkbox.dataset.command;
    const guildId = checkbox.dataset.guildId;
    const enabled = checkbox.checked;
    await apiPost('/api/guilds/' + guildId + '/commands/toggle', {
        commandName: cmd,
        enabled: enabled
    });
}
""".trimIndent()

fun embedsPageScript(): String = """
function updatePreview() {
    const title = document.getElementById('embed-title').value || 'Titulo do Embed';
    const desc = document.getElementById('embed-description').value || 'Descricao do embed...';
    const color = document.getElementById('embed-color').value || '#7c3aed';
    const footer = document.getElementById('embed-footer').value;
    const thumbnail = document.getElementById('embed-thumbnail').value;
    const image = document.getElementById('embed-image').value;

    document.getElementById('preview-title').textContent = title;
    document.getElementById('preview-description').textContent = desc
        .replace('{user}', '@Utilizador')
        .replace('{username}', 'Utilizador')
        .replace('{server}', 'Meu Servidor')
        .replace('{membercount}', '42');
    document.getElementById('preview-color-bar').style.background = color;

    const footerWrapper = document.getElementById('preview-footer-wrapper');
    footerWrapper.innerHTML = footer ? '<span id="preview-footer">' + footer + '</span>' : '';

    const thumbWrapper = document.getElementById('preview-thumbnail-wrapper');
    if (thumbnail) {
        thumbWrapper.innerHTML = '<img src="' + thumbnail + '" class="embed-preview-thumbnail" id="preview-thumbnail" onerror="this.style.display=\'none\'">';
    } else {
        thumbWrapper.innerHTML = '';
    }

    const imgWrapper = document.getElementById('preview-image-wrapper');
    if (image) {
        imgWrapper.innerHTML = '<img src="' + image + '" class="embed-preview-image" id="preview-image" onerror="this.style.display=\'none\'">';
    } else {
        imgWrapper.innerHTML = '';
    }
}

async function loadEmbedChannels() {
    const guildId = document.getElementById('save-embed-btn')?.dataset.guildId;
    const select = document.getElementById('embed-target-channel');
    if (!guildId || !select) return;

    try {
        const res = await fetch('/api/guilds/' + guildId + '/channels');
        if (!res.ok) return;
        const channels = await res.json();
        select.innerHTML = '<option value="">Selecione um canal</option>';
        channels.forEach(channel => {
            const option = document.createElement('option');
            option.value = channel.id;
            option.textContent = '#' + channel.name;
            select.appendChild(option);
        });
    } catch (e) {
        showToast('Nao foi possivel carregar os canais', 'error');
    }
}

function currentEmbedPayload() {
    return {
        title: document.getElementById('embed-title').value,
        description: document.getElementById('embed-description').value,
        color: document.getElementById('embed-color').value,
        footer: document.getElementById('embed-footer').value,
        thumbnailUrl: document.getElementById('embed-thumbnail').value,
        imageUrl: document.getElementById('embed-image').value
    };
}

document.getElementById('save-embed-btn')?.addEventListener('click', async () => {
    const guildId = document.getElementById('save-embed-btn').dataset.guildId;
    const data = {
        enabled: document.getElementById('embed-enabled').checked,
        ...currentEmbedPayload()
    };
    await apiPost('/api/guilds/' + guildId + '/welcome-embed', data);
});

document.getElementById('send-embed-btn')?.addEventListener('click', async () => {
    const guildId = document.getElementById('save-embed-btn').dataset.guildId;
    const channelId = document.getElementById('embed-target-channel').value;
    if (!channelId) {
        showToast('Selecione um canal para enviar o embed', 'error');
        return;
    }

    await apiPost('/api/guilds/' + guildId + '/embeds/send', {
        channelId,
        ...currentEmbedPayload()
    });
});

loadEmbedChannels();
""".trimIndent()
