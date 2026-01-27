# Inventory Management System - Frontend

Frontend aplikasi Inventory Management System yang dibangun menggunakan HTML, Bootstrap 5, dan Axios.

## 📋 Fitur

- **Dashboard**: Tampilan ringkasan inventory dengan statistik dan alert
- **Products Management**: CRUD produk dengan filter dan pencarian
- **Categories Management**: Kelola kategori produk
- **Suppliers Management**: Kelola supplier/pemasok
- **Stock Management**: Kelola stok produk (tambah, kurangi, update minimum stock)

## 🚀 Cara Menjalankan

### Prasyarat

- Backend API sudah berjalan di `http://localhost:8080`
- Browser modern (Chrome, Firefox, Edge, Safari)

### Langkah-langkah

1. **Pastikan Backend Berjalan**
   
   Jalankan aplikasi Spring Boot terlebih dahulu:
   ```bash
   cd inventory-example
   ./mvnw spring-boot:run
   ```
   
   Backend akan berjalan di: `http://localhost:8080`

2. **Buka Frontend**
   
   Ada beberapa cara untuk menjalankan frontend:

   **Opsi 1: Buka langsung file HTML**
   - Buka file `index.html` di browser
   - Namun, beberapa browser mungkin memblokir CORS untuk file lokal

   **Opsi 2: Menggunakan Live Server (Recommended)**
   
   Jika menggunakan VS Code:
   - Install extension "Live Server"
   - Klik kanan pada `index.html`
   - Pilih "Open with Live Server"
   - Frontend akan terbuka di `http://127.0.0.1:5500` atau port lainnya

   **Opsi 3: Menggunakan Python HTTP Server**
   ```bash
   cd frontend
   python -m http.server 8000
   ```
   Kemudian buka: `http://localhost:8000`

   **Opsi 4: Menggunakan Node.js HTTP Server**
   ```bash
   npx http-server frontend -p 8000
   ```
   Kemudian buka: `http://localhost:8000`

## 📁 Struktur File

```
frontend/
├── index.html              # Dashboard utama
├── products.html           # Halaman manajemen produk
├── categories.html         # Halaman manajemen kategori
├── suppliers.html          # Halaman manajemen supplier
├── stocks.html             # Halaman manajemen stok
├── js/
│   ├── config.js          # Konfigurasi API dan utility functions
│   ├── dashboard.js       # Logic untuk dashboard
│   ├── products.js        # Logic untuk products management
│   ├── categories.js      # Logic untuk categories management
│   ├── suppliers.js       # Logic untuk suppliers management
│   └── stocks.js          # Logic untuk stock management
└── README.md              # Dokumentasi ini
```

## 🔧 Konfigurasi

Jika backend API berjalan di URL yang berbeda, ubah di file `js/config.js`:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

## 📱 Halaman-halaman

### 1. Dashboard (`index.html`)
- Statistik total products, categories, suppliers
- Alert untuk low stock dan out of stock items
- Daftar produk terbaru

### 2. Products (`products.html`)
- Menambah, mengubah, dan menghapus produk
- Pencarian produk
- Filter berdasarkan kategori dan supplier
- Informasi stok produk

### 3. Categories (`categories.html`)
- Menambah, mengubah, dan menghapus kategori
- Tampilan card untuk setiap kategori
- Pencarian kategori

### 4. Suppliers (`suppliers.html`)
- Menambah, mengubah, dan menghapus supplier
- Informasi lengkap supplier (contact person, email, phone, address)
- Pencarian supplier

### 5. Stocks (`stocks.html`)
- Lihat semua stok produk
- Filter low stock dan out of stock
- Tambah stok produk
- Kurangi stok produk
- Update minimum stock threshold

## 🎨 Teknologi yang Digunakan

- **Bootstrap 5.3.0**: Framework CSS untuk UI yang responsive
- **Bootstrap Icons**: Icon library
- **Axios**: HTTP client untuk berkomunikasi dengan backend API
- **Vanilla JavaScript**: Tidak menggunakan framework JS tambahan

## ⚠️ Troubleshooting

### CORS Error

Jika mendapat error CORS, pastikan:

1. Backend sudah mengaktifkan CORS. Tambahkan di Spring Boot:
   
   ```java
   @Configuration
   public class WebConfig implements WebMvcConfigurer {
       @Override
       public void addCorsMappings(CorsRegistry registry) {
           registry.addMapping("/api/**")
                   .allowedOrigins("*")
                   .allowedMethods("GET", "POST", "PUT", "DELETE");
       }
   }
   ```

2. Atau jalankan frontend menggunakan web server (bukan dengan membuka file HTML langsung)

### Tidak Bisa Terhubung ke API

- Pastikan backend berjalan di `http://localhost:8080`
- Cek browser console (F12) untuk melihat error detail
- Verifikasi URL API di `js/config.js`

### Data Tidak Muncul

- Buka browser console (F12) untuk melihat error
- Pastikan database memiliki data
- Test API menggunakan Postman atau curl

## 📝 Catatan

- Frontend ini tidak menggunakan authentication/authorization
- Semua operasi langsung terhubung ke backend API
- Error handling ditampilkan dalam bentuk alert di bagian atas halaman
- Responsive design support untuk mobile, tablet, dan desktop

## 🔄 Update Backend Configuration (Optional)

Jika ingin menjalankan frontend dari domain berbeda, tambahkan CORS configuration di Spring Boot:

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:8000", "http://127.0.0.1:5500")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

## 📞 Support

Jika ada pertanyaan atau masalah, silakan buat issue atau hubungi tim development.

---

**Happy Coding! 🚀**