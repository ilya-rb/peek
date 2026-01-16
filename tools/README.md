# Tools

This directory contains utility scripts for the project.

## GitHub Integration

This project supports GitHub integration through two methods:

1. **MCP Server (Recommended for AI agents)** - Configured in `.cursor/mcp.json`
   - Allows AI agents to create pull requests directly
   - See `.cursor/MCP_SETUP.md` for setup instructions

2. **Command-line scripts** - Manual PR creation scripts (see below)

## create-pr.mjs

A zx script to create GitHub pull requests from the command line.

### Prerequisites

1. **Install zx**: `npm install -g zx` or `brew install zx` (macOS)
   - See [zx installation guide](https://github.com/google/zx#install)

2. **Authentication (choose one):**

   **Option 1: GitHub CLI (Recommended)**
   - Install GitHub CLI: `brew install gh` (macOS) or see [GitHub CLI installation guide](https://cli.github.com/manual/installation)
   - Authenticate: `gh auth login`

   **Option 2: GitHub API Token**
   - Create a personal access token with `repo` scope from [GitHub Settings](https://github.com/settings/tokens)
   - Set it as an environment variable: `export GITHUB_TOKEN=your_token`
   - Or configure it in git: `git config --global github.token your_token`

### Usage

```bash
# Basic usage (uses current branch as head, main as base)
zx tools/create-pr.mjs --title "Fix bug in login"

# With description
zx tools/create-pr.mjs --title "Add feature" --body "This PR adds a new feature"

# Specify branches
zx tools/create-pr.mjs -t "Update dependencies" -h feature-branch -B develop

# Create draft PR
zx tools/create-pr.mjs -t "WIP: New feature" --draft

# Specify repository explicitly
zx tools/create-pr.mjs -t "Fix issue" -r owner/repo
```

### Options

- `-t, --title TITLE` - PR title (required)
- `-b, --body BODY` - PR body/description
- `-h, --head BRANCH` - Head branch (default: current branch)
- `-B, --base BRANCH` - Base branch (default: main)
- `-r, --repo REPO` - Repository in format owner/repo (default: auto-detect from git remote)
- `-d, --draft` - Create as draft PR
- `--help` - Show help message

### Examples

```bash
# Create PR from current branch
zx tools/create-pr.mjs --title "Fix login bug" --body "Resolves issue #123"

# Create draft PR from feature branch
zx tools/create-pr.mjs -t "WIP: New UI" --head feature/new-ui --draft

# Create PR targeting develop branch
zx tools/create-pr.mjs -t "Update dependencies" -B develop
```

## create-pr.sh

Legacy bash version of the PR creation script. The zx version (`create-pr.mjs`) is recommended.
