@echo off
setlocal ENABLEDELAYEDEXPANSION
for %%I in ("%~dp0..") do set PROOT=%%~fI
set ENV_FILE=%PROOT%\.mcp\.env
if not exist "%ENV_FILE%" (
  echo .mcp\.env not found
  exit /b 1
)
for /f "usebackq tokens=1,2 delims== eol=#" %%A in ("%ENV_FILE%") do (
  set "%%A=%%B"
  echo Loaded %%A
)
endlocal