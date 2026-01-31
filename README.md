# Sistem Inventori Barang - Spring Boot + PostgreSQL

Aplikasi REST API untuk manajemen inventori barang menggunakan Spring Boot dan PostgreSQL dengan relasi database.

## 📋 Fitur

- **Manajemen Kategori**: CRUD kategori produk
- **Manajemen Supplier**: CRUD supplier/pemasok
- **Manajemen Produk**: CRUD produk dengan relasi ke kategori dan supplier
- **Manajemen Stok**: Monitoring dan update stok barang
- **Low Stock Alert**: Deteksi stok rendah dan habis

## 🗄️ Struktur Database

### Relasi Antar Tabel:

- **Category** (1) → (N) **Product** (One-to-Many)
- **Supplier** (1) → (N) **Product** (One-to-Many)
- **Product** (1) → (1) **Stock** (One-to-One)

## 🚀 Teknologi

- Spring Boot 4.0.2
- PostgreSQL
- Spring Data JPA
- Lombok
- Bean Validation
- Maven

## ⚙️ Setup

### 1. Install PostgreSQL

Pastikan PostgreSQL sudah terinstall di komputer Anda.

### 2. Buat Database

```sql
CREATE DATABASE inventory_db;
```

### 3. Konfigurasi Database

Edit file `application.properties` jika perlu mengubah username/password PostgreSQL:

```properties
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 4. Jalankan Aplikasi

```bash
./mvnw spring-boot:run
```

Aplikasi akan berjalan di `http://localhost:8080`

## 📡 API Endpoints

### Category

- `GET /api/categories` - Get semua kategori
- `GET /api/categories/{id}` - Get kategori by ID
- `POST /api/categories` - Buat kategori baru
- `PUT /api/categories/{id}` - Update kategori
- `DELETE /api/categories/{id}` - Hapus kategori

### Supplier

- `GET /api/suppliers` - Get semua supplier
- `GET /api/suppliers/{id}` - Get supplier by ID
- `GET /api/suppliers/search?name={name}` - Cari supplier by nama
- `POST /api/suppliers` - Buat supplier baru
- `PUT /api/suppliers/{id}` - Update supplier
- `DELETE /api/suppliers/{id}` - Hapus supplier

### Product

- `GET /api/products` - Get semua produk
- `GET /api/products/{id}` - Get produk by ID
- `GET /api/products/search?name={name}` - Cari produk by nama
- `GET /api/products/category/{categoryId}` - Get produk by kategori
- `GET /api/products/supplier/{supplierId}` - Get produk by supplier
- `POST /api/products` - Buat produk baru
- `PUT /api/products/{id}` - Update produk
- `DELETE /api/products/{id}` - Hapus produk

### Stock

- `GET /api/stocks` - Get semua stok
- `GET /api/stocks/{id}` - Get stok by ID
- `GET /api/stocks/product/{productId}` - Get stok by product ID
- `GET /api/stocks/low-stock` - Get produk dengan stok rendah
- `GET /api/stocks/out-of-stock` - Get produk yang habis
- `PUT /api/stocks/{id}` - Update stok
- `POST /api/stocks/product/{productId}/add` - Tambah stok
- `POST /api/stocks/product/{productId}/reduce` - Kurangi stok

## 📝 Contoh Request

### Buat Kategori

```json
POST /api/categories
{
  "name": "Elektronik",
  "description": "Barang elektronik"
}
```

### Buat Supplier

```json
POST /api/suppliers
{
  "name": "PT. Supplier Jaya",
  "address": "Jakarta",
  "phoneNumber": "021-12345678",
  "email": "supplier@example.com",
  "description": "Supplier terpercaya"
}
```

### Buat Produk

```json
POST /api/products
{
  "name": "Laptop Asus",
  "description": "Laptop gaming",
  "price": 15000000,
  "categoryId": 1,
  "supplierId": 1,
  "stockQuantity": 50,
  "minimumStock": 10
}
```

### Tambah Stok

```json
POST /api/stocks/product/1/add
{
  "amount": 20
}
```

### Kurangi Stok

```json
POST /api/stocks/product/1/reduce
{
  "amount": 5
}
```

## 🎓 Konsep yang Dipelajari

1. **JPA Relationships**
   - One-to-Many
   - Many-to-One
   - One-to-One

2. **Spring Boot Features**
   - REST API
   - Dependency Injection
   - Service Layer Pattern
   - DTO Pattern

3. **Database**
   - PostgreSQL
   - Hibernate/JPA
   - Auto DDL

4. **Validation**
   - Bean Validation
   - Custom Validation

5. **Lombok**
   - @Data
   - @NoArgsConstructor
   - @AllArgsConstructor
   - @RequiredArgsConstructor

## 📚 Belajar Lebih Lanjut

- Tambahkan fitur pencarian advanced
- Implementasi paging dan sorting
- Tambahkan exception handling global
- Implementasi Spring Security
- Tambahkan dokumentasi API dengan Swagger/OpenAPI
- Implementasi transaction history
- Tambahkan unit testing

## 🧪 Testing

### White Box Testing

Project ini sudah dilengkapi dengan **White Box Testing** yang komprehensif menggunakan JUnit 5 dan Mockito.

#### Struktur Test Files

| File                       | Deskripsi               | Jumlah Tests |
| -------------------------- | ----------------------- | ------------ |
| `StockTest.java`           | Entity Stock testing    | 21 tests     |
| `ProductServiceTest.java`  | ProductService testing  | 21 tests     |
| `UserServiceTest.java`     | UserService testing     | 22 tests     |
| `StockServiceTest.java`    | StockService testing    | 25 tests     |
| `CategoryServiceTest.java` | CategoryService testing | 17 tests     |

#### Teknik Testing yang Digunakan

1. **Statement Coverage** - Memastikan setiap statement dieksekusi minimal sekali
2. **Branch Coverage** - Menguji setiap cabang kondisi (if/else)
3. **Path Coverage** - Menguji semua jalur eksekusi yang mungkin
4. **Condition Coverage** - Menguji setiap kondisi boolean
5. **Boundary Value Analysis** - Menguji nilai batas
6. **Exception Testing** - Menguji penanganan exception

#### Menjalankan Tests

```bash
# Jalankan semua tests
./mvnw test

# Jalankan test class tertentu
./mvnw test -Dtest="StockTest"
./mvnw test -Dtest="ProductServiceTest"
./mvnw test -Dtest="UserServiceTest"
./mvnw test -Dtest="StockServiceTest"
./mvnw test -Dtest="CategoryServiceTest"

# Jalankan multiple test classes
./mvnw test -Dtest="StockTest,CategoryServiceTest"

# Jalankan test dengan output verbose
./mvnw test -Dsurefire.useFile=false
```

#### Contoh Output Test

```
[INFO] Tests run: 107, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

#### Teknologi Testing

- **JUnit 5** - Framework testing utama
- **Mockito** - Mocking dependencies untuk unit testing
- **H2 Database** - In-memory database untuk testing (opsional)

Selamat belajar! 🎉
