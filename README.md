# TermiX-Pro - Advanced Development Environment

13 fully working languages/tools plus Docker CLI (daemon not supported in PRoot), running inside Ubuntu PRoot-Distro on Android.

## Tools Status

- Python 3.14, Java (OpenJDK 25), C++ (GCC 15), Node.js 22, PHP 8.5, Go 1.26, Rust 1.93, Ruby 3.3, Kotlin, Dart 3.5, Nginx 1.28 - all working
- MariaDB 11.8 (MySQL-compatible) - working
- PostgreSQL 18 - working
- Docker 29 - CLI only, daemon cannot run inside PRoot (kernel features like cgroups/namespaces not available)

## Note on Docker

PRoot is a userspace ptrace-based emulation layer. It cannot provide the kernel features the Docker daemon needs. The docker CLI works, but dockerd will not start here. This is a known PRoot limitation.

## Quick Setup

```bash
git clone https://github.com/aashumalik784/TermiX-Pro.git
cd TermiX-Pro
bash scripts/install-termix-pro.sh
proot-distro login ubuntu
```

## Manual Setup Inside Ubuntu

```bash
apt update
apt install -y python3 openjdk-21-jdk g++ nodejs npm php golang-go rustc cargo ruby kotlin
apt install -y mariadb-server
service mariadb start
apt install -y postgresql postgresql-contrib
apt install -y nginx fcgiwrap spawn-fcgi
apt install -y docker.io
```

For Dart:

```bash
wget -q https://storage.googleapis.com/dart-archive/channels/stable/release/3.5.4/sdk/dartsdk-linux-x64-release.zip
unzip -q dartsdk-linux-x64-release.zip -d /usr/lib/
ln -sf /usr/lib/dart-sdk/bin/dart /usr/local/bin/dart
rm -f dartsdk-linux-x64-release.zip
```

## Web Dashboard

```bash
nginx
spawn-fcgi -s /run/fcgiwrap.socket -M 766 -- /usr/sbin/fcgiwrap
```

Then open browser: http://localhost:8080

## Building APK

```bash
./gradlew assembleDebug
```

Or use GitHub Actions for cloud build.

## License

MIT License

## Author

Aashu Malik (@aashumalik784)

## Important: Before Installing

If you already have Termux (or any other Termux fork) installed on your device, uninstall it first before installing TermiX-Pro. Since TermiX-Pro shares the same package name (`com.termux`) as the original Termux, installing it alongside another Termux-based app will cause a package conflict and the installation will fail.

## First Launch

On first launch, TermiX-Pro automatically sets up 14 programming languages/tools inside a Ubuntu PRoot-Distro environment in the background. This requires:
- An active internet connection
- 10-20 minutes (depending on network speed)
- Roughly 1-2 GB of free storage

Please keep the app open and connected to the internet during this initial setup.
