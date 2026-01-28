# 📚 Penjelasan Project Inventory Management System

## 📋 Ringkasan Project

Ini adalah sistem manajemen inventori menggunakan:

- **Backend**: Spring Boot 4.0.2 + Java 25 + PostgreSQL
- **Frontend**: HTML + JavaScript (Vanilla JS dengan Axios)
- **Arsitektur**: REST API dengan pola MVC (Model-View-Controller)

---

## 🔧 BACKEND - SPRING BOOT

### 1. ENTITY LAYER (`@Entity`)

**Entity** = representasi tabel database dalam bentuk class Java

```java
@Entity  // ← Menandai ini adalah entitas JPA (table database)
@Table(name = "products")  // ← Nama tabel di database
@Data  // ← Lombok: auto-generate getter/setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
}
```

#### Penjelasan Annotation Entity:

| Annotation                                            | Fungsi                                                           |
| ----------------------------------------------------- | ---------------------------------------------------------------- |
| `@Entity`                                             | Memberitahu Spring bahwa class ini adalah tabel database         |
| `@Table(name = "products")`                           | Nama tabel di PostgreSQL                                         |
| `@Id`                                                 | Menandai field sebagai primary key                               |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Auto-increment ID                                                |
| `@Column`                                             | Konfigurasi kolom (nullable, unique, length, precision, dll)     |
| `@NotBlank`                                           | Validasi: field tidak boleh kosong                               |
| `@NotNull`                                            | Validasi: field tidak boleh null                                 |
| `@Positive`                                           | Validasi: harus angka positif                                    |
| `@Data`                                               | Lombok: auto-generate getter, setter, toString, equals, hashCode |

#### Relasi Database (Relationship):

```java
// MANY products → ONE category
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id", nullable = false)
private Category category;

// MANY products → ONE supplier
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "supplier_id", nullable = false)
private Supplier supplier;

// ONE product → ONE stock
@OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
private Stock stock;
```

**Penjelasan Relasi:**

- `@ManyToOne`: Banyak product memiliki satu category/supplier
- `@OneToOne`: Satu product memiliki satu stock record
- `@JoinColumn`: Kolom foreign key di tabel products
- `fetch = FetchType.LAZY`: Data relasi dimuat hanya saat diakses (optimasi performa)
- `cascade = CascadeType.ALL`: Operasi pada Product (save/delete) juga berlaku ke Stock
- `mappedBy = "product"`: Stock adalah pemilik relasi

#### Lifecycle Hooks:

```java
@PrePersist  // ← Dijalankan sebelum data disimpan pertama kali
protected void onCreate() {
    createdAt = LocalDateTime.now();
    sku = generateSku();  // Auto-generate SKU
}

@PreUpdate  // ← Dijalankan sebelum data di-update
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```

---

### 2. REPOSITORY LAYER (`@Repository`)

