## Git Workflow

### Branch Naming
- Feature branches: `feature/short-description`
- Bug fixes: `fix/short-description`
- Refactoring: `refactor/short-description`
- Add the date at the end of the branch name (e.g., "feature/my-branch--2025-8-22")

### Commit Messages
- Use conventional commits: `type(scope): description`
- Examples: `feat(ui): add dark mode toggle`, `fix(api): handle null responses`

### Branch Management
- When adding a new feature or fix that is being performed from instructions in the todo.md file, create a new branch
- Create all branches from the current branch - prompt first if this seems like a bad idea
- Once a new feature/fix/change is done, commit it with a descriptive thorough commit message.
- Do not merge branches after commit
- Do not delete branches after merge
- Do not squash commits before merging