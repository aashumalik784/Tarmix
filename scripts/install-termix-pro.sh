#!/bin/bash
# TermiX-Pro Installation Script
# This script sets up the complete development environment

echo " Installing TermiX-Pro Development Environment..."

# Update packages
pkg update -y && pkg upgrade -y

# Install proot-distro
pkg install proot-distro -y

# Install Ubuntu
proot-distro install ubuntu

# Login to Ubuntu and install tools
proot-distro login ubuntu -- bash -c "
    apt update
    apt install -y nginx fcgiwrap spawn-fcgi python3 openjdk-21-jdk g++ nodejs php golang-go rustc ruby wget curl git
"

echo "✅ TermiX-Pro installation complete!"
echo " Run 'proot-distro login ubuntu' to start development"
