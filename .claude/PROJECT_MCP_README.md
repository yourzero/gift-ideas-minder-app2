# Project-local MCP & Agents

This directory contains a **project-scoped** MCP setup so your servers/agents are per-repo, not global.

## Files
- `.mcp/mcp.jsonc` — servers definition (JetBrains, GitHub, ADB, SQLite; optional OCR/Search placeholders).
- `.mcp/agents/agents.jsonc` — task-focused agents for Gift Idea Minder.
- `.mcp/.env` — tokens/paths (copy from `.env.example`).
- `scripts/run-claude-project-mcp.(sh|bat)` — launch Claude Code Studio pointed at this config.
- `scripts/export-env.(sh|bat)` — source environment variables for other shells/tools.

## Usage

1. Copy `.mcp/.env.example` → `.mcp/.env` and fill values.
2. Start Claude Code Studio with the project config:
   - **Linux/macOS**: `scripts/run-claude-project-mcp.sh`
   - **Windows**: `scripts\\run-claude-project-mcp.bat`

If your host doesn't support selecting a config file directly, use the **sync-free** approach above (we set env vars before launch). Otherwise, import these JSONC files via the host UI and keep the JSONC here as the source of truth.

> Agents are optional. If your host expects agents to be created via UI, paste from `.mcp/agents/agents.jsonc`.

## .gitignore
Add `.mcp/.env` to your `.gitignore`.