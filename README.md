# MSS
Mechanical Shop System

Backend application for MSS, an auto repair management system developed using Java and Spring Boot.

## Technologies

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* PostgreSQL
* Flyway
* Maven

## Features

* Customer and vehicle management
* Service request management
* Customer reports and vehicle damage photos
* User authentication and authorization
* Google OAuth 2.0
* Role-based access control
* Email verification

## Getting Started

### Prerequisites

* Java 17 or newer
* PostgreSQL
* Maven

### Installation

Clone the repository and navigate to the project directory:

```bash
git clone https://github.com/DraganJovanovic96/MSS.git
cd MSS-Backend
```

Create a PostgreSQL database named `mss_db`.

Configure the database connection and required environment variables in `application.yml` or `application.properties`.

Start the application:

```bash
./mvnw spring-boot:run
```

The backend runs on `http://localhost:8080`.

## Configuration

The application requires configuration for PostgreSQL, Google OAuth, email services,  and Supabase Storage.

## Author

Dragan Jovanović
