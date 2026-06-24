# Blockchain-Based Secure Land Registry System

A secure and scalable land record management platform developed using Spring Boot and a custom blockchain-inspired transaction ledger. The system enables transparent property registration, ownership transfers, dispute management, and land record verification while maintaining data integrity through immutable transaction tracking.

## Overview

The Blockchain-Based Secure Land Registry System is designed to modernize traditional land record management by providing a secure digital platform for property registration and ownership management. The application combines the reliability of relational databases with a blockchain-style transaction log to ensure transparency, traceability, and tamper resistance of land records.

The platform supports role-based access control, secure authentication, transaction history tracking, and blockchain integrity validation, making it suitable for educational, research, and prototype government digitization initiatives.

---

## Technology Stack

### Backend

* Java 17
* Spring Boot 3
* Spring Security
* Spring Data JPA
* Hibernate ORM
* RESTful APIs

### Database

* H2 Database (Development Environment)
* MySQL (Production Environment)

### Frontend

* HTML5
* CSS3
* JavaScript

### DevOps & Deployment

* Docker
* Docker Compose
* Nginx
* Render Cloud Platform
* Maven

---

## Key Features

### Property Management

* Register new land parcels
* Update land information and ownership records
* Manage property metadata and ownership details

### Ownership Transfer

* Secure ownership transfer workflow
* Complete transaction history tracking
* Ownership verification and audit trail

### Encumbrance Management

* Record liens, mortgages, and legal restrictions
* Maintain historical encumbrance records
* Track active and resolved encumbrances

### Dispute Handling

* File property disputes
* Monitor dispute status and resolution history
* Maintain dispute-related documentation records

### Blockchain-Based Audit Trail

* Immutable transaction logging
* Block generation for critical property transactions
* Blockchain integrity verification
* Historical transaction validation

### Security & Access Control

* JWT-based authentication
* Role-Based Access Control (RBAC)
* Secure API endpoints
* Protected administrative operations

### Dashboard & Reporting

* Interactive web dashboard
* Parcel search and record lookup
* Ownership history visualization
* Blockchain status monitoring

---

## System Architecture

```
Client (Web Dashboard)
│
▼
REST API Layer (Controllers)
│
▼
Business Logic Layer (Services)
│
┌────────┴────────┐
▼                 ▼
Blockchain Engine   JPA/Hibernate
▼                 ▼
Transaction Log    MySQL / H2 Database
```

---

## Project Structure

```
src/main/java/com/landregistry
│
├── blockchain/      Blockchain implementation and validation
├── config/          Security and application configuration
├── controller/      REST API endpoints
├── dto/             Request and response models
├── entity/          JPA entity classes
├── repository/      Data access layer
├── service/         Business logic layer
└── util/            Utility classes

src/main/resources
│
├── application.properties
├── application-prod.properties
└── static/
└── index.html

deploy/
├── nginx/
└── render/
```

---

## Core Functionalities

### Land Parcel Registration

Creates and stores new property records while generating corresponding blockchain transaction entries.

### Ownership Transfer

Transfers property ownership between registered users and records each transfer in the blockchain ledger.

### Record Modification

Allows authorized personnel to update land information while preserving historical transaction records.

### Blockchain Validation

Verifies the integrity of the blockchain by checking block hashes, previous hash references, and transaction consistency.

### Historical Audit Trail

Provides complete visibility into all transactions associated with a specific property.

---

## Development & Deployment

### Local Development

* Java 17
* Maven 3.9+
* H2 In-Memory Database

Run the application:

```bash
mvn spring-boot:run
```

Application URL:

```
http://localhost:8080
```

### Production Deployment

The application supports deployment using:

* Docker Containers
* Docker Compose
* Nginx Reverse Proxy
* Render Cloud Platform

Environment-specific configurations are managed through externalized environment variables and Spring Profiles.

---

## Security Features

* JWT Authentication
* Password Encryption using BCrypt
* Role-Based Authorization
* Secure REST API Access
* Environment-Based Secret Management
* CORS Configuration Support

---

## Learning Outcomes

This project demonstrates practical implementation of:

* Spring Boot Application Development
* REST API Design
* Spring Security & JWT Authentication
* Database Design with JPA/Hibernate
* Blockchain Fundamentals
* Docker Containerization
* Cloud Deployment Strategies
* Secure Enterprise Application Architecture

---

## Future Enhancements

* Smart Contract Integration
* Digital Signature Verification
* GIS/Map-Based Property Visualization
* Multi-Node Distributed Blockchain
* Government Registry Integration
* Advanced Analytics Dashboard
* Document Upload & Verification System

---

## Conclusion

The Blockchain-Based Secure Land Registry System provides a secure, transparent, and efficient solution for managing land ownership records. By combining modern web technologies with blockchain-inspired transaction tracking, the platform ensures data integrity, accountability, and auditability while delivering a user-friendly property management experience.
