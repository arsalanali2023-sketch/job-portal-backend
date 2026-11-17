# Job Portal Backend REST API

A secure, production-style RESTful backend for a Job Portal built with **Java 21**, **Spring Boot**, **Spring Security (JWT)**, **Spring Data JPA**, and **MySQL**. It supports role-based workflows for **Recruiters** and **Job Seekers**.

## Tech Stack
* **Language:** Java 21
* **Framework:** Spring Boot, Spring Web, Spring Data JPA
* **Security:** Spring Security, Stateless JWT Authentication, BCrypt Password Hashing
* **Database:** MySQL
* **Utilities:** Lombok, Jakarta Bean Validation, Maven

## Key Features
* **Authentication & RBAC:** User registration and login with JWT token generation and role-based access control (`RECRUITER` & `JOB_SEEKER`).
* **Job Management (Recruiter):** Create, update, view posted jobs, and soft-close job listings.
* **Job Discovery (Public):** Browse active jobs and search/filter by keyword (title/skills) and location.
* **Application Workflow:** Job seekers can apply with a resume link and cover letter (duplicate applications prevented) and track live status updates (`APPLIED`, `SHORTLISTED`, `REJECTED`, `HIRED`).
* **Global Exception Handling:** Clean, standardized JSON error responses for validation errors, unauthorized access, and missing resources.

## API Endpoints

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Register a new `RECRUITER` or `JOB_SEEKER` |
| `POST` | `/api/auth/login` | Public | Authenticate user and receive JWT token |
| `GET` | `/api/jobs` | Public | Get all active job listings |
| `GET` | `/api/jobs/{id}` | Public | Get details of a specific job |
| `GET` | `/api/jobs/search?keyword=&location=` | Public | Search active jobs by keyword and location |
| `POST` | `/api/jobs` | `RECRUITER` | Post a new job |
| `GET` | `/api/jobs/my-jobs` | `RECRUITER` | View all jobs posted by logged-in recruiter |
| `PUT` | `/api/jobs/{id}` | `RECRUITER` | Update own job posting |
| `DELETE` | `/api/jobs/{id}` | `RECRUITER` | Close/deactivate own job posting |
| `POST` | `/api/applications/apply/{jobId}` | `JOB_SEEKER` | Apply to an active job |
| `GET` | `/api/applications/my-applications` | `JOB_SEEKER` | Track own job applications and status |
| `GET` | `/api/applications/job/{jobId}` | `RECRUITER` | View all applicants for a specific job |
| `PATCH` | `/api/applications/{applicationId}/status` | `RECRUITER` | Update candidate status (`SHORTLISTED`, `REJECTED`, `HIRED`) |

## Local Setup

1. Create a MySQL database named `job_portal_db`.
2. Update `src/main/resources/application.properties` with your MySQL credentials and Base64 JWT secret:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/job_portal_db
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   jwt.secret=your_base64_encoded_secret_key
   jwt.expiration=86400000
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```