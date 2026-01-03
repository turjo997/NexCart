# NexCart

A lightweight **e-commerce demo application** built with Spring Boot, Thymeleaf, and PostgreSQL.  
It provides full CRUD operations for users and products, along with a complete per-user shopping cart experience.

## Docker Setup

This project includes a production-ready Docker setup using
multi-stage builds and Docker Compose.

📄 Full Docker documentation:
[README-Docker.md](README-Docker.md)


## Features

- **User Management**
  - List, create, edit, delete users
  - Safe deletion: prevents removing a user who has items in their cart
- **Product Management**
  - List, create, edit, delete products
  - Stock tracking and active/inactive status
- **Shopping Cart (Per User)**
  - Add products via AJAX with real-time mini-cart update
  - View, update quantity, remove items, clear cart
  - Proper stock validation (prevents overselling)
  - Friendly success/error messages on all actions
- **User Experience**
  - Clean, responsive Thymeleaf templates
  - Flash messages for feedback
  - Confirmation dialogs for destructive actions

## Tech Stack

- **Framework**: Spring Boot 3.x
- **Template Engine**: Thymeleaf
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA (Hibernate)
- **Build**: Maven
- **Key Dependencies**:
  - spring-boot-starter-web
  - spring-boot-starter-data-jpa
  - spring-boot-starter-thymeleaf
  - postgresql driver
  - Lombok

## Available Endpoints

### User Management
| Method | URL                        | Description                                      |
|--------|----------------------------|--------------------------------------------------|
| GET    | `/users`                   | List all users                                   |
| GET    | `/users/create`            | Show create user form                            |
| POST   | `/users/save`              | Save (create or update) user                     |
| GET    | `/users/edit/{id}`         | Show edit user form                              |
| GET    | `/users/delete/{id}`       | Delete user (blocked if cart has items)          |

### Product Management
| Method | URL                        | Description                                      |
|--------|----------------------------|--------------------------------------------------|
| GET    | `/products`                | List products (supports `?userId=X`)             |
| GET    | `/products/create`         | Show create product form                         |
| POST   | `/products/save`           | Save (create or update) product                  |
| GET    | `/products/edit/{id}`      | Show edit product form                           |
| GET    | `/products/delete/{id}`    | Delete product                                   |

### Shopping Cart
| Method | URL                              | Description                                      |
|--------|----------------------------------|--------------------------------------------------|
| GET    | `/cart/view?userId={id}`         | View user's cart page                            |
| POST   | `/cart/update`                   | Update item quantity                             |
| POST   | `/cart/remove`                   | Remove an item from cart                         |
| POST   | `/cart/clear`                    | Clear entire cart                                |
| GET    | `/api/cart/{userId}`             | Get cart data (JSON) – used for mini-cart        |
| POST   | `/api/cart/{userId}/add`         | Add item to cart (JSON body: `{productId, quantity}`) |

## How to Run

### Prerequisites
- Java 17 or higher
- PostgreSQL running locally
- Maven

### Steps
1. Create the database:
   ```bash
   createdb nexCart_db
2. Clone and run the project:
   ```bash
   git clone https://github.com/turjo997/NexCart.git
   cd nextcart
   mvn spring-boot:run
3. ```Open your browser and go to:
   http://localhost:9090/users/create   
