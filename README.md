# 📱 PhoneHub Backend API

👉 [Click here to view the Frontend Repository](#)

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-Database-blue.svg)](https://www.mysql.com/)
[![Spring Security](https://img.shields.io/badge/Spring_Security-Static-green.svg)](https://spring.io/projects/spring-security)
[![Hibernate](https://img.shields.io/badge/Hibernate-ORM-lightgrey.svg)](https://hibernate.org/)

---

## 1. Project Overview

**PhoneHub** is a comprehensive, RESTful API backend designed to power a robust e-commerce platform specializing in mobile devices. Engineered with a focus on high scalability, security, and maintainability, this system serves as the core data and logic processing unit. 

Key architectural pillars include a comprehensive **Role-Based Access Control (RBAC)** implementation securing endpoints based on dynamic user roles, robust **scalable backend logic** capable of handling concurrent transactions, and seamless **payment integration** to support secure and reliable checkout experiences.

---

## 2. Tech Stack

The system is built upon a modern Java ecosystem and leverages industry-standard tools and APIs for optimal performance and integration.

| Technology/Tool | Purpose |
| :--- | :--- |
| **Java 21** | Core programming language leveraging modern features (e.g., virtual threads, record patterns). |
| **Spring Boot 4** | Framework for developing stand-alone, production-grade Spring applications rapidly. |
| **Spring Security (JWT)** | Authentication and authorization mechanism protecting API endpoints via JSON Web Tokens. |
| **MySQL** | Relational Database Management System handling mission-critical data. |
| **Hibernate / JPA** | ORM library used to map Java objects to database tables securely and efficiently. |
| **Cloudinary API** | Third-party service integration for remote, optimized media storage (e.g., product images). |
| **VNPAY API** | Secure regional payment gateway integration handling transactional data flows. |

---

## 3. System Architecture & ERD

The database architecture is designed with strict adherence to relational DB normalization to prevent data anomalies while ensuring performant read/write capabilities for the e-commerce workload. The schema distinctly separates core domains: Users/Roles (RBAC), Product Catalog (Categories, Products, Multi-variants), and Transactions (Orders, Payments).

![ERD Placeholder](link-to-image)

*Note: The ERD above delineates the relational logic, illustrating foreign key constraints between dynamic product attributes, order schemas, and the overarching security models.*

---

## 4. Key Features & System Design

### 🔐 Dynamic RBAC Flow
- Implements a stateless, token-based mechanism. The system parses JWTs to retrieve user authorities, cross-referencing them against an in-database hierarchy of roles and permissions. This design dynamically restricts or grants access to distinct administrative boundaries (e.g., product management vs. user settings).

### 💳 VNPAY Checkout Data Flow
- Ensures ACID compliance during the checkout phase. Upon order initialization, the backend generates an encrypted VNPAY signature and redirect URL. Post-payment, it securely processes the async IPN (Instant Payment Notification) callback, updating system inventory and order status transactionally to avoid race conditions.

### 📦 Multi-Variant Product Catalog
- The database models products dynamically, allowing products to possess polymorphic attributes (e.g., Storage, Color, RAM variations). This is modeled utilizing parent-child entity relationships, enabling highly optimized, filtered queries across thousands of possible product variants without schema bloat.

---

## 5. API Documentation

Comprehensive API documentation, including request payloads, parameters, and example responses, is managed via Postman.

> **Instruction:** Import the JSON collection from the link below into your Postman workspace to interact with the endpoints.

[![Postman](https://img.shields.io/badge/Postman-Collection-FF6C37?style=for-the-badge&logo=postman)](link-to-postman-collection)

*(Insert Postman collection public link above)*

---

## 6. Getting Started

Follow the instructions below to configure, build, and execute the backend environment on your local machine.

### Prerequisites
Ensure the following tools are installed in your environment:
- **Java Development Kit (JDK) 21** or higher.
- **MySQL Server** (ensure the database daemon is actively running).
- **Maven** (if you opt not to use the provided Maven Wrapper).

### Environment Variables Setup
The system relies on various configurations to connect to standard resources securely.
Create a local `application-dev.properties` (or modify `application.properties`) and configure the following parameters. 

**⚠️ SECURITY WARNING:** Never commit real credentials or API secrets to version control. Keep your `.properties` file securely local or managed via environment secrets.

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/phonehub_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update

# JWT Secret
app.jwt.secret=YOUR_SUPER_SECRET_JWT_KEY_HERE
app.jwt.expiration-ms=86400000

# Cloudinary Integration
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET

# VNPAY Configuration
vnpay.tmnCode=YOUR_VNPAY_TMN_CODE
vnpay.hashSecret=YOUR_VNPAY_HASH_SECRET
vnpay.url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnpay.returnUrl=http://localhost:8080/api/payment/vnpay-return
```

### Installation & Run

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd demo
   ```

2. **Build the application:**
   Using the Maven Wrapper to download dependencies and compile the project (skip tests for a faster initial build):
   ```bash
   ./mvnw clean install -DskipTests
   ```
   *(For Windows Command Prompt, use `mvnw.cmd clean install -DskipTests`)*

3. **Run the server:**
   Start the Spring Boot application configuration directly:
   ```bash
   ./mvnw spring-boot:run
   ```

The server should successfully bind to `http://localhost:8080` upon a successful build context execution.
