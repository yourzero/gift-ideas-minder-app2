#!/usr/bin/env bash
set -euo pipefail

# Project-local MCP config
export MCP_CONFIG="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/.mcp/mcp.jsonc"
export AGENTS_CONFIG="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/.mcp/agents/agents.jsonc"

# Load .env if present
ENV_FILE="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/.mcp/.env"
if [ -f "$ENV_FILE" ]; then
  # shellcheck disable=SC2046
  export $(grep -v '^#' "$ENV_FILE" | xargs -I{} echo {})
fi

echo "Using MCP_CONFIG=$MCP_CONFIG"
echo "Using AGENTS_CONFIG=$AGENTS_CONFIG"

# Launch Claude Code Studio (adjust if your binary is different)
if command -v claude-code-studio >/dev/null 2>&1; then
  exec claude-code-studio
else
  echo "claude-code-studio not found on PATH. Start your host manually with these env vars."
fi