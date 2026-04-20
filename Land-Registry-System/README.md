# Land Registry System

Spring Boot application for managing land records with a custom blockchain-style transaction log, a static dashboard frontend, and relational persistence for query access.

## Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security
- H2 for local development
- MySQL for production deployment
- Static HTML/CSS/JavaScript frontend bundled in the backend
- Docker, Docker Compose, Nginx, and Render deployment files

## Main Features

- Register new land parcels
- Transfer ownership
- Mutate land records
- Record encumbrances
- File disputes
- View parcel lists, history, and blockchain state
- Validate blockchain integrity

## Project Structure

```text
src/main/java/com/landregistry
  blockchain/   Blockchain classes
  config/       Security and app configuration
  controller/   REST controllers
  dto/          Request and response DTOs
  entity/       JPA entities
  repository/   Database repositories
  service/      Business logic
  util/         Helpers

src/main/resources
  application.properties
  application-prod.properties
  static/index.html

deploy/
  nginx/
  render/
```

## Local Development

### Prerequisites

- Java 17
- Maven 3.9+

### Run Locally

```bash
mvn spring-boot:run
```

The app starts on:

- `http://localhost:8080`

Local development uses:

- in-memory H2 database
- demo data seeding
- default seeded users

## Default Local Users

These are seeded only when local demo seeding is enabled.

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin@1234` | `ROLE_ADMIN` |
| `registrar` | `Registrar@1234` | `ROLE_REGISTRAR` |
| `officer` | `Officer@1234` | `ROLE_OFFICER` |

## Build

```bash
mvn clean package
```

The packaged JAR is created under `target/`.

## Test

```bash
mvn test
```

## Production Configuration

Production settings are defined in [src/main/resources/application-prod.properties](src/main/resources/application-prod.properties).

Important environment variables:

- `PORT` or `SERVER_PORT`
- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`
- `APP_CORS_ALLOWED_ORIGINS`

Optional bootstrap admin values:

- `APP_BOOTSTRAP_ADMIN_USERNAME`
- `APP_BOOTSTRAP_ADMIN_PASSWORD`
- `APP_BOOTSTRAP_ADMIN_FULL_NAME`
- `APP_BOOTSTRAP_ADMIN_EMAIL`

## Docker Deployment

Build and run with Docker Compose:

```bash
docker compose --env-file .env up -d --build
```

Related files:

- [Dockerfile](Dockerfile)
- [docker-compose.yml](docker-compose.yml)
- [.env.example](.env.example)
- [DEPLOYMENT.md](DEPLOYMENT.md)

## Render Deployment

This repo includes a Render blueprint:

- [render.yaml](render.yaml)

It defines:

- a web service for the Spring Boot app
- a private MySQL service with persistent disk

To deploy on Render:

1. Push the repo to GitHub, GitLab, or Bitbucket.
2. In Render, create a new Blueprint from the repo.
3. Set the secret environment variables requested by `render.yaml`.
4. Deploy.

## Frontend

The frontend is served from:

- [src/main/resources/static/index.html](src/main/resources/static/index.html)

It is a single static dashboard page that interacts with the backend REST API.

## Notes

- The blockchain implementation in this project is a custom educational/prototype design, not a distributed public blockchain.
- The database is used for application persistence and query access.
- Some documentation files in the repo may still describe older auth or deployment behavior; use the current code and config files as the source of truth.
