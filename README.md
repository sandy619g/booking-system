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