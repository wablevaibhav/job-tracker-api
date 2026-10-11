# Job Application Tracker API

REST API to track job applications through the hiring pipeline (applied, screening, interview, offer, rejected).
Built with Java 21 and Spring Boot 3.

**Stack:** Spring Boot, Spring Web, Spring Data JPA (Hibernate), PostgreSQL, Spring Security with JWT,
Bean Validation, springdoc OpenAPI, JUnit 5 + MockMvc, Docker / Docker Compose.

## Features

- Register and log in; stateless JWT authentication (BCrypt password hashing)
- CRUD for job applications, scoped to the authenticated user (users cannot see each other's data)
- Pagination (`page`, `size`, newest-updated first) and filtering by status
- Status updates and a per-status stats endpoint
- Validation errors and not-found/conflict cases returned as RFC 7807 `ProblemDetail` responses
- OpenAPI docs at `/swagger-ui.html`, health check at `/actuator/health`

## Run with Docker Compose

```bash
docker compose up --build
```

API on http://localhost:8080, Swagger UI on http://localhost:8080/swagger-ui.html.

## Run locally

Requires Java 21, Maven and a PostgreSQL database (defaults: `jobtracker` / `jobtracker` / `jobtracker` on localhost:5432).
Override with `DB_URL`, `DB_USER`, `DB_PASSWORD`, and set `JWT_SECRET` (32+ characters) outside local development.

```bash
mvn spring-boot:run
```

## Run tests

Tests use an in-memory H2 database (profile `test`), so no database is needed.

```bash
mvn test
```

## Endpoints

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Create account, returns JWT |
| POST | `/api/auth/login` | Log in, returns JWT |
| POST | `/api/applications` | Create an application |
| GET | `/api/applications?status=&page=&size=` | List (paged, optional status filter) |
| GET | `/api/applications/{id}` | Get one |
| PUT | `/api/applications/{id}` | Update |
| PATCH | `/api/applications/{id}/status` | Change status |
| DELETE | `/api/applications/{id}` | Delete |
| GET | `/api/applications/stats` | Counts by status |

Send the token as `Authorization: Bearer <token>`.

## Example

```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"me@example.com","password":"password123"}' | jq -r .token)

curl -s -X POST localhost:8080/api/applications \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"company":"Acme","role":"Java Developer","location":"Pune"}'
```

## Possible next steps

Flyway migrations, refresh tokens, Testcontainers-based tests against real PostgreSQL, Kafka events on status changes.