**Repository** = interface untuk operasi database (CRUD)

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods
    Optional<Product> findBySku(String sku);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryIdAndSupplierId(Long categoryId, Long supplierId);
}
```

#### Fungsi Built-in dari JpaRepository:

- `save(entity)` → Create atau Update
- `findById(id)` → Read by ID
- `findAll()` → Read semua data
- `deleteById(id)` → Delete by ID
- `count()` → Hitung jumlah record
- `existsById(id)` → Cek apakah ID exists

#### Custom Query Methods:

Spring Data JPA otomatis generate query dari nama method:

| Method Name                                   | SQL Generated                                            |
| --------------------------------------------- | -------------------------------------------------------- |
| `findBySku(String sku)`                       | `SELECT * FROM products WHERE sku = ?`                   |
| `findByNameContainingIgnoreCase(String name)` | `SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?)` |
| `findByCategoryId(Long categoryId)`           | `SELECT * FROM products WHERE category_id = ?`           |

#### Manual JPQL Query:

```java
@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
List<Product> findByCategoryIdAndSupplierId(Long categoryId, Long supplierId);
```

**JPQL** = Java Persistence Query Language (pakai nama class/field, bukan tabel/kolom)

---

### 3. SERVICE LAYER (`@Service`)

**Service** = logika bisnis aplikasi

```java
@Service  // ← Spring component untuk business logic
@RequiredArgsConstructor  // ← Lombok: auto-generate constructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)  // ← Transaksi read-only (optimize)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)  // Convert Entity → DTO
                .collect(Collectors.toList());
    }

    @Transactional  // ← Transaksi write (rollback jika error)
    public ProductDto createProduct(ProductDto productDto) {
        // Validasi category exists
        Category category = categoryRepository.findById(productDto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));

        // Create product + stock
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setCategory(category);

        // Buat stock default untuk produk baru
        Stock stock = new Stock();
        stock.setQuantity(productDto.getStockQuantity());
        stock.setProduct(product);
        product.setStock(stock);

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCategoryName(product.getCategory().getName());
        // ... mapping lainnya
        return dto;
    }
}
```

#### Penjelasan Annotation Service:

- **`@Service`**: Menandai class sebagai Spring bean untuk business logic
- **`@Transactional`**: Semua operasi database dalam satu transaksi. Jika ada error, semua di-rollback
- **`@Transactional(readOnly = true)`**: Optimasi untuk operasi read-only
- **`@RequiredArgsConstructor`**: Lombok auto-generate constructor untuk final fields (Dependency Injection)

#### Kenapa pakai Service Layer?

- Pisahkan business logic dari controller
- Reusable logic (bisa dipanggil dari berbagai controller)
- Transaction management
- Validasi data sebelum masuk database

---

### 4. CONTROLLER LAYER (`@RestController`)

**Controller** = REST API endpoint yang menerima HTTP request

```java
@RestController  // ← REST controller (return JSON, bukan view)
@RequestMapping("/api/products")  // ← Base URL path
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/products
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // GET /api/products/search?name=laptop
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    // POST /api/products (create)
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(productDto));
    }

    // PUT /api/products/{id} (update)
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}
```

#### Annotation Controller Penting:

| Annotation                         | Fungsi                                                  | Contoh                         |
| ---------------------------------- | ------------------------------------------------------- | ------------------------------ |
| `@RestController`                  | Kombinasi `@Controller` + `@ResponseBody` (return JSON) | -                              |
| `@RequestMapping("/api/products")` | Base path untuk semua endpoint di controller ini        | `/api/products`                |
| `@GetMapping`                      | HTTP GET request                                        | `@GetMapping("/{id}")`         |
| `@PostMapping`                     | HTTP POST request (create)                              | `@PostMapping`                 |
| `@PutMapping`                      | HTTP PUT request (update)                               | `@PutMapping("/{id}")`         |
| `@DeleteMapping`                   | HTTP DELETE request                                     | `@DeleteMapping("/{id}")`      |
| `@PathVariable`                    | Ambil parameter dari URL path                           | `/products/123` → id=123       |
| `@RequestParam`                    | Ambil parameter dari query string                       | `?name=laptop` → name="laptop" |
| `@RequestBody`                     | Ambil data dari request body (JSON)                     | POST body → ProductDto         |
| `@Valid`                           | Validasi input sesuai annotation di DTO                 | Cek `@NotBlank`, `@Positive`   |

#### HTTP Status Codes:

```java
ResponseEntity.ok(data)                        // 200 OK
ResponseEntity.status(HttpStatus.CREATED)      // 201 Created
ResponseEntity.noContent().build()             // 204 No Content
ResponseEntity.notFound().build()              // 404 Not Found
ResponseEntity.badRequest()                    // 400 Bad Request
```

---

### 5. DTO (Data Transfer Object)

**DTO** = object untuk transfer data antara layer (bukan Entity langsung)

```java
@Data
public class ProductDto {
    private Long id;
    private String name;
    private String sku;
    private String description;
    private BigDecimal price;

    // Foreign key untuk request
    private Long categoryId;
    private Long supplierId;

    // Data relasi untuk response
    private String categoryName;
    private String supplierName;

