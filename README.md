# Car Service

This project is a car rental management system built with Spring Boot, allowing users to register accounts, rent cars, and manage rentals through backend endpoints. Key features include:

- Register user accounts with specific roles (User, Admin).
- Browse available cars and rent them (up to 2 cars per user).
- View active and past rentals, and return rented cars.
- Admins can manage cars (add, update, delete) and view all rentals.

The application is backend-only, with no frontend. Use Swagger UI to test endpoints directly.

## Technologies

- **Java 21**
- **Maven**
- **Spring Boot 3.4.2**
  - Spring Web
  - Spring Data JPA (with Hibernate)
  - Spring Security (with JWT and Basic Auth)
  - Spring Validation
  - MySQL Driver
  - Springdoc OpenAPI (Swagger UI)
  - Liquibase
  - OAuth2 Resource Server
- **MySQL**
- **Docker**
- **Docker Compose**

## Requirements

You will need:

- Docker Desktop
- Basic knowledge of Docker and API testing
- Optional: Postman (for manual endpoint testing)
- Optional: IntelliJ IDEA (for development, not required for running)

## Installation

### Clone the repository:

```bash
git clone https://github.com/your-username/car_service.git
```

### Run the application with Docker Compose:

Run Docker Desktop

Navigate to the project directory and start the containers:

```bash
docker-compose up --build
```

This will:

- Build the Spring Boot application using the Dockerfile with Maven.
- Use the `wait-for-it.sh` script to ensure MySQL is ready before starting the app.
- Start a MySQL container with the `car_service` database.
- Start phpMyAdmin for database management.
- Deploy the backend application.

### Access Swagger UI:

Open [http://localhost:8080/swagger-ui-custom.html/](http://localhost:8080/swagger-ui-custom.html) to explore and test endpoints.

### Optional: Database Management:

- **phpMyAdmin**: [http://localhost:8081](http://localhost:8081) (username: `root`, password: `my-secret-pw`)

### Stop the application:

To stop containers:

```bash
docker-compose down
```

To stop and reset the database:

```bash
docker-compose down -v
```

## Database Initialization

The database is initialized automatically via Liquibase, creating tables for users, roles, cars, and rentals with test data:

**Users:**

- `user1` (password: `password123`, roles: `ROLE_USER`, `ROLE_ADMIN`)
- `admin` (password: `admin123`, role: `ROLE_ADMIN`)
- `basic` (password: `basic123`, role: `ROLE_USER`)

**Cars:**

- Toyota Camry (2020, RENTED, \$50/day)
- Honda Civic (2021, AVAILABLE, \$45/day)
- Ford Mustang (2022, AVAILABLE, \$75/day)

**Rentals:**

- User `basic` has rented the Toyota Camry starting 2025-03-10 (active).

Use these credentials in Swagger UI to test the API immediately.

## Usage

### Authentication

- Use **Basic Auth** in Swagger UI to obtain a JWT token via `/api/token`.
- Use the JWT token as a **Bearer Token** for authenticated endpoints.

### Endpoints (via Swagger UI)

**Public:**

- `/api/users` (POST) - Register a new user.\
  Example: `{ "username": "newuser", "password": "newpass123", "roles": [{ "id": 1 }] }`\
  For admin, use both roles: `[{ "id": 1 }, { "id": 2 }]`.

**Token:**

- `/api/token` (POST) - Get JWT with Basic Auth (e.g., `user1:password123`).

**User Role Endpoints** (require `ROLE_USER`):

- `/api/cars/available` (GET) - View available cars.
- `/api/rentals` (POST) - Rent a car.\
  Example: `{ "carId": 2, "rentalStart": "2025-03-20" }`.
- `/api/rentals/my` (GET) - View active rentals.
- `/api/rentals/my/history` (GET) - View rental history.
- `/api/rentals/return/{id}` (POST) - Return a car.

**Admin Role Endpoints** (require `ROLE_ADMIN`):

- `/api/users` (GET) - View all users.
- `/api/users/{id}` (PUT/DELETE) - Update or delete a user.
- `/api/cars` (GET/POST) - View or add cars.\
  Example: `{ "brand": "Tesla", "model": "Model 3", "year": 2023, "dailyRentPrice": 100.00 }`.
- `/api/cars/{id}` (GET/PUT/DELETE) - Manage a specific car.
- `/api/rentals/history` (GET) - View all rentals.

Explore all endpoints in Swagger UI at [http://localhost:8080/swagger-ui-custom.html](http://localhost:8080/swagger-ui-custom.html).

## Testing

Unit tests are included and require a running Docker container (via `docker-compose up`) for Liquibase to pass (rougtly done 50% of tests, still WIP, will be finished as soon as possible):

```bash
mvn test
```

Test endpoints manually using **Swagger UI** (preferred) or **Postman** (optional).

## Credits

This project was developed by **Andrej Titkov** using Java and Spring Boot.

Special thanks to:

- My teacher **Julius Zabulenas** for guidance and support.
- The creator of `wait-for-it.sh`, **vishnubob**, for the initialization script.

