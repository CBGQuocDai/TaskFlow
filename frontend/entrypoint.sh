#!/bin/sh

echo "window.env = {" > /usr/share/nginx/html/env.js
echo "  VITE_BACKEND: \"$VITE_BACKEND\" " >> /usr/share/nginx/html/env.js
echo "}" >> /usr/share/nginx/html/env.js

# Khởi động Nginx
exec nginx -g "daemon off;"