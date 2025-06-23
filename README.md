# TradeMate - Allegro API Client

A simple web application based on Spring Boot, designed for integration with the Allegro REST API. This project serves as a learning tool and a foundation for future sales management features.

## Tech Stack
* Java 21
* Spring Boot 3.3.2
* Maven
* Spring Web & WebFlux (for handling the REST API and HTTP communication with `WebClient`)
* Lombok

## Setup and First Run

To run the application, a one-time configuration is required. This involves setting up your credentials and generating the initial API tokens.

### Step 1: Configure Your Application on Allegro

Ensure you have a registered application in the Allegro Developer Portal with the following settings:
* **Grant Type:** `Authorization Code`
* **Redirect URIs:** `http://localhost:8080/login/oauth2/code/allegro`

### Step 2: Create Local Configuration Files

This project uses template files to manage secrets. You need to create your local copies of these files.

1.  **Credentials File:**
    * Copy the template file `src/main/resources/application.properties.example` to a new file named `src/main/resources/application.properties`.
    * Open `application.properties` and fill in your actual `YOUR_CLIENT_ID` and `YOUR_CLIENT_SECRET`.
    * The `application.properties` file is git-ignored and will not be committed.

2.  **Token File:**
    * After generating tokens in Step 3, you will copy `allegro_token.json.example` to `allegro_token.json` and paste your refresh token there.

### Step 3: Generating Tokens (Initial Setup / Recovery)

This procedure is required for the first run or any time you lose or invalidate your `refresh_token`.

#### 3.1. Get the Authorization Code

1.  Build the authorization URL by inserting your `client_id`:
    ```
    [https://allegro.pl/auth/oauth/authorize?response_type=code&client_id=YOUR_CLIENT_ID&redirect_uri=http://localhost:8080/login/oauth2/code/allegro](https://allegro.pl/auth/oauth/authorize?response_type=code&client_id=YOUR_CLIENT_ID&redirect_uri=http://localhost:8080/login/oauth2/code/allegro)
    ```
2.  Paste the above URL into your browser, log in to your Allegro account, and authorize the application.
3.  You will be redirected to a non-existent `localhost` page. Look at the browser's address bar - it will look like this:
    `http://localhost:8080/login/oauth2/code/allegro?code=AUTHORIZATION_CODE_FROM_BROWSER`
4.  Copy the `AUTHORIZATION_CODE_FROM_BROWSER` value. It is valid only for a short time.

#### 3.2. Exchange the Code for an Access Token and Refresh Token

We will use the `curl` command-line tool for this. It's the most reliable method, independent of our application code.

1.  **Prepare the `Authorization` Header**:
    * Join your `client_id` and `client_secret` with a colon: `clientId:clientSecret`.
    * Encode this string using Base64. You can do this in a terminal (on Mac/Linux):
        ```bash
        echo -n 'YOUR_CLIENT_ID:YOUR_CLIENT_SECRET' | base64
        ```
    * Copy the resulting string (e.g., `YTI5...Hg=`).

2.  **Execute the `curl` Request**:
    * Open your terminal and paste the command below, replacing `AUTHORIZATION_CODE_FROM_BROWSER` and `ENCODED_KEYS`:
        ```bash
        curl -X POST \
        '[https://allegro.pl/auth/oauth/token?grant_type=authorization_code&code=AUTHORIZATION_CODE_FROM_BROWSER&redirect_uri=http://localhost:8080/login/oauth2/code/allegro](https://allegro.pl/auth/oauth/token?grant_type=authorization_code&code=AUTHORIZATION_CODE_FROM_BROWSER&redirect_uri=http://localhost:8080/login/oauth2/code/allegro)' \
        -H 'Authorization: Basic ENCODED_KEYS'
        ```

3.  **Analyze the Response**: You will receive a JSON response in the terminal containing, among other things, the `access_token` and `refresh_token`.

#### 3.3. Save the Refresh Token

1.  In the root directory of the project (`tradeMate/`), create a file named `allegro_token.json`.
2.  Paste the following content into it, replacing the value with **your new `refresh_token`** from the `curl` response:
    ```json
    {
        "refresh_token": "NEW_REFRESH_TOKEN_FROM_CURL_RESPONSE"
    }
    ```

### Step 4: Running the Application

Your application is now ready to start.

1.  Build the project to ensure everything is up to date:
    ```bash
    mvn clean install
    ```
2.  Run the main `main` method in the `TradeMateApplication` class.
3.  After the server starts, you can test the application by navigating to the following URL in your browser:
    `http://localhost:8080/orders`

The application will automatically use the saved `refresh_token` to fetch a current `access_token` and execute the API request.