    // Stock info
    private Integer stockQuantity;
    private Integer minimumStock;
}
```

#### Kenapa pakai DTO, bukan Entity langsung?

1. **Hindari Circular Reference**:

   ```
   Product → Category → List<Product> → Category → ...
   (infinite loop saat serialize ke JSON)
   ```

2. **Kontrol Data yang Di-expose**:
   - Entity punya banyak field internal yang tidak perlu di-expose
   - DTO hanya berisi data yang dibutuhkan frontend

3. **Pisahkan Model Database dari API Contract**:
   - Perubahan database tidak langsung affect API
   - API lebih stable dan backward compatible

4. **Gabungkan Data dari Multiple Entities**:
   ```java
   ProductDto {
       productName,      // dari Product
       categoryName,     // dari Category
       supplierName,     // dari Supplier
       stockQuantity     // dari Stock
   }
   ```

---

### 6. CONFIGURATION

#### CORS Configuration

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Allow CORS untuk /api/*
                .allowedOrigins("*")    // Allow semua origin (development only!)
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

**CORS** = Cross-Origin Resource Sharing

- Browser security: blok request dari domain lain
- Config ini allow frontend (localhost:5500) akses backend (localhost:8080)

⚠️ **Production**: Ganti `"*"` dengan domain frontend specific!

---

## 🌐 FRONTEND - HTML + JavaScript

### 1. Struktur File Frontend

```
frontend/
├── index.html          → Dashboard (ringkasan data)
├── products.html       → Halaman kelola produk
├── categories.html     → Halaman kelola kategori
├── suppliers.html      → Halaman kelola supplier
├── stocks.html         → Halaman kelola stok
└── js/
    ├── config.js       → Konfigurasi API endpoints
    ├── products.js     → Logic halaman produk
    ├── categories.js   → Logic halaman kategori
    ├── suppliers.js    → Logic halaman supplier
    ├── stocks.js       → Logic halaman stok
    └── dashboard.js    → Logic dashboard
```

---

### 2. Config (API Endpoints)

**File: `frontend/js/config.js`**

```javascript
const API_BASE_URL = "http://localhost:8080/api";

const API_ENDPOINTS = {
  products: `${API_BASE_URL}/products`,
  categories: `${API_BASE_URL}/categories`,
  suppliers: `${API_BASE_URL}/suppliers`,
  stocks: `${API_BASE_URL}/stocks`,
};

function formatCurrency(amount) {
  return new Intl.NumberFormat("id-ID", {
    style: "currency",
    currency: "IDR",
  }).format(amount);
}

function handleError(error) {
  console.error("Error:", error);
  alert(error.response?.data?.message || "Terjadi kesalahan");
}
```

---

### 3. Frontend Logic (products.js)

#### Load Data dari API (HTTP GET)

```javascript
let allProducts = [];

