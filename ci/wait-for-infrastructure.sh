#!/bin/bash
set -euo

until curl 'localhost.localstack.cloud:4566/health' --silent | grep -q "\"initScripts\": \"initialized\""; do
     sleep 5
     echo "Waiting for LocalStack Initialisation scripts to have been run..."
done
