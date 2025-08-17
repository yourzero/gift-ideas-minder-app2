#!/usr/bin/env bash
set -euo pipefail
ENV_FILE="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/.mcp/.env"
if [ -f "$ENV_FILE" ]; then
  # shellcheck disable=SC2046
  export $(grep -v '^#' "$ENV_FILE" | xargs -I{} echo {})
  echo "Environment loaded from .mcp/.env"
else
  echo ".mcp/.env not found"
fi