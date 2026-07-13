#!/bin/bash
echo "Content-Type: text/html"
echo ""
TOOL=$(echo $QUERY_STRING | cut -d'=' -f2)
echo "<html><head><title>TermiX-Pro: $TOOL</title></head><body>"
echo "<h1>Tool Status: $TOOL</h1>"
case $TOOL in
    python|python3) echo "<p>Python</p><pre>$(python3 --version 2>&1)</pre>" ;;
    java) echo "<p>Java</p><pre>$(java -version 2>&1)</pre>" ;;
    g++) echo "<p>C++</p><pre>$(g++ --version | head -n 1)</pre>" ;;
    node) echo "<p>Node.js</p><pre>$(node --version)</pre>" ;;
    php) echo "<p>PHP</p><pre>$(php -v | head -n 1)</pre>" ;;
    go) echo "<p>Go</p><pre>$(go version)</pre>" ;;
    rust) echo "<p>Rust</p><pre>$(rustc --version)</pre>" ;;
    ruby) echo "<p>Ruby</p><pre>$(ruby --version)</pre>" ;;
    kotlin) echo "<p>Kotlin</p><pre>$(kotlinc -version 2>&1 | head -n 1)</pre>" ;;
    dart) echo "<p>Dart</p><pre>$(dart --version)</pre>" ;;
    nginx) echo "<p>Nginx</p><pre>$(nginx -v 2>&1)</pre>" ;;
    docker) echo "<p>Docker CLI (daemon not supported in PRoot)</p><pre>$(docker --version 2>&1)</pre>" ;;
    mariadb|mysql) echo "<p>MariaDB (MySQL-compatible)</p><pre>$(mysql --version 2>&1)</pre>" ;;
    postgresql|psql) echo "<p>PostgreSQL</p><pre>$(psql --version)</pre>" ;;
    *) echo "<p>Checking $TOOL...</p><pre>$(which $TOOL 2>&1)</pre>" ;;
esac
echo "<br><a href='/'>Back to Dashboard</a></body></html>"
