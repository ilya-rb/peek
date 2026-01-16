# GitHub MCP Server Setup

This project is configured to use the official GitHub MCP (Model Context Protocol) server, which allows AI agents to interact with GitHub repositories directly.

## Configuration

The MCP server is configured in `.cursor/mcp.json`. This configuration uses the official `@modelcontextprotocol/server-github` package.

**Security Note**: The configuration uses environment variables instead of hardcoding tokens. The `GITHUB_TOKEN` must be set in your system environment, and Cursor will automatically pass it to the MCP server when it runs.

## Prerequisites

1. **Node.js and npm** - Required to run the MCP server
   - The server runs via `npx`, so Node.js must be installed
   - Check installation: `node --version` and `npm --version`

2. **GitHub Personal Access Token**
   - Create a token with appropriate scopes from [GitHub Settings](https://github.com/settings/tokens)
   - Required scopes: `repo` (for full repository access including PR creation)
   - Set it as an environment variable: `export GITHUB_TOKEN=your_token`

## Setup Instructions

1. **Set your GitHub token:**
   ```bash
   export GITHUB_TOKEN=your_personal_access_token
   ```
   
   To make it persistent, add it to your shell profile (e.g., `~/.zshrc` or `~/.bashrc`):
   ```bash
   echo 'export GITHUB_TOKEN=your_personal_access_token' >> ~/.zshrc
   source ~/.zshrc
   ```

2. **Restart Cursor** - After setting the token, restart Cursor to load the MCP server configuration.
   
   **Important for macOS users**: GUI applications don't automatically inherit shell environment variables. You have two options:
   - **Option A (Recommended)**: Launch Cursor from the terminal so it inherits your environment:
     ```bash
     open -a Cursor
     ```
   - **Option B**: Use a tool like [EnvPane](https://github.com/hschmidt/EnvPane) or set environment variables for GUI apps using `launchctl` (though this method is deprecated on newer macOS versions)

3. **Verify the setup** - The AI agent should now be able to:
   - Create pull requests
   - View repository information
   - Access GitHub resources through the MCP protocol

## How It Works

The MCP server configuration in `.cursor/mcp.json` tells Cursor to:
- Use `npx` to run the official GitHub MCP server via command execution
- Automatically inherit the `GITHUB_TOKEN` environment variable from your system environment
- The server runs automatically when Cursor starts
- **No tokens are stored in the configuration file** - they're read from environment variables at runtime

## Troubleshooting

- **Token not working**: Ensure your token has the `repo` scope
- **Server not starting**: Check that Node.js is installed and `GITHUB_TOKEN` is set
- **Permission errors**: Verify your token has access to the repository you're working with

## Additional Resources

- [MCP GitHub Server Documentation](https://github.com/modelcontextprotocol/servers/tree/main/src/github)
- [Model Context Protocol Documentation](https://modelcontextprotocol.io/)
