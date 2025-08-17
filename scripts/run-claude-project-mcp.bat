@echo off
setlocal ENABLEDELAYEDEXPANSION

rem Project-local MCP config
for %%I in ("%~dp0..") do set PROOT=%%~fI
set MCP_CONFIG=%PROOT%\.claude\mcp.jsonc
set AGENTS_CONFIG=%PROOT%\.claude\agents\agents.jsonc

rem Load .env if present (simple parser: KEY=VALUE per line)
set ENV_FILE=%PROOT%\.claude\.env
if exist "%ENV_FILE%" (
  for /f "usebackq tokens=1,2 delims== eol=#" %%A in ("%ENV_FILE%") do (
    set "%%A=%%B"
  )
)

echo Using MCP_CONFIG=%MCP_CONFIG%
echo Using AGENTS_CONFIG=%AGENTS_CONFIG%

where claude-code-studio >nul 2>&1
if %errorlevel%==0 (
  start "" claude-code-studio
) else (
  echo claude-code-studio not found on PATH. Start your host manually with these env vars.
)
endlocal