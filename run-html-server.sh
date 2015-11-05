#!/bin/sh

echo "Starting HTTP Server on port 9090"
cd example-clients/html && ruby -run -e httpd . -p 9090