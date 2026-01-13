# BookPass Backend

This is the Spring Boot backend service for the BookPass application. It provides APIs for user authentication, book management, and more.

## Prerequisites

- **Java 17+** (Java 25 verified working)
- **Maven** (Wrapper included)

## Setup for Local Development

### 1. Configure Secrets
The application requires connection to an Azure PostgreSQL database and other secrets.

1.  Copy the template file:
    ```powershell
    cp local-secrets.env.template local-secrets.env
    ```
2.  Edit `local-secrets.env` and fill in your values:
    - `DB_PASSWORD`: Password for the Azure database user.
    - `DB_USERNAME`: (Optional) Database username (defaults to `bookpass`).
    - `JWT_SECRET`: Secret key for token generation.
    - `MOYASAR_SECRET_KEY`: Payment gateway secret.

    > **Note**: `local-secrets.env` is git-ignored to protect your credentials.

### 2. Run the Application
Use the provided PowerShell script to load secrets and start the app:

```powershell
.\run-local.ps1
```

Alternatively, if you set environment variables manually:
```bash
./mvnw spring-boot:run
```

## API Documentation
Once the application is running, you can access the interactive API documentation (Swagger UI) at:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

## Deployment
For production deployment, use the `docker-compose.prod.yml` file which is configured for the production environment.
