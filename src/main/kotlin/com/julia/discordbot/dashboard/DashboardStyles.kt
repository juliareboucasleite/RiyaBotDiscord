package com.julia.discordbot.dashboard

fun dashboardCSS(): String = """
/* ===== Reset & Base ===== */
*, *::before, *::after {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

:root {
    --bg-primary: #0a0a0f;
    --bg-secondary: #12121a;
    --bg-card: #1a1a2e;
    --bg-card-hover: #1f1f36;
    --sidebar-bg: #0d0d14;
    --text-primary: #e8e8f0;
    --text-secondary: #8888a0;
    --text-muted: #555570;
    --accent-purple: #7c3aed;
    --accent-blue: #3b82f6;
    --accent-pink: #ec4899;
    --accent-green: #10b981;
    --accent-orange: #f59e0b;
    --accent-red: #ef4444;
    --accent-purple-glow: rgba(124, 58, 237, 0.3);
    --accent-blue-glow: rgba(59, 130, 246, 0.3);
    --accent-pink-glow: rgba(236, 72, 153, 0.3);
    --accent-green-glow: rgba(16, 185, 129, 0.3);
    --border-color: rgba(255, 255, 255, 0.06);
    --border-active: rgba(124, 58, 237, 0.4);
    --radius: 12px;
    --radius-lg: 16px;
    --shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
    --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

html, body {
    height: 100%;
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    background: var(--bg-primary);
    color: var(--text-primary);
    line-height: 1.6;
    -webkit-font-smoothing: antialiased;
}

a { text-decoration: none; color: inherit; }

/* ===== App Layout ===== */
.app {
    display: flex;
    min-height: 100vh;
}

/* ===== Sidebar ===== */
.sidebar {
    width: 260px;
    min-height: 100vh;
    background: var(--sidebar-bg);
    border-right: 1px solid var(--border-color);
    display: flex;
    flex-direction: column;
    padding: 24px 16px;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 100;
}

.sidebar-header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 0 8px 24px;
    border-bottom: 1px solid var(--border-color);
    margin-bottom: 24px;
}

.bot-avatar {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    border: 2px solid var(--accent-purple);
    box-shadow: 0 0 16px var(--accent-purple-glow);
}

.bot-info h2 {
    font-size: 1rem;
    font-weight: 700;
    letter-spacing: -0.02em;
}

.status-badge {
    font-size: 0.7rem;
    color: var(--accent-green);
    display: flex;
    align-items: center;
    gap: 4px;
}

.status-badge i { font-size: 0.45rem; }

.nav-links {
    list-style: none;
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.nav-item a {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 14px;
    border-radius: 10px;
    font-size: 0.875rem;
    font-weight: 500;
    color: var(--text-secondary);
    transition: var(--transition);
}

.nav-item a:hover {
    background: rgba(124, 58, 237, 0.08);
    color: var(--text-primary);
}

.nav-item.active a {
    background: linear-gradient(135deg, rgba(124, 58, 237, 0.15), rgba(59, 130, 246, 0.1));
    color: var(--accent-purple);
    border: 1px solid var(--border-active);
    font-weight: 600;
}

.nav-item a i {
    font-size: 1rem;
    width: 20px;
    text-align: center;
}

/* ===== Main Content ===== */
.content {
    flex: 1;
    margin-left: 260px;
    padding: 32px 40px;
    min-height: 100vh;
    background: var(--bg-primary);
}

.content-header {
    margin-bottom: 32px;
}

.content-header h1 {
    font-size: 1.75rem;
    font-weight: 800;
    letter-spacing: -0.03em;
    background: linear-gradient(135deg, var(--text-primary), var(--accent-purple));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.subtitle {
    color: var(--text-secondary);
    font-size: 0.875rem;
    margin-top: 4px;
}

/* ===== Stats Grid ===== */
.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 16px;
    margin-bottom: 40px;
}

.stat-card {
    background: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-lg);
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: var(--transition);
    position: relative;
    overflow: hidden;
}

.stat-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.stat-card:hover {
    transform: translateY(-2px);
    border-color: var(--border-active);
    box-shadow: var(--shadow);
}

.card-purple::before { background: linear-gradient(90deg, var(--accent-purple), transparent); }
.card-blue::before { background: linear-gradient(90deg, var(--accent-blue), transparent); }
.card-pink::before { background: linear-gradient(90deg, var(--accent-pink), transparent); }
.card-green::before { background: linear-gradient(90deg, var(--accent-green), transparent); }

.card-purple .stat-icon { background: var(--accent-purple-glow); color: var(--accent-purple); }
.card-blue .stat-icon { background: var(--accent-blue-glow); color: var(--accent-blue); }
.card-pink .stat-icon { background: var(--accent-pink-glow); color: var(--accent-pink); }
.card-green .stat-icon { background: var(--accent-green-glow); color: var(--accent-green); }

.stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
    flex-shrink: 0;
}

.stat-info { display: flex; flex-direction: column; }

.stat-value {
    font-size: 1.5rem;
    font-weight: 800;
    letter-spacing: -0.03em;
    line-height: 1.2;
}

.stat-label {
    font-size: 0.8rem;
    color: var(--text-secondary);
    font-weight: 500;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

/* ===== Sections ===== */
.section { margin-bottom: 32px; }

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
}

.section-header h2 {
    font-size: 1.1rem;
    font-weight: 700;
    letter-spacing: -0.02em;
    display: flex;
    align-items: center;
    gap: 8px;
}

.section-header h2 i {
    color: var(--accent-purple);
    font-size: 0.95rem;
}

.view-all {
    font-size: 0.8rem;
    color: var(--accent-purple);
    font-weight: 600;
    transition: var(--transition);
}

.view-all:hover { color: var(--accent-blue); }

/* ===== Guild Cards ===== */
.guilds-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 16px;
}

.guilds-grid.full-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
}

.guild-card {
    background: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-lg);
    padding: 20px;
    transition: var(--transition);
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.guild-card:hover {
    transform: translateY(-2px);
    border-color: var(--border-active);
    box-shadow: 0 8px 32px rgba(124, 58, 237, 0.1);
}

.guild-card-header {
    display: flex;
    align-items: center;
    gap: 14px;
}

.guild-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    object-fit: cover;
}

.guild-icon-placeholder {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    background: linear-gradient(135deg, var(--accent-purple), var(--accent-blue));
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.875rem;
    font-weight: 700;
    color: #fff;
    flex-shrink: 0;
}

.guild-icon-placeholder.large {
    width: 64px;
    height: 64px;
    font-size: 1.2rem;
    border-radius: 16px;
}

.guild-meta h3 {
    font-size: 0.95rem;
    font-weight: 600;
    line-height: 1.3;
}

.guild-meta p {
    font-size: 0.8rem;
    color: var(--text-secondary);
    display: flex;
    align-items: center;
    gap: 6px;
}

.guild-meta p i { font-size: 0.7rem; }

.guild-link {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 14px;
    background: rgba(124, 58, 237, 0.08);
    border: 1px solid rgba(124, 58, 237, 0.15);
    border-radius: 10px;
    font-size: 0.8rem;
    font-weight: 600;
    color: var(--accent-purple);
    transition: var(--transition);
}

.guild-link:hover {
    background: rgba(124, 58, 237, 0.15);
    border-color: var(--accent-purple);
}

.guild-link i {
    font-size: 0.7rem;
    transition: var(--transition);
}

.guild-link:hover i { transform: translateX(4px); }

/* ===== Guild Detail ===== */
.breadcrumb {
    font-size: 0.8rem;
    color: var(--text-secondary);
    margin-bottom: 16px;
}

.breadcrumb a {
    color: var(--accent-purple);
    transition: var(--transition);
}

.breadcrumb a:hover { color: var(--accent-blue); }
.breadcrumb .current { color: var(--text-primary); font-weight: 600; }

.guild-header-info {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 8px;
}

.guild-header-icon {
    width: 64px;
    height: 64px;
    border-radius: 16px;
    object-fit: cover;
    border: 2px solid var(--border-active);
}

.guild-header-info h1 { font-size: 1.5rem; }

/* ===== Tab Navigation ===== */
.tab-nav {
    display: flex;
    gap: 4px;
    margin-bottom: 28px;
    background: var(--bg-secondary);
    padding: 4px;
    border-radius: 12px;
    border: 1px solid var(--border-color);
}

.tab {
    padding: 10px 18px;
    border-radius: 8px;
    font-size: 0.8rem;
    font-weight: 500;
    color: var(--text-secondary);
    transition: var(--transition);
    display: flex;
    align-items: center;
    gap: 8px;
}

.tab:hover {
    color: var(--text-primary);
    background: rgba(255, 255, 255, 0.04);
}

.tab.active {
    background: var(--accent-purple);
    color: #fff;
    font-weight: 600;
}

.tab i { font-size: 0.85rem; }

/* ===== Settings Grid ===== */
.settings-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 14px;
}

.setting-card {
    background: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: var(--radius);
    padding: 18px;
    display: flex;
    align-items: center;
    gap: 14px;
    transition: var(--transition);
}

.setting-card:hover { border-color: var(--border-active); }

.setting-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    background: var(--accent-purple-glow);
    color: var(--accent-purple);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.9rem;
    flex-shrink: 0;
}

.setting-info {
    display: flex;
    flex-direction: column;
    gap: 4px;
    flex: 1;
}

.setting-label {
    font-size: 0.8rem;
    color: var(--text-secondary);
    font-weight: 500;
}

.setting-value {
    font-size: 0.9rem;
    font-weight: 600;
    font-family: 'JetBrains Mono', 'Fira Code', monospace;
}

.not-configured {
    color: var(--text-muted);
    font-style: italic;
    font-weight: 400;
    font-family: 'Inter', sans-serif;
}

.setting-actions {}

/* ===== Forms ===== */
.form-card {
    background: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-lg);
    padding: 24px;
}

.form-group {
    margin-bottom: 20px;
}

.form-group:last-child { margin-bottom: 0; }

.form-group label {
    display: block;
    font-size: 0.85rem;
    font-weight: 600;
    margin-bottom: 8px;
    color: var(--text-primary);
}

.input {
    width: 100%;
    background: var(--bg-primary);
    border: 1px solid var(--border-color);
    border-radius: 8px;
    padding: 10px 14px;
    font-size: 0.875rem;
    color: var(--text-primary);
    font-family: 'Inter', sans-serif;
    transition: var(--transition);
    outline: none;
}

.input:focus {
    border-color: var(--accent-purple);
    box-shadow: 0 0 0 3px var(--accent-purple-glow);
}

.textarea {
    min-height: 100px;
    resize: vertical;
}

.input-row {
    display: flex;
    gap: 10px;
    align-items: center;
}

.input-row .input { flex: 1; }

.color-row {
    display: flex;
    gap: 10px;
    align-items: center;
}

.color-picker {
    width: 44px;
    height: 40px;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    cursor: pointer;
    background: transparent;
    padding: 2px;
}

.color-row .input { flex: 1; }

.form-hint {
    font-size: 0.75rem;
    color: var(--text-muted);
    margin-top: 6px;
    display: block;
}

.toggle-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

/* ===== Buttons ===== */
.btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 18px;
    border-radius: 8px;
    font-size: 0.8rem;
    font-weight: 600;
    border: none;
    cursor: pointer;
    transition: var(--transition);
    font-family: 'Inter', sans-serif;
}

.btn-primary {
    background: var(--accent-purple);
    color: #fff;
}

.btn-primary:hover {
    background: #6d28d9;
    box-shadow: 0 4px 16px var(--accent-purple-glow);
}

.btn-secondary {
    background: rgba(255, 255, 255, 0.06);
    color: var(--text-secondary);
    border: 1px solid var(--border-color);
}

.btn-secondary:hover {
    background: rgba(255, 255, 255, 0.1);
    color: var(--text-primary);
    border-color: var(--border-active);
}

.btn-sm {
    padding: 6px 12px;
    font-size: 0.75rem;
}

.btn-full { width: 100%; justify-content: center; }

/* ===== Toggle Switch ===== */
.switch {
    position: relative;
    display: inline-block;
    width: 44px;
    height: 24px;
    flex-shrink: 0;
}

.switch input {
    opacity: 0;
    width: 0;
    height: 0;
}

.slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: var(--bg-primary);
    border: 1px solid var(--border-color);
    transition: var(--transition);
    border-radius: 24px;
}

.slider::before {
    content: '';
    position: absolute;
    height: 18px;
    width: 18px;
    left: 2px;
    bottom: 2px;
    background: var(--text-muted);
    transition: var(--transition);
    border-radius: 50%;
}

input:checked + .slider {
    background: var(--accent-purple);
    border-color: var(--accent-purple);
}

input:checked + .slider::before {
    transform: translateX(20px);
    background: #fff;
}

/* ===== Commands List ===== */
.commands-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.command-card {
    background: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: var(--radius);
    padding: 16px 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    transition: var(--transition);
}

.command-card:hover { border-color: var(--border-active); }

.command-info { flex: 1; }

.command-header-row {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 4px;
}

.command-name {
    font-size: 0.9rem;
    font-weight: 700;
    color: var(--accent-purple);
    font-family: 'JetBrains Mono', monospace;
}

.command-aliases {
    font-size: 0.7rem;
    color: var(--text-muted);
    font-family: 'JetBrains Mono', monospace;
    background: rgba(255, 255, 255, 0.04);
    padding: 2px 8px;
    border-radius: 6px;
}

.command-desc {
    font-size: 0.8rem;
    color: var(--text-secondary);
    margin-bottom: 6px;
}

.command-usage {
    font-size: 0.75rem;
    font-family: 'JetBrains Mono', monospace;
    color: var(--text-muted);
    background: var(--bg-primary);
    padding: 4px 10px;
    border-radius: 6px;
    display: inline-block;
}

.command-toggle {
    display: flex;
    align-items: center;
}

/* ===== Embed Editor ===== */
.embed-editor-layout {
    display: grid;
    grid-template-columns: 1fr 400px;
    gap: 24px;
    align-items: start;
}

.embed-form { min-width: 0; }

.embed-preview-container {
    position: sticky;
    top: 32px;
}

.preview-title {
    font-size: 0.9rem;
    font-weight: 600;
    color: var(--text-secondary);
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.preview-title i { color: var(--accent-purple); }

.discord-embed-preview {
    background: #2f3136;
    border-radius: 4px;
    display: flex;
    overflow: hidden;
    font-family: 'Inter', sans-serif;
}

.embed-color-bar {
    width: 4px;
    flex-shrink: 0;
    background: var(--accent-purple);
}

.embed-content {
    padding: 16px;
    flex: 1;
    display: flex;
    gap: 16px;
}

.embed-body {
    flex: 1;
    min-width: 0;
}

.embed-preview-title {
    font-size: 0.95rem;
    font-weight: 700;
    color: #fff;
    margin-bottom: 8px;
}

.embed-preview-desc {
    font-size: 0.85rem;
    color: #dcddde;
    line-height: 1.5;
    margin-bottom: 12px;
    white-space: pre-wrap;
    word-break: break-word;
}

.embed-preview-image {
    width: 100%;
    max-height: 200px;
    object-fit: cover;
    border-radius: 4px;
    margin-top: 8px;
}

.embed-preview-footer {
    font-size: 0.75rem;
    color: #72767d;
    margin-top: 12px;
    padding-top: 8px;
    border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.embed-preview-thumbnail {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    object-fit: cover;
    flex-shrink: 0;
}

.embed-thumbnail-wrapper {
    flex-shrink: 0;
}

.embed-preview-image-wrapper {}

/* ===== Moderation ===== */
.mod-features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 14px;
}

.mod-feature-card {
    background: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: var(--radius);
    padding: 20px;
    display: flex;
    align-items: flex-start;
    gap: 14px;
    transition: var(--transition);
}

.mod-feature-card:hover { border-color: var(--border-active); }

.mod-feature-icon {
    width: 42px;
    height: 42px;
    border-radius: 10px;
    background: var(--accent-purple-glow);
    color: var(--accent-purple);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1rem;
    flex-shrink: 0;
}

.mod-feature-info {
    flex: 1;
}

.mod-feature-info h3 {
    font-size: 0.9rem;
    font-weight: 600;
    margin-bottom: 4px;
}

.mod-feature-info p {
    font-size: 0.8rem;
    color: var(--text-secondary);
    line-height: 1.5;
}

.mod-feature-status {
    font-size: 0.7rem;
    color: var(--accent-green);
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 4px;
    flex-shrink: 0;
    padding-top: 2px;
}

/* ===== Empty State ===== */
.empty-state {
    text-align: center;
    padding: 48px 24px;
    color: var(--text-muted);
    background: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-lg);
}

.empty-state i {
    font-size: 2.5rem;
    margin-bottom: 12px;
    display: block;
    color: var(--text-muted);
}

.empty-state p {
    font-size: 0.85rem;
}

/* ===== Toast Notifications ===== */
.toast-container {
    position: fixed;
    bottom: 24px;
    right: 24px;
    z-index: 1000;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.toast {
    background: var(--bg-card);
    border: 1px solid var(--border-color);
    border-radius: 10px;
    padding: 12px 20px;
    font-size: 0.85rem;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 8px;
    min-width: 250px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
    opacity: 0;
    transform: translateY(12px);
    transition: all 0.3s ease;
}

.toast.show {
    opacity: 1;
    transform: translateY(0);
}

.toast-success {
    border-left: 3px solid var(--accent-green);
    color: var(--accent-green);
}

.toast-error {
    border-left: 3px solid var(--accent-red);
    color: var(--accent-red);
}

/* ===== Responsive ===== */
@media (max-width: 1024px) {
    .embed-editor-layout {
        grid-template-columns: 1fr;
    }
    .embed-preview-container {
        position: static;
    }
}

@media (max-width: 768px) {
    .sidebar { display: none; }
    .content { margin-left: 0; padding: 20px; }
    .stats-grid { grid-template-columns: repeat(2, 1fr); }
    .guilds-grid { grid-template-columns: 1fr; }
    .tab-nav { flex-wrap: wrap; }
}

/* ===== Scrollbar ===== */
::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: var(--bg-primary); }
::-webkit-scrollbar-thumb { background: var(--bg-card); border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: var(--text-muted); }

/* ===== Animations ===== */
@keyframes fadeIn {
    from { opacity: 0; transform: translateY(8px); }
    to { opacity: 1; transform: translateY(0); }
}

.stat-card,
.guild-card,
.setting-card,
.command-card,
.mod-feature-card {
    animation: fadeIn 0.4s ease-out both;
}

.stat-card:nth-child(1) { animation-delay: 0.05s; }
.stat-card:nth-child(2) { animation-delay: 0.1s; }
.stat-card:nth-child(3) { animation-delay: 0.15s; }
.stat-card:nth-child(4) { animation-delay: 0.2s; }

.guild-card:nth-child(1) { animation-delay: 0.1s; }
.guild-card:nth-child(2) { animation-delay: 0.15s; }
.guild-card:nth-child(3) { animation-delay: 0.2s; }
.guild-card:nth-child(4) { animation-delay: 0.25s; }
.guild-card:nth-child(5) { animation-delay: 0.3s; }
.guild-card:nth-child(6) { animation-delay: 0.35s; }

.command-card:nth-child(1) { animation-delay: 0.05s; }
.command-card:nth-child(2) { animation-delay: 0.1s; }
.command-card:nth-child(3) { animation-delay: 0.15s; }
.command-card:nth-child(4) { animation-delay: 0.2s; }
.command-card:nth-child(5) { animation-delay: 0.25s; }
""".trimIndent()
