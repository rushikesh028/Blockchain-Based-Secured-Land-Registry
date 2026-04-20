# Blackbook Screenshot Checklist

Use these screenshots to show that the project is a working prototype, not a blank UI.

## Project Scope Note

This application is a final-year college prototype / proof of concept. It demonstrates how blockchain can provide tamper-evident land transaction history for a land registry system. It is not a government-ready production system and does not use a distributed public blockchain network.

## System Architecture Screenshot

Capture the architecture diagram from `README.md`.

Caption:
System architecture of the Blockchain-Based Land Registry System showing Spring Boot REST APIs, JWT role-based security, service layer, in-memory blockchain, and database query cache.

## Website Screenshots

1. Dashboard after startup
   - URL: `http://localhost:8080`
   - Show total parcels, transaction count, chain status, and recent records.
   - Caption: Dashboard showing demo land parcels loaded from backend seed data.

2. All registered parcels
   - URL: `http://localhost:8080`
   - Open the land records/parcels view.
   - Caption: Registered land parcels with owner, district, land type, value, and status.

3. Blockchain/admin view
   - Sign in as `admin` / `Admin@1234` if required.
   - Open the blockchain view.
   - Caption: Blockchain ledger showing blocks created for registration, transfer, mutation, encumbrance, and dispute transactions.

4. Register land form
   - Sign in as `registrar` / `Registrar@1234` if required.
   - Open the registration form.
   - Caption: Registrar land registration form secured by role-based access.

## API Testing Screenshots

Use Postman, Thunder Client, or browser for GET endpoints.

1. Login API
   - Method: `POST`
   - URL: `http://localhost:8080/api/auth/login`
   - Body:
```json
{
  "username": "admin",
  "password": "Admin@1234"
}
```
   - Caption: JWT login response for authenticated system access.

2. Get all land parcels
   - Method: `GET`
   - URL: `http://localhost:8080/api/land`
   - Caption: API response listing seeded demo land records.

3. Get blockchain information
   - Method: `GET`
   - URL: `http://localhost:8080/api/land/blockchain/info`
   - Caption: Blockchain metadata showing chain length, latest block hash, difficulty, and total transactions.

4. Validate blockchain
   - Method: `GET`
   - URL: `http://localhost:8080/api/land/blockchain/validate`
   - Caption: Blockchain validation API confirming whether the chain is valid.

5. Get parcel transaction history
   - Method: `GET`
   - URL: `http://localhost:8080/api/land/{parcelId}/history`
   - Replace `{parcelId}` with a parcel ID from the dashboard.
   - Caption: Transaction history for one parcel, proving traceability of land record changes.
