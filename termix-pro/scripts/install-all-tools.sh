#!/bin/bash
# TermiX-Pro Complete Development Environment Installer
# This script installs 14+ programming languages and tools

echo "🚀 Installing TermiX-Pro Development Environment..."
echo "This will install: Python, Java, C++, Node.js, PHP, Go, Rust, Ruby, Kotlin, Dart, Nginx, Docker, MySQL, PostgreSQL"
echo ""

# Check if proot-distro is installed
if ! command -v proot-distro &> /dev/null; then
    echo "📦 Installing proot-distro..."
    pkg install proot-distro -y
fi

# Install Ubuntu if not already installed
if ! proot-distro list | grep -q "ubuntu"; then
    echo "📦 Installing Ubuntu container..."
    proot-distro install ubuntu
fi

# Login to Ubuntu and install all tools
echo "🔧 Installing tools inside Ubuntu..."
proot-distro login ubuntu -- bash -c "
    apt update
    apt upgrade -y
    
    echo '📦 Installing Programming Languages...'
    apt install -y python3 openjdk-21-jdk g++ nodejs npm php golang-go rustc cargo ruby wget curl git
    
    echo '📦 Installing Web Server...'
    apt install -y nginx fcgiwrap spawn-fcgi
    
    echo '📦 Installing Databases...'
    apt install -y mysql-client postgresql postgresql-contrib
    
    echo '📦 Installing Docker...'
    apt install -y docker.io
    
    echo '📦 Installing Kotlin...'
    apt install -y kotlin
    
    echo '📦 Installing Dart...'
    wget -q https://storage.googleapis.com/dart-archive/channels/stable/release/3.5.4/sdk/dartsdk-linux-x64-release.zip
    apt install -y unzip
    unzip -q dartsdk-linux-x64-release.zip -d /usr/lib/
    ln -sf /usr/lib/dart-sdk/bin/dart /usr/local/bin/dart
    rm dartsdk-linux-x64-release.zip
    
    echo '✅ All tools installed successfully!'
"

echo ""
echo "🎉 TermiX-Pro installation complete!"
echo "Run 'proot-distro login ubuntu' to start development"
echo "Then run 'bash /path/to/termix-pro/scripts/start-web-dashboard.sh' to start web interface"
