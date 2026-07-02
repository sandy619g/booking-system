# Booking System

---

## Features

- User management
- Slot management (FREE/BUSY)
- Meeting scheduling
- Participant coordination
- Calendar view (aggregated availability)

---

## Tech Stack

- Java 21
- Spring Boot 3
- PostgreSQL
- JPA / Hibernate
- Docker + Docker Compose
- JUnit + Mockito

---

## Clone Repository

```bash
   git clone https://github.com/sandy619g/booking-system.git
   cd booking-system
```

### Run Application

```bash
docker compose up -d postgres

mvn spring-boot:run
```

Application runs at:

http://localhost:8080

### Run application on docker

```bash
mvn clean package
docker compose up --build
```

### Run test

```bash
mvn test
```

# API Documentation

## Create Users
```http
POST /users
{
  "name": "John",
  "email": "john@test.com"
}
```
## Create Slot
```http
POST /users/{userId}/slots
{
"startTime": "2026-07-03T09:00:00",
"endTime": "2026-07-03T10:00:00"
}
```
## Fetch Slot
Get all slots
```http
GET /users/{userId}/slots
```

## Fetch Slot by Status
```http
GET /users/{userId}/slots?status=FREE
```

## Create Meeting
```http
POST /meetings
{
"title": "Sprint Planning",
"description": "Planning session",
"organizerSlotId": 1,
"participantIds": [2, 3]
}
```

## View Calendar
```http
GET /users/{userId}/calendar?from=2026-07-03T08:00:00&to=2026-07-03T12:00:00
```

## Cancel Meeting
```http
DELETE /meetings/{meetingId}
```