async function loadProducts() {
  try {
    // HTTP GET request ke backend
    const response = await axios.get(API_ENDPOINTS.products);
    allProducts = response.data; // Array of ProductDto
    displayProducts(allProducts);
  } catch (error) {
    handleError(error);
  }
}
```

**Axios** = library untuk HTTP request (alternatif: fetch API)

---

#### Display Data ke Table HTML

```javascript
function displayProducts(products) {
  const tbody = document.getElementById("productsTableBody");

  if (products.length === 0) {
    tbody.innerHTML = '<tr><td colspan="9">No products found</td></tr>';
    return;
  }

  tbody.innerHTML = products
    .map((product) => {
      const stockBadge = getStockBadge(product.stockQuantity, product.minimumStock);

      return `
            <tr>
                <td>${product.id}</td>
                <td><strong>${product.name}</strong></td>
                <td><code>${product.sku || "-"}</code></td>
                <td>${product.description || "-"}</td>
                <td><span class="badge bg-info">${product.categoryName}</span></td>
                <td>${product.supplierName}</td>
                <td>${formatCurrency(product.price)}</td>
                <td>${stockBadge}</td>
                <td>
                    <button class="btn btn-warning" onclick="openEditModal(${product.id})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-danger" onclick="openDeleteModal(${product.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    })
    .join("");
}

function getStockBadge(quantity, minimum) {
  if (quantity === 0) {
    return `<span class="badge bg-danger">${quantity} (Out of Stock)</span>`;
  } else if (quantity < minimum) {
    return `<span class="badge bg-warning">${quantity} (Low Stock)</span>`;
  } else {
    return `<span class="badge bg-success">${quantity}</span>`;
  }
}
```

---

#### Create Product (HTTP POST)

```javascript
async function createProduct() {
  // Ambil data dari form
  const productDto = {
    name: document.getElementById("productName").value,
    description: document.getElementById("productDescription").value,
    price: parseFloat(document.getElementById("productPrice").value),
    categoryId: parseInt(document.getElementById("productCategory").value),
    supplierId: parseInt(document.getElementById("productSupplier").value),
    stockQuantity: parseInt(document.getElementById("stockQuantity").value),
    minimumStock: parseInt(document.getElementById("minimumStock").value),
  };

  try {
    // HTTP POST request
    const response = await axios.post(API_ENDPOINTS.products, productDto);

    // Tutup modal
    bootstrap.Modal.getInstance(document.getElementById("productModal")).hide();

    // Refresh table
    loadProducts();

    // Show success message
    alert("Produk berhasil ditambahkan!");
  } catch (error) {
    handleError(error);
  }
}
```

---

#### Update Product (HTTP PUT)

```javascript
async function updateProduct(id) {
  const productDto = {
    name: document.getElementById("productName").value,
    description: document.getElementById("productDescription").value,
    price: parseFloat(document.getElementById("productPrice").value),
    categoryId: parseInt(document.getElementById("productCategory").value),
    supplierId: parseInt(document.getElementById("productSupplier").value),
  };

  try {
    // HTTP PUT request
    await axios.put(`${API_ENDPOINTS.products}/${id}`, productDto);

    bootstrap.Modal.getInstance(document.getElementById("productModal")).hide();
    loadProducts();
    alert("Produk berhasil diupdate!");
  } catch (error) {
    handleError(error);
  }
}
```

---

#### Delete Product (HTTP DELETE)

```javascript
async function confirmDelete() {
  try {
    // HTTP DELETE request
    await axios.delete(`${API_ENDPOINTS.products}/${deleteProductId}`);

    bootstrap.Modal.getInstance(document.getElementById("deleteModal")).hide();
    loadProducts();
    alert("Produk berhasil dihapus!");
  } catch (error) {
    handleError(error);
  }
}
```

---

#### Filter & Search

```javascript
function filterProducts() {
  const searchTerm = document.getElementById("searchInput").value.toLowerCase();
  const categoryFilter = document.getElementById("categoryFilter").value;
  const supplierFilter = document.getElementById("supplierFilter").value;

  let filtered = allProducts;

  // Filter by search term
  if (searchTerm) {
    filtered = filtered.filter((p) => p.name.toLowerCase().includes(searchTerm) || p.sku?.toLowerCase().includes(searchTerm) || p.description?.toLowerCase().includes(searchTerm));
  }

  // Filter by category
  if (categoryFilter) {
    filtered = filtered.filter((p) => p.categoryId == categoryFilter);
  }

  // Filter by supplier
  if (supplierFilter) {
    filtered = filtered.filter((p) => p.supplierId == supplierFilter);
  }

  displayProducts(filtered);
}
```

---

## 🔄 ALUR DATA: Frontend → Backend → Database

### Contoh: User Membuat Product Baru

```
┌─────────────┐
│   BROWSER   │  1. User isi form & klik "Save"
│  (Frontend) │
└──────┬──────┘
       │ 2. JavaScript: ambil data form
       │    → kirim HTTP POST request
       │
       ▼
┌──────────────────────────────────────────────────┐
│              SPRING BOOT BACKEND                  │
│                                                   │
│  ┌────────────────────────────────────────────┐  │
│  │  @RestController - ProductController       │  │
│  │  3. Terima POST /api/products              │  │
│  │     @Valid @RequestBody ProductDto dto     │  │
│  │     → Validasi input (@NotBlank, dll)      │  │
│  └────────────┬───────────────────────────────┘  │
│               │                                   │
│               ▼                                   │
│  ┌────────────────────────────────────────────┐  │
│  │  @Service - ProductService                 │  │
│  │  4. Business Logic:                        │  │
│  │     - Cek category exists                  │  │
│  │     - Cek supplier exists                  │  │
│  │     - Convert DTO → Entity                 │  │
│  │     - Create Stock                         │  │
│  └────────────┬───────────────────────────────┘  │
│               │                                   │
│               ▼                                   │
│  ┌────────────────────────────────────────────┐  │
│  │  @Repository - ProductRepository           │  │
│  │  5. save(product)                          │  │
│  │     → Generate SQL INSERT                  │  │
│  └────────────┬───────────────────────────────┘  │
└───────────────┼───────────────────────────────────┘
                │
                ▼
       ┌─────────────────┐
       │   PostgreSQL    │  6. Execute SQL:
       │    DATABASE     │     INSERT INTO products...
       └────────┬────────┘     INSERT INTO stocks...
                │
                │ 7. Return ID & data
                ▼
       ┌────────────────┐
       │  @Service      │  8. Convert Entity → DTO
       │                │     return ProductDto
       └────────┬───────┘
                │
                ▼
       ┌────────────────┐
       │ @Controller    │  9. ResponseEntity.status(201)
       │                │     .body(productDto)
       └────────┬───────┘
                │
                ▼
       ┌────────────────┐
       │   Frontend     │  10. Terima response 201
       │                │      → Refresh table
       │                │      → Show success message
       └────────────────┘
```

---

## 📊 RELASI DATABASE

```
┌──────────────┐
│   Category   │
│──────────────│
│ id (PK)      │
│ name         │───┐
│ description  │   │
└──────────────┘   │
                   │ One-to-Many
                   │
┌──────────────┐   │    ┌──────────────┐
│   Supplier   │   │    │   Product    │
│──────────────│   │    │──────────────│
│ id (PK)      │───┼───▶│ id (PK)      │
│ name         │   │    │ name         │
│ contact      │   │    │ sku          │
│ address      │   └───▶│ category_id  │ (FK)
└──────────────┘        │ supplier_id  │ (FK)
                        │ price        │
     One-to-Many        └──────┬───────┘
                               │
                               │ One-to-One
                               │
                        ┌──────▼───────┐
                        │    Stock     │
                        │──────────────│
                        │ id (PK)      │
                        │ product_id   │ (FK)
                        │ quantity     │
                        │ minimum_stock│
                        └──────────────┘
```

### Penjelasan Relasi:

1. **Category ← Product** (One-to-Many)
   - Satu kategori punya banyak produk
   - Contoh: Kategori "Electronics" → Product: Laptop, Mouse, Keyboard

2. **Supplier ← Product** (One-to-Many)
   - Satu supplier supply banyak produk
   - Contoh: Supplier "ABC Corp" → Product: Laptop, Monitor

3. **Product → Stock** (One-to-One)
   - Satu produk punya satu record stok
   - Stock track jumlah barang & minimum stock level

---

## 🎯 SUMMARY - Arsitektur Berlapis

### Backend Layers:

| Layer          | Tanggung Jawab               | Annotation                           | Contoh                 |
| -------------- | ---------------------------- | ------------------------------------ | ---------------------- |
| **Entity**     | Model database (table)       | `@Entity`, `@Table`                  | Product.java           |
| **Repository** | CRUD operations              | `@Repository`, extends JpaRepository | ProductRepository.java |
| **Service**    | Business logic & transaction | `@Service`, `@Transactional`         | ProductService.java    |
| **Controller** | REST API endpoint            | `@RestController`, `@GetMapping`     | ProductController.java |
| **DTO**        | Data transfer object         | `@Data`                              | ProductDto.java        |
| **Config**     | Configuration                | `@Configuration`                     | CorsConfig.java        |

### Flow Pattern:

```
Request → Controller → Service → Repository → Database
                ↓         ↓          ↓
              DTO    Business     Entity
                     Logic
```

---

## 🛠️ Technology Stack

### Backend:

- **Spring Boot 4.0.2**: Framework Java untuk build REST API
- **Spring Data JPA**: ORM (Object-Relational Mapping) untuk database operations
- **Hibernate**: JPA implementation
- **PostgreSQL**: Relational database
- **Lombok**: Reduce boilerplate code (auto-generate getter/setter)
- **Jakarta Validation**: Input validation (`@NotBlank`, `@Positive`)

### Frontend:

- **HTML5**: Struktur halaman
- **CSS3 + Bootstrap 5**: Styling & responsive design
- **JavaScript (ES6+)**: Logic & interactivity
- **Axios**: HTTP client untuk REST API calls
- **Bootstrap Icons**: Icon library

### Build Tool:

- **Maven**: Dependency management & build automation

---

## 📝 Key Concepts

### 1. Dependency Injection (DI)

```java
@Service
@RequiredArgsConstructor  // Lombok: auto constructor
public class ProductService {
    // Spring auto-inject repository saat runtime
    private final ProductRepository productRepository;
}
```

Spring otomatis create instance & inject dependencies.

---

### 2. Transaction Management

```java
@Transactional  // Start transaction
public ProductDto createProduct(ProductDto dto) {
    // Multiple database operations
    categoryRepository.findById(...);  // Query 1
    supplierRepository.findById(...);  // Query 2
    productRepository.save(...);       // Insert 1
    stockRepository.save(...);         // Insert 2

    // Jika salah satu fail → ROLLBACK semua
    // Jika semua success → COMMIT
}
```

---

### 3. RESTful API Principles

| Method | Path                 | Action           | Response           |
| ------ | -------------------- | ---------------- | ------------------ |
| GET    | `/api/products`      | Get all products | 200 OK + data      |
| GET    | `/api/products/{id}` | Get by ID        | 200 OK + data      |
| POST   | `/api/products`      | Create new       | 201 Created + data |
| PUT    | `/api/products/{id}` | Update existing  | 200 OK + data      |
| DELETE | `/api/products/{id}` | Delete           | 204 No Content     |

---

### 4. Lazy Loading vs Eager Loading

```java
// LAZY: data dimuat saat diakses
@ManyToOne(fetch = FetchType.LAZY)
private Category category;

// Product query → SELECT * FROM products (category tidak di-load)
// Saat akses product.getCategory() → SELECT * FROM categories

// EAGER: data langsung dimuat
@ManyToOne(fetch = FetchType.EAGER)
private Category category;

// Product query → SELECT * FROM products JOIN categories (langsung join)
```

**Best Practice**: Gunakan LAZY untuk performa, convert ke DTO di Service layer.

---

## 🚀 Cara Menjalankan Project

### 1. Setup Database (PostgreSQL)

```sql
CREATE DATABASE inventory_db;
```

### 2. Configure `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Run Backend

```bash
# Di root project folder
./mvnw spring-boot:run

# Atau di Windows
mvnw.cmd spring-boot:run
```

Backend berjalan di: `http://localhost:8080`

### 4. Run Frontend

Buka `frontend/index.html` dengan:

- Live Server extension di VS Code
- Atau buka langsung di browser

---

## 📚 Istilah Penting

| Istilah                  | Pengertian                                                                       |
| ------------------------ | -------------------------------------------------------------------------------- |
| **ORM**                  | Object-Relational Mapping: konversi otomatis antara object Java ↔ table database |
| **JPA**                  | Java Persistence API: standard untuk ORM di Java                                 |
| **Hibernate**            | Implementasi JPA yang paling populer                                             |
| **Entity**               | Class Java yang merepresentasikan table database                                 |
| **Repository**           | Interface untuk operasi CRUD database                                            |
| **DTO**                  | Data Transfer Object: object untuk transfer data antar layer                     |
| **Bean**                 | Object yang di-manage oleh Spring container                                      |
| **Dependency Injection** | Spring otomatis inject dependencies yang dibutuhkan                              |
| **REST**                 | Representational State Transfer: arsitektur API menggunakan HTTP                 |
| **CRUD**                 | Create, Read, Update, Delete: operasi dasar database                             |
| **Transaction**          | Sekumpulan operasi database yang harus semua success atau semua gagal            |
| **CORS**                 | Cross-Origin Resource Sharing: allow request dari domain lain                    |

---

## 🎓 Kesimpulan

Project ini mendemonstrasikan:

✅ **Clean Architecture**: Pemisahan concerns (Entity, Repository, Service, Controller)  
✅ **REST API**: Standard communication antara frontend & backend  
✅ **Database Relationships**: One-to-Many, One-to-One dengan JPA  
✅ **Transaction Management**: ACID compliance  
✅ **Input Validation**: Data integrity  
✅ **DTO Pattern**: Separation of concerns  
✅ **Dependency Injection**: Loose coupling, easy testing

Ini adalah contoh aplikasi Spring Boot yang production-ready dengan best practices!

---

**Dibuat pada**: 28 Januari 2026  
**Project**: Inventory Management System  
**Tech Stack**: Spring Boot 4.0.2 + Java 25 + PostgreSQL + JavaScript
