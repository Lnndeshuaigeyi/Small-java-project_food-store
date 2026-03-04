# Online Fresh Food Store (Java + PostgreSQL)

A small self-designed Java project for an online fresh food store system.  
It supports registering customers, adding products, and placing orders with PostgreSQL persistence.

## Features
- Register customer (insert into `ofs_customers`)
- Add product (insert into `ofs_products`)
- Place order (transaction):  
  - Decrease product stock
  - Insert a new row into `ofs_orders`

## Tech Stack
- Java (IntelliJ IDEA)
- Maven
- PostgreSQL + pgAdmin 4
- JDBC (PostgreSQL driver)

## Database Setup
Create a database (e.g., `food_store`) and create tables:
- `ofs_customers`
- `ofs_categories`
- `ofs_products`
- `ofs_deliveries`
- `ofs_orders`

> Note: `ofs_products.categoryid` is a foreign key to `ofs_categories.id`.

## Configure DB Connection (Important)
This repo does **NOT** include real credentials.

1. Copy the example config:
   - `src/main/resources/db.properties_example`
2. Create a new file:
   - `src/main/resources/db.properties`
3. Fill in your local PostgreSQL settings, for example:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/food_store
   db.user=postgres
   db.password=YOUR_PASSWORD
