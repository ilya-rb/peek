#!/usr/bin/env zx

/**
 * Script to create a GitHub pull request
 * Usage: zx tools/create-pr.mjs [options]
 *
 * Options:
 *   -t, --title TITLE       PR title (required)
 *   -b, --body BODY         PR body/description
 *   -h, --head BRANCH       Head branch (default: current branch)
 *   -B, --base BRANCH       Base branch (default: main)
 *   -r, --repo REPO         Repository in format owner/repo (default: auto-detect)
 *   -d, --draft             Create as draft PR
 *   --help                  Show this help message
 */

import { chalk } from 'zx'

// Parse command line arguments
const args = process.argv.slice(2)
let title = ''
let body = ''
let headBranch = ''
let baseBranch = 'main'
let repo = ''
let draft = false

// Function to print usage
function usage() {
  console.log(`
Usage: zx tools/create-pr.mjs [options]

Create a GitHub pull request.

Options:
    -t, --title TITLE       PR title (required)
    -b, --body BODY         PR body/description
    -h, --head BRANCH       Head branch (default: current branch)
    -B, --base BRANCH       Base branch (default: main)
    -r, --repo REPO         Repository in format owner/repo (default: auto-detect)
    -d, --draft             Create as draft PR
    --help                  Show this help message

Examples:
    zx tools/create-pr.mjs --title "Fix bug in login" --body "This PR fixes the login issue"
    zx tools/create-pr.mjs -t "Add feature" -b "Description" --head feature-branch --base develop
    zx tools/create-pr.mjs -t "WIP: New feature" --draft
`)
}

// Parse arguments
for (let i = 0; i < args.length; i++) {
  const arg = args[i]
  const nextArg = args[i + 1]

  switch (arg) {
    case '-t':
    case '--title':
      if (nextArg && !nextArg.startsWith('-')) {
        title = nextArg
        i++
      }
      break
    case '-b':
    case '--body':
      if (nextArg && !nextArg.startsWith('-')) {
        body = nextArg
        i++
      }
      break
    case '-h':
    case '--head':
      if (nextArg && !nextArg.startsWith('-')) {
        headBranch = nextArg
        i++
      }
      break
    case '-B':
    case '--base':
      if (nextArg && !nextArg.startsWith('-')) {
        baseBranch = nextArg
        i++
      }
      break
    case '-r':
    case '--repo':
      if (nextArg && !nextArg.startsWith('-')) {
        repo = nextArg
        i++
      }
      break
    case '-d':
    case '--draft':
      draft = true
      break
    case '--help':
      usage()
      process.exit(0)
    default:
      if (arg.startsWith('-')) {
        console.error(chalk.red(`Error: Unknown option: ${arg}`))
        usage()
        process.exit(1)
      }
      break
  }
}

// Validate required arguments
if (!title) {
  console.error(chalk.red('Error: --title is required'))
  usage()
  process.exit(1)
}

// Get current branch if HEAD_BRANCH is not specified
if (!headBranch) {
  try {
    headBranch = (await $`git rev-parse --abbrev-ref HEAD`).stdout.trim()
    if (!headBranch) {
      throw new Error('Unable to determine current branch')
    }
    console.log(chalk.yellow(`Using current branch: ${headBranch}`))
  } catch (error) {
    console.error(chalk.red('Error: Not in a git repository or unable to determine current branch'))
    process.exit(1)
  }
}

// Auto-detect repository if not provided
if (!repo) {
  try {
    const remoteUrl = (await $`git config --get remote.origin.url`).stdout.trim()
    if (!remoteUrl) {
      throw new Error('No remote URL found')
    }

    // Parse remote URL (handles both https and ssh formats)
    const match = remoteUrl.match(/github\.com[:/]([^/]+)\/([^/]+?)(?:\.git)?$/)
    if (match) {
      repo = `${match[1]}/${match[2]}`
      console.log(chalk.yellow(`Detected repository: ${repo}`))
    } else {
      throw new Error(`Unable to parse repository from remote URL: ${remoteUrl}`)
    }
  } catch (error) {
    console.error(chalk.red(`Error: Unable to detect repository. Please specify with --repo owner/repo`))
    process.exit(1)
  }
}

// Check if GitHub CLI is available
const ghAvailable = await which('gh').catch(() => null)

if (ghAvailable) {
  console.log(chalk.green('Using GitHub CLI (gh)'))

  // Check if user is authenticated
  try {
    await $`gh auth status`
  } catch (error) {
    console.error(chalk.red('Error: GitHub CLI is not authenticated. Run \'gh auth login\' first.'))
    process.exit(1)
  }

  // Build gh command
  const ghArgs = [
    'pr',
    'create',
    '--title', title,
    '--head', headBranch,
    '--base', baseBranch,
  ]

  if (body) {
    ghArgs.push('--body', body)
  }

  if (draft) {
    ghArgs.push('--draft')
  }

  try {
    await $`gh ${ghArgs}`
    console.log(chalk.green('✓ Pull request created successfully!'))
  } catch (error) {
    console.error(chalk.red('✗ Failed to create pull request'))
    process.exit(1)
  }
} else {
  // Fallback to GitHub API
  console.log(chalk.yellow('GitHub CLI not found. Using GitHub API...'))

  // Check for GitHub token
  let githubToken = process.env.GITHUB_TOKEN
  if (!githubToken) {
    try {
      githubToken = (await $`git config --get github.token`).stdout.trim()
    } catch (error) {
      // Token not found in git config
    }
  }

  if (!githubToken) {
    console.error(chalk.red('Error: GITHUB_TOKEN environment variable or git config \'github.token\' is required'))
    console.error(chalk.yellow('You can set it with: export GITHUB_TOKEN=your_token'))
    console.error(chalk.yellow('Or configure it: git config --global github.token your_token'))
    process.exit(1)
  }

  // Prepare JSON payload
  const payload = {
    title,
    head: headBranch,
    base: baseBranch,
    body: body || '',
    draft,
  }

  try {
    const response = await fetch(`https://api.github.com/repos/${repo}/pulls`, {
      method: 'POST',
      headers: {
        'Authorization': `token ${githubToken}`,
        'Accept': 'application/vnd.github.v3+json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    })

    const responseBody = await response.json()

    if (response.status === 201) {
      console.log(chalk.green('✓ Pull request created successfully!'))
      console.log(chalk.green(`  URL: ${responseBody.html_url}`))
    } else {
      console.error(chalk.red('✗ Failed to create pull request'))
      console.error(chalk.red(`  HTTP Code: ${response.status}`))
      console.error(chalk.red(`  Response: ${JSON.stringify(responseBody, null, 2)}`))
      process.exit(1)
    }
  } catch (error) {
    console.error(chalk.red('✗ Failed to create pull request'))
    console.error(chalk.red(`  Error: ${error.message}`))
    process.exit(1)
  }
}
