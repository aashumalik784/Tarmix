#!/system/bin/sh
# TermiX-Pro Auto-Setup Script for Termux App

TERMIX_MARKER="/sdcard/.termix-pro-initialized"

if [ -f "$TERMIX_MARKER" ]; then
    exit 0
fi

# Show welcome message
echo "╔══════════════════════════════════════════════════════╗"
echo "║     Welcome to TermiX-Pro Development Environment    ║"
echo "║                                                      ║"
echo "║  Installing 14 programming languages & tools...      ║"
echo "║  This may take 10-15 minutes. Please wait...         ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

# Install proot-distro
pkg install proot-distro -y

# Install Ubuntu
proot-distro install ubuntu

# Install all tools
proot-distro login ubuntu -- bash -c "
    apt update
    apt upgrade -y
    
    # Programming Languages
    apt install -y python3 openjdk-21-jdk g++ nodejs npm php golang-go rustc cargo ruby
    
    # Web & Server
    apt install -y nginx fcgiwrap spawn-fcgi
    
    # Databases
    apt install -y mysql-client postgresql postgresql-contrib
    
    # Container
    apt install -y docker.io
    
    # Kotlin & Dart
    apt install -y kotlin
    
    # Dart installation
    wget -q https://storage.googleapis.com/dart-archive/channels/stable/release/3.5.4/sdk/dartsdk-linux-x64-release.zip
    apt install -y unzip
    unzip -q dartsdk-linux-x64-release.zip -d /usr/lib/
    ln -sf /usr/lib/dart-sdk/bin/dart /usr/local/bin/dart
    rm -f dartsdk-linux-x64-release.zip
"

# Mark setup complete
touch "$TERMIX_MARKER"

echo ""
echo "✅ TermiX-Pro setup complete!"
echo ""
echo " Quick Start:"
echo "   proot-distro login ubuntu     - Enter Ubuntu environment"
echo "   termix-web                    - Start web dashboard"
echo ""
echo "Happy Coding! 🚀"
