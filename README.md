# CommerceHub- Ecommerce Springboot Backend

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.md)
[![Language: Java](https://img.shields.io/badge/Language-Java-blue.svg)]
[![Framework: Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-green.svg)]
[![Database: MySQL](https://img.shields.io/badge/Database-MySQL-orange.svg)]
[![Cache: Redis](https://img.shields.io/badge/Cache-Redis-red.svg)]
[![Messaging: Kafka](https://img.shields.io/badge/Messaging-Kafka-lightgrey.svg)]

## Project Overview

This project is a comprehensive **Ecommerce Spring Boot Backend** designed to manage a multi-user digital commerce platform. The application implements complex business logic across three distinct user roles: **ADMIN**, **SELLER**, and **CUSTOMER**. It features robust, token-based authentication (JWT), secure payment gateway integrations (Stripe and Razorpay), inventory and listing management, and a unified, pluggable notification system.

The core problem solved is providing a scalable and secure backend foundation for a full-featured e-commerce platform, separating user permissions and handling critical functionalities like inventory reservation (cart logic) and payment processing.

## Key Features

The application incorporates the following core functionalities:
*   **Role-Based Access Control (RBAC):** Users are assigned one of three roles (ADMIN, CUSTOMER, SELLER), with Spring Security ensuring method-level (`@PreAuthorize`) and endpoint-level authorization.
*   **JWT Authentication:** Secure sign-up for sellers and customers, login, and generation of stateless JWT tokens for subsequent authorized access.
*   **Seller Product Management:** Functionality for sellers to create product records (defining product attributes like name, brand, category) and create detailed product listings (defining quantity and price for inventory).
*   **Customer Cart and Inventory Flow:** Logic for customers to add items to their cart, which triggers the reservation of an individual `InventoryItem` instance from an `AVAILABLE` status. Includes removal from cart logic.
*   **Payment Gateway Integration:** Supports generation of payment links via two different providers: **Stripe** and **Razorpay**. Includes a dedicated endpoint for handling payment webhooks.
*   **Notification System:** A unified `UnifiedNotificationDispatcher` that conditionally routes email requests either to Kafka (event-driven asynchronous flow) or a direct synchronous dispatcher (SendGrid or SMTP). Successful sign-ups trigger an email notification.
*   **Redis Caching:** Integration of Redis in the `FakeStoreProductService` to cache product details fetched from external APIs, improving performance and reducing reliance on external calls.
*   **Admin Tools:** APIs for administrators to fetch comprehensive details on sellers, customers, and system inventory/customerOrder details.
*   **Data Paging/Filtering:** Implementation of Spring Data JPA `Pageable` functionality for returning paginated results in several endpoints (e.g., viewing customer/seller products, admin details).

## Tech Stack

The architecture relies heavily on the Java ecosystem, leveraging Spring Boot for rapid development and enterprise-grade features.

| Layer | Technology | Role / Specific Use |
| :--- | :--- | :--- |
| **Backend Framework** | Java, Spring Boot, Spring Security | Core application logic, REST API controllers. JWT configuration and filtering. |
| **Authentication** | JWT (Json Web Tokens), BCrypt | Stateless authorization and secure password hashing. |
| **Database** | MySQL, Spring Data JPA, Hibernate, Flyway | Primary data persistence layer. Flyway handles database schema migrations. |
| **Caching** | Redis (Spring Data Redis) | Used by `FakeStoreProductService` for caching external API responses. |
| **Payment Gateways** | Stripe, Razorpay | Payment link generation (`StripePaymentGatewayImpl`, `RazorpayGateway`). |
| **Messaging/Email** | Apache Kafka (Optional), SendGrid, SMTP | Asynchronous notification queueing or direct email dispatch. |
| **Utilities** | Lombok, RestTemplate | Boilerplate reduction for DTOs and models, external API consumption (FakeStore). |


## Architecture

The architecture follows a modular, layer-based approach characteristic of modern Spring Boot applications, with distinct packages for Authentication (`auth`), Core E-commerce logic (`ecommerce`), and external communications (`notifications`).

### 1. Structure (Packages)

*   `com.example.ecommerce_springboot`: Root package.
*   `auth`: Handles user management, security, JWT logic, and authentication controllers (`AuthController`, `UserController`).
*   `ecommerce`: Contains all domain models (e.g., `Product`, `Cart`, `InventoryItem`), business logic services (split by role: `SellerProductService`, `CustomerProductService`, `AdminProductService`), controllers, and repositories.
*   `notifications`: Contains components for sending emails, including dispatchers (`UnifiedNotificationDispatcher`), email service implementations (SendGrid, Smtp), and Kafka consumer/producer logic.

### 2. Security Flow

Security is configured for stateless sessions (`SessionCreationPolicy.STATELESS`).
1.  **Request Entry:** An incoming request checks for an `Authorization: Bearer <token>` header in `JwtFilter`.
2.  **Token Validation:** The token is validated using `JwtUtil`.
3.  **Context Setting:** If valid, an `UsernamePasswordAuthenticationToken` is created, including the user's role (prefixed with `ROLE_` as required by Spring Security). This token is placed in the `SecurityContextHolder`.
4.  **Authorization:** Controllers use `@PreAuthorize("hasRole('ROLE')")` or define URL patterns in `SecurityConfig` to restrict access based on the authenticated user's role.

### 3. Inventory and Cart Logic

The cart system interacts directly with the detailed inventory:
1.  When a customer calls `addToCart`, the system verifies the existence of the product listing.
2.  It searches the `InventoryItemRepository` for an `InventoryItem` linked to that listing that has the status `AVAILABLE`.
3.  The specific inventory item found is then linked to the new `CartItem`, effectively reserving that physical unit for the customer.
4.  The cart status is updated (from `EMPTY` to `FILLED`) and the total is calculated.


## Prerequisites

To run this application locally, you need the following installed:

*   **Java Development Kit (JDK) 17+** (Required for Spring Boot 3)
*   **A build tool** (e.g., Maven or Gradle, generally included in IDEs)
*   **MySQL Database** instance running locally or accessible via URL.
*   **Optional:** A running **Redis instance** for caching functionality.
*   **Optional:** A running **Apache Kafka instance** if `use.kafka` is set to `true`.

## Installation & Setup

### 1. Clone the repository

```bash
git clone "https://github.com/YashR-github/ecommerce-springboot-backend.git"
cd Ecommerce_Springboot_Backend
```


### 2. Configure Environment Variables

The application relies on several environment variables defined in `application.properties`. You must set these variables, preferably via a `.env` file or command line arguments, before running.

| Variable | Description | Source/Usage |
| :--- | :--- | :--- |
| `DATASOURCE_USERNAME` | MySQL database username | Used for connecting to `EcommerceDb`. |
| `DATASOURCE_PASS` | MySQL database password | Used for connecting to `EcommerceDb`. |
| `JWT_SECRET` | Secret key for JWT signing (must be BASE64 decoded format) | Used by `JwtUtil` for token security. |
| `STRIPE_API_KEY` | Secret key for Stripe integration | Used by `StripePaymentGatewayImpl`. |
| `RAZORPAY_KEY_ID` | Key ID for Razorpay integration | Used by `RazorpayClientConfig`. |
| `RAZORPAY_KEY_SECRET` | Key Secret for Razorpay integration | Used by `RazorpayClientConfig`. |
| `SENDGRID_API_KEY` | API Key for SendGrid email delivery | Used by `SendGridEmailService`. |

**Note on Database Configuration:**
The application uses Flyway for migrations and is configured to connect to a specific remote MySQL endpoint: `jdbc:mysql://aws-db.cbc86ayumlgd.eu-north-1.rds.amazonaws.com:3306/EcommerceDb`. **You must replace this with your own local or remote database URL if you intend to run locally.** The `spring.jpa.hibernate.ddl-auto` is set to `create` for initial setup.

### 3. Build and Run

Use your preferred build tool (e.g., Maven or Gradle) or run directly from your IDE.

If using Maven (typical Spring Boot build):
```bash
# Compile and package the application
./mvnw clean package

# Run the packaged JAR file
java -jar target/Ecommerce_Springboot_Backend.jar
```


## Usage Guide

The application starts as a Spring Boot application named `Ecommerce_Springboot_Backend`.

**Initial Access:** Since Flyway is enabled and `ddl-auto` is set to `create`, the database schema will be initialized on startup. You must register users via the exposed signup APIs before accessing protected endpoints.

**Interacting with APIs:**

All authenticated requests must include the JWT token obtained from the `/auth/login` endpoint in the `Authorization` header as a `Bearer` token.

### Starting the Server

The main class running the application is `Ecommerce_Springboot_Backend.java`.

```java
public static void main(String[] args) {
    SpringApplication.run(Ecommerce_Springboot_Backend.class, args);
    // ... custom initialization
}
```

## API Documentation

The following are the key, role-protected API endpoints:

### Authentication & Authorization (Base Path: `/auth`)

| Method | Endpoint | Description | Roles |
| :--- | :--- | :--- | :--- |
| `POST` | `/seller/signup` | Register a new seller account. | Public |
| `POST` | `/customer/signup` | Register a new customer account. | Public |
| `POST` | `/login` | Log in, receive JWT token and user details in response headers/body. | Public |

### Seller Operations (Base Path: `/products/sellers`)

Access requires `SELLER` role.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/product` | Create a new core product record. |
| `POST` | `/product-listing` | Create a new product listing (inventory declaration) linking to a product record. |
| `PATCH` | `/products/{id}` | Update an existing product record. |
| `DELETE` | `/products/{id}` | Delete a product record. |
| `GET` | `/products` | Get paginated and filtered list of products created by the seller. |
| `GET` | `/product-listings` | Get paginated and filtered list of product listings created by the seller. |

### Customer Operations (Base Path: `/products/customers`)

Access requires `CUSTOMER` role.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/products` | Get paginated, search-filtered product listings. |
| `GET` | `/product-details/{productListingId}` | Get details for a single product listing. |
| `GET` | `/add-to-cart/{productListingId}` | Add an available inventory item to the customer's cart. |
| `GET` | `/remove-from-cart/{cartItemId}` | Remove a specific item from the cart. |
| `POST` | `/payments` | Generate a payment link (Stripe or Razorpay implementation) for an customerOrder. |
| `GET` | `/cart` | Get paginated details of the customer's current cart. |

### Admin Operations

Access requires `ADMIN` role.

| Method | Endpoint | Description | Base Path |
| :--- | :--- | :--- | :--- |
| `GET` | `/seller-details` | Get paginated details of all sellers. | `/user-management` |
| `GET` | `/customer-details` | Get paginated details of all customers. | `/user-management` |
| `GET` | `/inventory-details` | Get filtered, paginated inventory details. | `/products/admin` |
| `GET` | `/customerOrder-id-details` | Get paginated customerOrder details by ID. | `/products/admin` |
| `DELETE` | `/{id}` | Delete a user (requires ADMIN role or self-deletion). | `/user-management` |

