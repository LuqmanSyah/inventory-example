# Quick Start Guide - Inventory Management System

Panduan cepat untuk menjalankan aplikasi Inventory Management System (Backend + Frontend).

## 🚀 Quick Start

### 1. Setup Database PostgreSQL

```bash
# Masuk ke PostgreSQL
psql -U postgres

# Buat database
CREATE DATABASE inventory_db;

# Keluar
\q
```

### 2. Jalankan Backend (Spring Boot)

```bash
# Di direktori root project
cd inventory-example

# Jalankan aplikasi
./mvnw spring-boot:run

# Atau di Windows
mvnw.cmd spring-boot:run
```

Backend akan berjalan di: **http://localhost:8080**

### 3. Jalankan Frontend

**Opsi A: Menggunakan Python (Recommended)**
```bash
# Di direktori frontend
cd frontend
python -m http.server 8000
```

**Opsi B: Menggunakan Node.js**
```bash
cd frontend
npx http-server -p 8000
```

**Opsi C: Menggunakan VS Code Live Server**
1. Install extension "Live Server" di VS Code
2. Klik kanan pada `index.html`
3. Pilih "Open with Live Server"

Frontend akan terbuka di: **http://localhost:8000**

### 4. Akses Aplikasi

Buka browser dan akses: **http://localhost:8000**

## 📝 Membuat Data Awal

Untuk memulai, ikuti urutan ini:

1. **Buat Categories** (Minimal 1)
   - Klik menu "Categories"
   - Klik tombol "Add Category"
   - Contoh: Electronics, Food, Clothing

2. **Buat Suppliers** (Minimal 1)
   - Klik menu "Suppliers"
   - Klik tombol "Add Supplier"
   - Isi nama supplier dan informasi kontak

3. **Buat Products**
   - Klik menu "Products"
   - Klik tombol "Add Product"
   - Pilih category dan supplier yang sudah dibuat

4. **Kelola Stocks**
   - Klik menu "Stocks"
   - Pilih product dan klik tombol "+" untuk menambah stok
   - Atau klik tombol "-" untuk mengurangi stok

## 🔧 Troubleshooting

### Backend tidak bisa start?

**Masalah: Connection refused ke PostgreSQL**
```
Solusi:
1. Pastikan PostgreSQL sudah berjalan
2. Cek username/password di application.properties
3. Pastikan database 'inventory_db' sudah dibuat
```

**Masalah: Port 8080 sudah digunakan**
```
Solusi:
Ubah port di application.properties:
server.port=8081
```

### Frontend tidak bisa akses API?

**Masalah: CORS error**
```
Solusi:
1. Pastikan CorsConfig.java sudah ada di backend
2. Restart backend setelah menambah CorsConfig
3. Pastikan frontend tidak dibuka dengan file:// protocol
   (gunakan web server seperti Python http.server)
```

**Masalah: Cannot connect to server**
```
Solusi:
1. Pastikan backend sudah berjalan di http://localhost:8080
2. Test dengan curl atau Postman:
   curl http://localhost:8080/api/products
3. Cek browser console (F12) untuk error detail
```

## 📚 API Endpoints

### Products
- `GET    /api/products` - Get all products
- `GET    /api/products/{id}` - Get product by ID
- `POST   /api/products` - Create product
- `PUT    /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product
- `GET    /api/products/search?name={name}` - Search products
- `GET    /api/products/category/{categoryId}` - Get by category
- `GET    /api/products/supplier/{supplierId}` - Get by supplier

### Categories
- `GET    /api/categories` - Get all categories
- `GET    /api/categories/{id}` - Get category by ID
- `POST   /api/categories` - Create category
- `PUT    /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Suppliers
- `GET    /api/suppliers` - Get all suppliers
- `GET    /api/suppliers/{id}` - Get supplier by ID
- `POST   /api/suppliers` - Create supplier
- `PUT    /api/suppliers/{id}` - Update supplier
- `DELETE /api/suppliers/{id}` - Delete supplier
- `GET    /api/suppliers/search?name={name}` - Search suppliers

### Stocks
- `GET    /api/stocks` - Get all stocks
- `GET    /api/stocks/{id}` - Get stock by ID
- `GET    /api/stocks/product/{productId}` - Get stock by product
- `GET    /api/stocks/low-stock` - Get low stock items
- `GET    /api/stocks/out-of-stock` - Get out of stock items
- `PUT    /api/stocks/{id}` - Update stock settings
- `POST   /api/stocks/product/{productId}/add?quantity={qty}&reason={reason}` - Add stock
- `POST   /api/stocks/product/{productId}/reduce?quantity={qty}&reason={reason}` - Reduce stock

## 🎯 Fitur Utama

✅ CRUD Products dengan validasi
✅ CRUD Categories
✅ CRUD Suppliers
✅ Stock Management (Add/Reduce)
✅ Low Stock Alert
✅ Out of Stock Alert
✅ Search & Filter Products
✅ Responsive Design
✅ Real-time Error Handling

## 📱 Teknologi

**Backend:**
- Spring Boot 3.x
- PostgreSQL
- JPA/Hibernate
- Lombok
- Bean Validation

**Frontend:**
- HTML5
- Bootstrap 5.3
- Axios
- Vanilla JavaScript

## 💡 Tips

1. **Data Sample**: Buat beberapa categories dan suppliers dulu sebelum menambah products
2. **Stock Management**: Setelah membuat product, set minimum stock dan tambahkan quantity di menu Stocks
3. **Testing API**: Gunakan Postman atau curl untuk testing API secara langsung
4. **Browser DevTools**: Gunakan F12 untuk melihat network requests dan console errors

## 📞 Need Help?

Jika mengalami masalah:
1. Cek terminal backend untuk error logs
2. Cek browser console (F12) untuk error di frontend
3. Pastikan semua dependencies sudah terinstall
4. Restart backend dan refresh browser

---

**Selamat mencoba! 🎉**