#!/data/data/com.termux/files/usr/bin/bash

# ANSI Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# 1. Colorful PS1 (Prompt)
export PS1="${BLUE}┌─[${GREEN}\u${BLUE}@${CYAN}\h${BLUE}]─[${YELLOW}\w${BLUE}]${NC}\n${BLUE}└─\$ ${NC}"

# 2. LS_COLORS for ls command
export LS_COLORS='di=1;34:ln=1;36:so=1;35:pi=1;33:ex=1;32:bd=1;34;46:cd=1;34;43:su=1;31;47:sg=1;36;47:tw=1;32;41:ow=1;33;41:st=1;37;44:'

# 3. Enable color for grep, diff, etc.
export GREP_COLOR='1;33'
export GREP_OPTIONS='--color=auto'
alias grep='grep --color=auto'
alias diff='diff --color=auto'
alias ls='ls --color=auto'
alias ll='ls -la --color=auto'

# 4. Optional: Color for man pages (if you install less)
export LESS='-R'

echo -e "${CYAN}🎨 Colorful terminal enabled!${NC}"
