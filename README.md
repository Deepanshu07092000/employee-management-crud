# Employee Management CRUD REST API

A production-oriented Employee Management REST API built using Spring Boot, Spring Data JPA, Hibernate, MySQL, Bean Validation, Lombok, and JUnit/Mockito.

The application provides complete employee lifecycle management through RESTful APIs, including employee creation, retrieval, update, partial update, deletion, validation, exception handling, pagination, sorting, duplicate-email protection, logging, and automated testing.

---

## 📌 Project Overview

The Employee Management System is a backend REST API designed to manage employee information in a structured and scalable manner.

The application follows a layered architecture that separates responsibilities across:

- Controller
- Service
- Repository
- Entity
- DTO
- Mapper
- Exception Handling

This separation makes the application easier to maintain, test, extend, and deploy.

---

## 🚀 Key Features

### Employee Management

- Create a new employee
- Retrieve employee by ID
- Retrieve all employees
- Update an employee completely using PUT
- Partially update an employee using PATCH
- Delete an employee

### API Features

- RESTful API design
- Proper HTTP status codes
- Request validation
- Global exception handling
- Standardized error responses
- Duplicate email validation
- Database-level unique email constraint
- Pagination
- Sorting
- DTO-based request/response design
- Entity-to-DTO mapping
- Application logging

### Testing

- Service layer unit tests
- Controller layer tests using MockMvc
- Repository tests using `@DataJpaTest`
- Integration tests
- H2 in-memory database for tests
- Mockito-based service mocking

---

# 🛠️ Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Programming Language |
| Spring Boot 4.1.1 | Application Framework |
| Spring Web MVC | REST API development |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL | Production database |
| H2 | Test database |
| Jakarta Validation | Request validation |
| Lombok | Boilerplate code reduction |
| JUnit 5 | Unit testing |
| Mockito | Mocking dependencies |
| MockMvc | Controller/API testing |
| Maven | Dependency management and build |
| Git | Version control |
| GitHub | Source code repository |
| STS | Development IDE |

---

# 🏗️ Project Architecture

The application follows a layered architecture:

```text
                        Client
                          |
                          v
                +-------------------+
                | EmployeeController|
                +-------------------+
                          |
                          v
                +-------------------+
                |  EmployeeService  |
                +-------------------+
                          |
                          v
                +-------------------+
                | EmployeeRepository|
                +-------------------+
                          |
                          v
                    +---------+
                    |  MySQL  |
                    +---------+
