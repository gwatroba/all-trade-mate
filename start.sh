#!/bin/bash
set -e

docker compose up -d

echo "Waiting for Ollama service to start..."
sleep 5

echo "Pulling llama3 model..."
docker exec -it ollama ollama pull llama3

echo "Setup complete. All services are running."