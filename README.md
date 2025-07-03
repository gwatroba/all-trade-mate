# TradeMate - Allegro API Client & Automation Hub

TradeMate is a backend application built with Spring Boot designed to interact with the Allegro REST API. It serves as a foundation for building automated sales and customer communication workflows. The project is fully containerized using Docker and includes services for workflow automation (n8n) and local AI model hosting (Ollama).

This project demonstrates a modern, robust, and scalable application architecture, following industry best practices for security, configuration, and deployment.

---

## ✨ Features

* **Secure Allegro API Integration:** A clean, reusable client for interacting with Allegro's REST API.
* **Automated OAuth 2.0 Token Management:** Handles the full OAuth 2.0 refresh token flow automatically, including secure token storage and rotation.
* **RESTful API:** Exposes business logic through a clean, secured REST API.
* **Basic Auth Security:** The application's API endpoints are protected with Basic Authentication via Spring Security.
* **Containerized Environment:** The entire application stack (Spring Boot App, n8n, Ollama) is orchestrated with a single `docker-compose` file for easy, reproducible setup.
* **Asynchronous Job Processing:** Demonstrates handling of long-running tasks in the background using Spring's `@Async` capabilities (e.g., sending mass messages).
* **CI/CD Pipeline:** Includes a GitHub Actions workflow for continuous integration, automatically building and testing the application on every push.

---

## 🛠️ Tech Stack & Tools

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.2-brightgreen)
![Docker](https://img.shields.io/badge/Docker-blue)
![Maven](https://img.shields.io/badge/Maven-red)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI/CD-black)
![n8n](https://img.shields.io/badge/n8n-automation-purple)
![Ollama](https://img.shields.io/badge/Ollama-Local_LLM-lightgrey)

---

## 🚀 Getting Started

This project is fully containerized, and the recommended way to run it is using Docker.

### Prerequisites

* **Docker & Docker Compose:** Ensure you have Docker Desktop (or Docker Engine with the Compose plugin) installed.
* **Allegro Developer Account:** You need an active Allegro account with a registered application.

### Step 1: Initial Application Setup on Allegro

Ensure your registered application on the Allegro Developer Portal has the following settings:
* **Grant Type:** `Authorization Code`
* **Redirect URIs:** `http://localhost:8080/login/oauth2/code/allegro`
* **Permissions:** At least `allegro:api:orders:read` and `allegro:api:messaging`.

### Step 2: Local Configuration

This project uses template files for secrets. You must create your local copies before running the application.

1.  **Credentials File:**
    * Copy the template file `src/main/resources/application.properties.example` to a new file named `src/main/resources/application.properties`.
    * Open `application.properties` and fill in your actual `YOUR_CLIENT_ID` and `YOUR_CLIENT_SECRET`.
    * This file is git-ignored and will not be committed.

2.  **Token File:**
    * This project requires an initial, long-lived `refresh_token` to function. After generating it in the next step, copy `allegro_token.json.example` to `allegro_token.json` and paste your token there.

### Step 3: Generating Initial Tokens

This manual procedure is required **only once** for the first run or if your `refresh_token` is lost or invalidated.

1.  **Get Authorization Code:**
    * Build the authorization URL by inserting your `client_id`:
      `https://allegro.pl/auth/oauth/authorize?response_type=code&client_id=YOUR_CLIENT_ID&redirect_uri=http://localhost:8080/login/oauth2/code/allegro`
    * Paste it into your browser, log in, and authorize the application.
    * From the redirect URL (`http://localhost:8080/login/oauth2/code/allegro?code=...`), copy the `AUTHORIZATION_CODE_FROM_BROWSER`.

2.  **Exchange Code for Tokens via `curl`:**
    * First, Base64-encode your credentials in the terminal:
        ```bash
        echo -n 'YOUR_CLIENT_ID:YOUR_CLIENT_SECRET' | base64
        ```
    * Execute the `curl` request, pasting your code and encoded keys:
        ```bash
        curl -X POST \
        '[https://allegro.pl/auth/oauth/token?grant_type=authorization_code&code=AUTHORIZATION_CODE_FROM_BROWSER&redirect_uri=http://localhost:8080/login/oauth2/code/allegro](https://allegro.pl/auth/oauth/token?grant_type=authorization_code&code=AUTHORIZATION_CODE_FROM_BROWSER&redirect_uri=http://localhost:8080/login/oauth2/code/allegro)' \
        -H 'Authorization: Basic YOUR_ENCODED_KEYS'
        ```

3.  **Save the Refresh Token:**
    * From the JSON response, copy the `refresh_token` value.
    * Paste it into your newly created `allegro_token.json` file.

### Step 4: Running the Full Stack with Docker

With all configuration files in place, you can now start the entire application stack with a single command.

1.  **Build and Start Containers:**
    * Open a terminal in the project's root directory and run the provided helper script:
        ```bash
        ./start.sh
        ```
    * This script will start the `tradeMate`, `n8n`, and `Ollama` containers in the background.

2.  **(First Run Only) Download the AI Model:**
    * The `start.sh` script will automatically execute this command. It tells the running Ollama container to download the Llama 3 model. This may take a few minutes depending on your internet connection.
        ```bash
        docker exec -it ollama ollama pull llama3
        ```

### Step 5: Accessing the Services

Once everything is running, the services are available at:
* **Your Application API:** `http://localhost:8080/api`
* **n8n Automation:** `http://localhost:5678`
* **Ollama Local AI:** `http://localhost:11434`

---

## 🔐 API Security

The endpoints under `/api/**` are protected using **HTTP Basic Authentication**. To access them, you must provide the credentials defined in your `application.properties` file.

**Example `curl` request:**
```bash
curl -u YOUR_SPRING_USERNAME:YOUR_SPRING_PASSWORD http://localhost:8080/api/orders
```

---

## 🗺️ Project Roadmap

* [ ] **Offer Management:** Implement endpoints for listing, creating, and updating Allegro offers.
* [ ] **Web Interface:** Add a simple frontend using Thymeleaf to interact with the API from a browser.
* [ ] **Database Integration:** Store historical data (sent messages, order snapshots) in a PostgreSQL database, also managed via Docker Compose.
* [ ] **Advanced CI/CD:** Expand the GitHub Actions workflow to automatically build and push the Docker image to a registry (e.g., Docker Hub or a cloud provider's registry).