# CLAUDE.md

This file contains instructions for Claude Code when working with the Gift Idea Minder Android project.

## Essential Project Info

**Gift Idea Minder** is an Android app built with Jetpack Compose, Room, Hilt, and MVVM architecture. It helps users capture, organize, and track gift ideas with AI-powered suggestions, price tracking, OCR import, and person/relationship management.

## AI-Optimized Rules (Use These)

When working on this project, reference these token-optimized rule files:

- **Commands**: `claude-rules/commands-ai.md` - Build, test, and SDK configuration
- **Architecture**: `claude-rules/architecture-ai.md` - Tech stack and critical patterns
- **Standards**: `claude-rules/coding-standards-ai.md` - Code style and requirements
- **Workflows**: `claude-rules/workflows-ai.md` - Common development tasks
- **Config**: `claude-rules/configuration-ai.md` - Required setup
- **Features**: `claude-rules/features-ai.md` - Key features and integrations
- **Roadmap**: `claude-rules/roadmap-ai.md` - Future plans and limitations

## Human-Readable Documentation

For detailed explanations, see the full documentation files:

- `claude-rules/commands.md`
- `claude-rules/architecture.md` 
- `claude-rules/coding-standards.md`
- `claude-rules/workflows.md`
- `claude-rules/configuration.md`
- `claude-rules/features.md`
- `claude-rules/roadmap.md`

## Project Status

### Core Features
- **Person Management**: Multi-step flow with relationship-based date prompting
- **Gift Management**: Price tracking, AI suggestions, OCR import, budget alerts
- **Integrations**: Gemini API, CamelCamelCamel, ML Kit, device contacts

### Known Incomplete Items
- Share intent navigation to `add_gift?sharedText=...`
- Full file import (only CSV works)
- AI SMS history summarization (stubbed)

## Development Principles

Prioritize:
1. **Maintainable code** over clever solutions
2. **Proper testing** at all layers  
3. **Excellent UX** with consistent patterns
4. **Security best practices** for personal data

## Configuration
- load ./.claude/.mcp.json for the MCP server configuration

## Command Execution
- Use PowerShell for all shell commands
- Default shell: `powershell.exe`



## Notifications
- **Permission prompts**: powershell.exe -c "(New-Object Media.SoundPlayer 'C:\Users\justin\Music\notifications\new-notification-022-370046.wav').PlaySync()"
- **Task completion**: powershell.exe -c "(New-Object Media.SoundPlayer 'C:\Users\justin\Music\notifications\notification-sound-3-262896.wav').PlaySync()"
- **Errors/Warnings**: powershell.exe -c "(New-Object Media.SoundPlayer 'C:\Users\justin\Music\notifications\error-04-199275.wav').PlaySync()"
