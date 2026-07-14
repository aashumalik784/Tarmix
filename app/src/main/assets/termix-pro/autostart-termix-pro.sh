#!/data/data/com.termux/files/usr/bin/bash

# ANSI Colors
RED='\033[1;31m'
GREEN='\033[1;32m'
YELLOW='\033[1;33m'
BLUE='\033[1;34m'
CYAN='\033[1;36m'
NC='\033[0m'

# 1. Colorful Welcome Banner
clear
echo -e "${BLUE}"
echo "╔══════════════════════════════════════╗"
echo "║                                      ║"
echo "║      🚀 Welcome To Tremix 🚀         ║"
echo "║                                      ║"
echo "║   ⚡ Your Ultimate Terminal Setup    ║"
echo "║                                      ║"
echo "╚══════════════════════════════════════╝"
echo -e "${NC}"
echo -e "${GREEN}✅ System Ready | $(date '+%H:%M:%S')${NC}"
echo ""

# 2. Auto Git Clone / Update
REPO_URL="https://github.com/aashumalik784/Tremix.git"
REPO_DIR="$HOME/Tremix"

if [ -d "$REPO_DIR" ]; then
    echo -e "${CYAN}📁 Repository found, updating...${NC}"
    cd "$REPO_DIR"
    git pull origin main 2>/dev/null
    echo -e "${GREEN}✅ Repository updated!${NC}"
else
    echo -e "${YELLOW}📥 Cloning repository for the first time...${NC}"
    pkg install git -y 2>/dev/null
    git clone "$REPO_URL" "$REPO_DIR" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Repository cloned successfully!${NC}"
        cd "$REPO_DIR"
    else
        echo -e "${RED}❌ Failed to clone repository!${NC}"
    fi
fi

# 3. Setup Process
SETUP_MARKER="$HOME/.termix-pro-initialized"
LOCAL_BACKUP="/sdcard/TermiX-Pro/ubuntu-rootfs.tar.zst"
ROOTFS_DIR="$PREFIX/var/lib/proot-distro/containers/ubuntu"

if [ -f "$SETUP_MARKER" ]; then
    echo -e "${GREEN}✅ TermiX-Pro already configured!${NC}"
    echo -e "${CYAN}💡 Tip: cd ~/Tremix to access your project${NC}"
    exit 0
fi

termux-setup-storage 2>/dev/null
sleep 2

if [ ! -d "/sdcard" ]; then
    echo -e "${RED}❌ Storage permission not granted!${NC}"
    exit 1
fi

mkdir -p /sdcard/TermiX-Pro
pkg install -y proot-distro zstd wget git 2>/dev/null

git config --global user.email "aashumalik784@users.noreply.github.com"
git config --global user.name "Aashumalik784"

if [ -f "$LOCAL_BACKUP" ]; then
    echo -e "${YELLOW}⚡ Fast Setup from backup...${NC}"
    rm -rf "$ROOTFS_DIR"
    mkdir -p "$PREFIX/var/lib/proot-distro/containers"
    tar --use-compress-program=unzstd -xf "$LOCAL_BACKUP" \
        -C "$PREFIX/var/lib/proot-distro/containers/" \
        --strip-components=9
    echo -e "${GREEN}✅ Restore Complete!${NC}"
else
    echo -e "${YELLOW}⏳ Installing from scratch...${NC}"
    proot-distro install ubuntu 2>/dev/null
    proot-distro login ubuntu -- bash -c "
        export DEBIAN_FRONTEND=noninteractive
        apt update -y 2>/dev/null
        apt install -y python3 openjdk-21-jdk g++ nodejs php golang-go rustc ruby kotlin nginx docker.io mariadb-client postgresql wget unzip curl 2>/dev/null
        cd /opt
        wget -q https://storage.googleapis.com/dart-archive/channels/stable/release/latest/sdk/dartsdk-linux-arm64-release.zip 2>/dev/null
        unzip -q dartsdk-linux-arm64-release.zip 2>/dev/null
        rm -f dartsdk-linux-arm64-release.zip
        ln -sf /opt/dart-sdk/bin/dart /usr/local/bin/dart 2>/dev/null
    "
    tar --use-compress-program=zstd -cf "$LOCAL_BACKUP" "$PREFIX/var/lib/proot-distro/installed-rootfs/ubuntu"
    echo -e "${GREEN}✅ Backup created!${NC}"
fi

echo -e "${CYAN}🌐 Starting Nginx...${NC}"
proot-distro login ubuntu -- bash -c "nginx 2>/dev/null"

touch "$SETUP_MARKER"
echo -e "${GREEN}🎉 TermiX-Pro Setup Complete!${NC}"
echo -e "${CYAN}💡 Your code is at: ~/Tremix${NC}"
echo -e "${CYAN} Type: cd ~/Tremix${NC}"

# Load colorful terminal
source "$HOME/termix-pro/colors.sh" 2>/dev/null
