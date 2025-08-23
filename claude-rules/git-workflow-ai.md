# Git Workflow (AI-Optimized)

## Branch Naming
**Format:** `type/descriptive-name--YYYY-MM-DD`
- `feature/` - New features/enhancements
- `fix/` - Bug fixes and corrections  
- `refactor/` - Code restructuring
- **Descriptive names required** (clarity > brevity)
- **Date suffix mandatory** (e.g., `--2025-08-23`)

**Examples:**
- `feature/initial-setup-contact-import--2025-08-23`
- `fix/gift-price-tracking-null-error--2025-08-23`
- `refactor/person-management-ui-cleanup--2025-08-23`

## Commit Messages
**Format:** `type(scope): description`

**Types:** feat, fix, refactor, docs, test, chore, style
**Scopes:** ui, api, db, core, tests, config

**Examples:**
- `feat(ui): add initial setup contact import flow`
- `fix(api): handle null responses in price tracking`
- `refactor(db): optimize gift query performance`
- `test(core): add person relationship validation tests`

## Branch Management Workflow
1. **New task from todo.md** → Create new branch from current
2. **Prompt first** if branching from current seems wrong
3. **Complete work** → Commit with thorough descriptive message
4. **NEVER:** merge branches, delete branches, squash commits

## Validation Rules
- Branch name includes date suffix
- Descriptive branch names (not abbreviations)
- Conventional commit format enforced
- All commits from proper branch context