# Deployment Guide

## What Changed

- `prod` profile uses MySQL instead of in-memory H2.
- Demo seed users are disabled in `prod`.
- Public self-registration is limited to `ROLE_CITIZEN`.
- Land write APIs and blockchain admin APIs are protected again in `prod`.
- Docker, Docker Compose, and Nginx configs are included.

## Important Limitation

The bundled frontend is still a simple dashboard and does not manage JWT login tokens yet.
In `prod`, protected API operations require authentication, so either:

- add login/token handling to the frontend, or
- use authenticated API clients for write/admin operations.

## Option 1: Docker Compose

1. Copy `.env.example` to `.env`.
2. Fill in strong values for:
   - `MYSQL_PASSWORD`
   - `MYSQL_ROOT_PASSWORD`
   - `APP_JWT_SECRET`
   - `APP_CORS_ALLOWED_ORIGINS`
3. Optionally set bootstrap admin credentials for the first deployment:
   - `APP_BOOTSTRAP_ADMIN_USERNAME`
   - `APP_BOOTSTRAP_ADMIN_PASSWORD`
4. Start the stack:

```bash
docker compose --env-file .env up -d --build
```

5. Verify:

```bash
docker compose ps
docker compose logs -f app
curl http://localhost/actuator/health
```

The site is proxied through Nginx on port `80`.

## Option 2: JAR On A VM

Build locally:

```bash
mvn -DskipTests package
```

Run with the production profile:

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/landregistry?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export SPRING_DATASOURCE_USERNAME='landregistry'
export SPRING_DATASOURCE_PASSWORD='replace-me'
export APP_JWT_SECRET='replace-with-a-long-random-secret'
export APP_CORS_ALLOWED_ORIGINS='https://your-domain.com'
export APP_BOOTSTRAP_ADMIN_USERNAME='admin'
export APP_BOOTSTRAP_ADMIN_PASSWORD='replace-me'
export APP_BOOTSTRAP_ADMIN_EMAIL='admin@your-domain.com'
java -jar target/land-registry-blockchain-1.0.0.jar
```

## Reverse Proxy

The sample Nginx config is at `deploy/nginx/default.conf`.
For a real public deployment, terminate TLS at Nginx, Caddy, or a cloud load balancer and point DNS at that host.

## First Production Checklist

- Set `SPRING_PROFILES_ACTIVE=prod`
- Use MySQL or another persistent database
- Set a strong `APP_JWT_SECRET`
- Restrict `APP_CORS_ALLOWED_ORIGINS` to your real domain
- Create a bootstrap admin only for the first deploy, then remove those env vars
- Put HTTPS in front of the app
- Back up the database
