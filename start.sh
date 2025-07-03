#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

echo "Starting all services with Docker Compose..."
# Start all containers in the background
docker compose up -d

echo ""
echo "Waiting a few seconds for Ollama service to be ready..."
sleep 5

echo ""
echo "Pulling the llama3 model (this might take a while on the first run)..."
# Execute the pull command inside the running ollama container
docker exec -it ollama ollama pull llama3

echo ""
echo "Setup complete! All services are running and the llama3 model is available."
echo "You can access:"
#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

echo "Starting all services with Docker Compose..."
# Start all containers in the background
docker compose up -d

echo ""
echo "Waiting a few seconds for Ollama service to be ready..."
sleep 5

echo ""
echo "Pulling the llama3 model (this might take a while on the first run)..."
# Execute the pull command inside the running ollama container
docker exec -it ollama ollama pull llama3

echo ""
echo "Setup complete! All services are running and the llama3 model is available."
echo "You can access:"
echo "  - n8n: http://localhost:5678"
echo "  - ollama: http://ollama:11434/api/generate"
echo "  - App: http://localhost:8080/"