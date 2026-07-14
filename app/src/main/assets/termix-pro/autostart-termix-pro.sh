#!/system/bin/sh
# TermiX-Pro Magic Auto-Setup Script

SETUP_MARKER="$HOME/.termix-pro-initialized"
LOCAL_BACKUP="/sdcard/TermiX-Pro/ubuntu-rootfs.tar.zst"
ROOTFS_DIR="$PREFIX/var/lib/proot-distro/containers/ubuntu"

if [ -f "$SETUP_MARKER" ]; then
    echo "TermiX-Pro already configured!"
    exit 0
fi

echo "Setting up TermiX-Pro Development Environment..."

echo "Requesting storage permission..."
termux-setup-storage
sleep 3

if [ ! -d "/sdcard" ]; then
    echo "Storage permission nahi mili! Please Allow dabao."
    exit 1
fi

mkdir -p /sdcard/TermiX-Pro

echo "Installing required packages..."
pkg install -y proot-distro zstd wget git unzip

echo "Configuring Git automatically..."
git config --global user.email "aashumalik784@users.noreply.github.com"
git config --global user.name "Aashumalik784"

if [ -f "$LOCAL_BACKUP" ]; then
    echo "Found Local Backup! Fast Setup Starting..."
    rm -rf "$ROOTFS_DIR"
    mkdir -p "$PREFIX/var/lib/proot-distro/containers"
    echo "Extracting backup (Wait 2-3 mins)..."
    tar --use-compress-program=unzstd -xf "$LOCAL_BACKUP" -C "$PREFIX/var/lib/proot-distro/containers/"
    if [ $? -eq 0 ]; then
        echo "Local Restore Complete!"
    else
        echo "Extraction failed!"
        exit 1
    fi
else
    echo "No local backup. Installing from scratch..."
    proot-distro install ubuntu
    proot-distro login ubuntu -- bash -c "
        export DEBIAN_FRONTEND=noninteractive
        apt update -y
        apt install -y python3 openjdk-21-jdk g++ nodejs npm php golang-go rustc cargo ruby kotlin nginx fcgiwrap spawn-fcgi docker.io mariadb-server postgresql postgresql-contrib wget unzip curl
        service mariadb start
        wget -q https://storage.googleapis.com/dart-archive/channels/stable/release/3.5.4/sdk/dartsdk-linux-arm64-release.zip
        unzip -q dartsdk-linux-arm64-release.zip -d /usr/lib/
        ln -sf /usr/lib/dart-sdk/bin/dart /usr/local/bin/dart
        rm -f dartsdk-linux-arm64-release.zip
    "
    echo "Creating backup for next time..."
    tar --use-compress-program="zstd -19 -T0" -cf "$LOCAL_BACKUP" -C "$PREFIX/var/lib/proot-distro/containers" ubuntu
    echo "Backup created!"
fi

echo "Configuring Nginx dashboard..."
ROOTFS="$PREFIX/var/lib/proot-distro/containers/ubuntu/rootfs"
mkdir -p "$ROOTFS/var/www/html"
cat > "$ROOTFS/etc/nginx/sites-enabled/default" << 'NGINXEOF'
server {
    listen 8080 default_server;
    listen [::]:8080 default_server;
    root /var/www/html;
    index index.html;
    server_name _;
    location / {
        try_files $uri $uri/ =404;
    }
}
NGINXEOF
if [ ! -s "$ROOTFS/var/www/html/index.html" ]; then
    echo "<h1>TermiX-Pro Dashboard</h1>" > "$ROOTFS/var/www/html/index.html"
fi

echo "Starting Nginx..."
proot-distro login ubuntu -- bash -c "service mariadb start; pkill nginx 2>/dev/null; sleep 1; nginx"

touch "$SETUP_MARKER"
echo "TermiX-Pro Setup Complete!"
