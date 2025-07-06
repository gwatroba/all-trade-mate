#!/bin/bash
set -e

docker compose up --build -d

echo "Setup complete. All services are running."
echo "http://localhost:8080"