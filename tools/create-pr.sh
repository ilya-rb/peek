#!/bin/bash

# Script to create a GitHub pull request
# Usage: ./tools/create-pr.sh [options]
#
# Options:
#   -t, --title TITLE       PR title (required)
#   -b, --body BODY         PR body/description
#   -h, --head BRANCH       Head branch (default: current branch)
#   -B, --base BRANCH       Base branch (default: main)
#   -r, --repo REPO         Repository in format owner/repo (default: auto-detect)
#   -d, --draft             Create as draft PR
#   --help                  Show this help message

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default values
TITLE=""
BODY=""
HEAD_BRANCH=""
BASE_BRANCH="main"
REPO=""
DRAFT=false

# Function to print usage
usage() {
    cat << EOF
Usage: $0 [options]

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
    $0 --title "Fix bug in login" --body "This PR fixes the login issue"
    $0 -t "Add feature" -b "Description" --head feature-branch --base develop
    $0 -t "WIP: New feature" --draft

EOF
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -t|--title)
            TITLE="$2"
            shift 2
            ;;
        -b|--body)
            BODY="$2"
            shift 2
            ;;
        -h|--head)
            HEAD_BRANCH="$2"
            shift 2
            ;;
        -B|--base)
            BASE_BRANCH="$2"
            shift 2
            ;;
        -r|--repo)
            REPO="$2"
            shift 2
            ;;
        -d|--draft)
            DRAFT=true
            shift
            ;;
        --help)
            usage
            exit 0
            ;;
        *)
            echo -e "${RED}Error: Unknown option: $1${NC}" >&2
            usage
            exit 1
            ;;
    esac
done

# Validate required arguments
if [[ -z "$TITLE" ]]; then
    echo -e "${RED}Error: --title is required${NC}" >&2
    usage
    exit 1
fi

# Get current branch if HEAD_BRANCH is not specified
if [[ -z "$HEAD_BRANCH" ]]; then
    HEAD_BRANCH=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "")
    if [[ -z "$HEAD_BRANCH" ]]; then
        echo -e "${RED}Error: Not in a git repository or unable to determine current branch${NC}" >&2
        exit 1
    fi
    echo -e "${YELLOW}Using current branch: $HEAD_BRANCH${NC}"
fi

# Auto-detect repository if not provided
if [[ -z "$REPO" ]]; then
    REMOTE_URL=$(git config --get remote.origin.url 2>/dev/null || echo "")
    if [[ -z "$REMOTE_URL" ]]; then
        echo -e "${RED}Error: Unable to detect repository. Please specify with --repo owner/repo${NC}" >&2
        exit 1
    fi
    
    # Parse remote URL (handles both https and ssh formats)
    if [[ "$REMOTE_URL" =~ github\.com[:/]([^/]+)/([^/]+)(\.git)?$ ]]; then
        REPO="${BASH_REMATCH[1]}/${BASH_REMATCH[2]}"
        REPO="${REPO%.git}"  # Remove .git suffix if present
    else
        echo -e "${RED}Error: Unable to parse repository from remote URL: $REMOTE_URL${NC}" >&2
        echo -e "${YELLOW}Please specify repository with --repo owner/repo${NC}" >&2
        exit 1
    fi
    echo -e "${YELLOW}Detected repository: $REPO${NC}"
fi

# Check if GitHub CLI is available
if command -v gh &> /dev/null; then
    echo -e "${GREEN}Using GitHub CLI (gh)${NC}"
    
    # Check if user is authenticated
    if ! gh auth status &> /dev/null; then
        echo -e "${RED}Error: GitHub CLI is not authenticated. Run 'gh auth login' first.${NC}" >&2
        exit 1
    fi
    
    # Build gh command
    GH_CMD="gh pr create"
    GH_CMD="$GH_CMD --title \"$TITLE\""
    
    if [[ -n "$BODY" ]]; then
        GH_CMD="$GH_CMD --body \"$BODY\""
    fi
    
    GH_CMD="$GH_CMD --head $HEAD_BRANCH"
    GH_CMD="$GH_CMD --base $BASE_BRANCH"
    
    if [[ "$DRAFT" == true ]]; then
        GH_CMD="$GH_CMD --draft"
    fi
    
    # Execute the command
    eval "$GH_CMD"
    
    if [[ $? -eq 0 ]]; then
        echo -e "${GREEN}✓ Pull request created successfully!${NC}"
    else
        echo -e "${RED}✗ Failed to create pull request${NC}" >&2
        exit 1
    fi
    
# Fallback to GitHub API
else
    echo -e "${YELLOW}GitHub CLI not found. Using GitHub API...${NC}"
    
    # Check for GitHub token
    GITHUB_TOKEN="${GITHUB_TOKEN:-$(git config --get github.token 2>/dev/null || echo "")}"
    
    if [[ -z "$GITHUB_TOKEN" ]]; then
        echo -e "${RED}Error: GITHUB_TOKEN environment variable or git config 'github.token' is required${NC}" >&2
        echo -e "${YELLOW}You can set it with: export GITHUB_TOKEN=your_token${NC}" >&2
        echo -e "${YELLOW}Or configure it: git config --global github.token your_token${NC}" >&2
        exit 1
    fi
    
    # Prepare JSON payload
    JSON_PAYLOAD=$(cat <<EOF
{
  "title": "$TITLE",
  "head": "$HEAD_BRANCH",
  "base": "$BASE_BRANCH",
  "body": "$BODY",
  "draft": $DRAFT
}
EOF
)
    
    # Create PR using GitHub API
    RESPONSE=$(curl -s -w "\n%{http_code}" \
        -X POST \
        -H "Authorization: token $GITHUB_TOKEN" \
        -H "Accept: application/vnd.github.v3+json" \
        -H "Content-Type: application/json" \
        -d "$JSON_PAYLOAD" \
        "https://api.github.com/repos/$REPO/pulls")
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY_RESPONSE=$(echo "$RESPONSE" | sed '$d')
    
    if [[ "$HTTP_CODE" == "201" ]]; then
        PR_URL=$(echo "$BODY_RESPONSE" | grep -o '"html_url":"[^"]*"' | cut -d'"' -f4)
        echo -e "${GREEN}✓ Pull request created successfully!${NC}"
        echo -e "${GREEN}  URL: $PR_URL${NC}"
    else
        echo -e "${RED}✗ Failed to create pull request${NC}" >&2
        echo -e "${RED}  HTTP Code: $HTTP_CODE${NC}" >&2
        echo -e "${RED}  Response: $BODY_RESPONSE${NC}" >&2
        exit 1
    fi
fi
