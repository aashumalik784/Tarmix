#!/data/data/com.termux/files/usr/bin/bash

# Ubuntu Container Direct Access Aliases
UBUNTU_LOGIN="proot-distro login ubuntu --"

# Programming Languages
alias python3-ubuntu="$UBUNTU_LOGIN python3"
alias java="$UBUNTU_LOGIN java"
alias javac="$UBUNTU_LOGIN javac"
alias node="$UBUNTU_LOGIN node"
alias npm="$UBUNTU_LOGIN npm"
alias npx="$UBUNTU_LOGIN npx"
alias go="$UBUNTU_LOGIN go"
alias rustc="$UBUNTU_LOGIN rustc"
alias cargo="$UBUNTU_LOGIN cargo"
alias ruby="$UBUNTU_LOGIN ruby"
alias kotlinc="$UBUNTU_LOGIN kotlinc"
alias dart="$UBUNTU_LOGIN dart"
alias php="$UBUNTU_LOGIN php"

# Databases
alias mysql="$UBUNTU_LOGIN mysql"
alias psql="$UBUNTU_LOGIN psql"
alias mariadbd="$UBUNTU_LOGIN mariadbd"

# Web & DevOps
alias nginx="$UBUNTU_LOGIN nginx"
alias docker="$UBUNTU_LOGIN docker"

# Utility
alias ubuntu-shell="$UBUNTU_LOGIN bash"

echo -e "\033[1;32m✅ Ubuntu tools loaded! Use directly: node, java, go, etc.\033[0m"
