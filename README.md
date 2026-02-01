# Sistem Inventori Barang

REST API + Frontend untuk manajemen inventori barang dengan autentikasi user.

## 🚀 Teknologi

- **Backend**: Spring Boot 4.0.2, Java 25, Spring Security, Spring Data JPA
- **Database**: PostgreSQL
- **Frontend**: HTML, CSS, JavaScript
- **Testing**: JUnit 5, Mockito, H2

## 📋 Fitur

- Autentikasi (Login/Register) dengan role Admin & Staff
- CRUD Kategori, Supplier, Produk, Stok, User
- Low Stock Alert
- Dashboard Admin & Staff

## ⚙️ Setup

```bash
# 1. Buat database PostgreSQL
CREATE DATABASE inventory_db;

# 2. Konfigurasi di application.properties

# 3. Jalankan
./mvnw spring-boot:run
```

Aplikasi berjalan di `http://localhost:8080`

## 📡 API Endpoints

| Resource | Endpoints                                      |
| -------- | ---------------------------------------------- |
| Auth     | POST `/api/auth/login`, `/api/auth/register`   |
| Category | GET/POST/PUT/DELETE `/api/categories`          |
| Supplier | GET/POST/PUT/DELETE `/api/suppliers`           |
| Product  | GET/POST/PUT/DELETE `/api/products`            |
| Stock    | GET/PUT `/api/stocks`, `/api/stocks/low-stock` |
| User     | GET/POST/PUT/DELETE `/api/users`               |

## 🗄️ Database Relations

- Category (1) → (N) Product
- Supplier (1) → (N) Product
- Product (1) → (1) Stock

## 🧪 Testing

```bash
./mvnw test
```

Test classes: `StockTest`, `ProductServiceTest`, `UserServiceTest`, `StockServiceTest`, `CategoryServiceTest`
