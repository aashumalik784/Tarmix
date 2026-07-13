#!/bin/bash
# Start TermiX-Pro Web Dashboard

echo "🌐 Starting TermiX-Pro Web Dashboard..."

# Copy files to Ubuntu container
proot-distro login ubuntu -- bash -c "
    # Copy dashboard files
    cp -r /data/data/com.termux/files/home/termux-app/termix-pro/web-dashboard/* /var/www/
    
    # Copy nginx config
    cp /data/data/com.termux/files/home/termux-app/termix-pro/configs/nginx-default-site /etc/nginx/sites-enabled/default
    
    # Set permissions
    chmod +x /var/www/cgi-bin/execute.sh
    
    # Start services
    nginx
    spawn-fcgi -s /run/fcgiwrap.socket -M 766 -- /usr/sbin/fcgiwrap
"

echo "✅ Web Dashboard is LIVE!"
echo "🌍 Open browser: http://localhost:8080"
