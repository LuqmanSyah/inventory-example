# Testing Guide - Sistem Inventori Barang

## 🧪 Pre-requisite Testing

Sebelum testing, pastikan:
1. PostgreSQL sudah berjalan di localhost:5432
2. Database `inventory_db` sudah dibuat
3. Maven dan Java 17+ sudah installed

## 🚀 Startup Application

### Backend
```bash
cd inventory-example
mvn clean install
mvn spring-boot:run
```

Server akan berjalan di: **http://localhost:8080**

### Frontend
Akses melalui browser:
**http://localhost:8080/frontend/login.html**

---

## 🔑 Login Testing

### Test Case 1: Admin Login
**Input:**
- Username: `admin`
- Password: `admin123`

**Expected:**
- ✅ Login berhasil
- ✅ Redirect ke [admin-dashboard.html](http://localhost:8080/frontend/admin-dashboard.html)
- ✅ Navbar biru dengan menu lengkap
- ✅ Statistik: Total Produk, Kategori, Supplier, Stok Rendah

### Test Case 2: Staff Login
**Input:**
- Username: `staff`
- Password: `staff123`

**Expected:**
- ✅ Login berhasil
- ✅ Redirect ke [staff-dashboard.html](http://localhost:8080/frontend/staff-dashboard.html)
- ✅ Navbar hijau dengan menu terbatas (tanpa Kategori)
- ✅ Statistik: Total Produk, Stok Rendah, Produk Habis

### Test Case 3: Login Gagal
**Input:**
- Username: `admin`
- Password: `wrongpassword`

**Expected:**
- ❌ Alert: "Username atau password salah"
- ❌ Tetap di halaman login

### Test Case 4: Empty Fields
**Input:**
- Username: (kosong)
- Password: (kosong)

**Expected:**
- ❌ Alert: "Username dan password harus diisi"

---

## 🚪 Logout Testing

### Test Case 5: Logout Confirmation
**Steps:**
1. Login sebagai admin
2. Klik nama user di top-right navbar
3. Pilih "Keluar"

**Expected:**
- ✅ Modal konfirmasi muncul
- ✅ Text: "Apakah Anda yakin ingin keluar dari sistem inventori?"

### Test Case 6: Logout Confirm
**Steps:**
1. Lanjut dari Test Case 5
2. Klik "Ya, Keluar"

**Expected:**
- ✅ Alert: "Anda berhasil keluar dari sistem"
- ✅ Redirect ke login.html
- ✅ Session/localStorage cleared

### Test Case 7: Logout Cancel
**Steps:**
1. Lanjut dari Test Case 5
2. Klik "Batal" atau close button

**Expected:**
- ✅ Modal tertutup
- ✅ Tetap di halaman current

---

## 📝 Produk Management Testing

### Test Case 8: View Produk List
**Steps:**
1. Login sebagai admin/staff
2. Klik menu "Produk"

**Expected:**
- ✅ Table produk muncul dengan kolom: Nama, SKU, Kategori, Supplier, Harga, Stok
- ✅ Supplier name muncul dengan benar (bukan ID atau kosong)

### Test Case 9: Tambah Produk
**Steps:**
1. Di halaman Produk, klik "Tambah Produk"
2. Isi form:
   - Nama: "Test Product"
   - SKU: (jangan diisi - disabled)
   - Deskripsi: "Product for testing"
   - Harga: 50000
   - Kategori: (pilih salah satu)
   - Supplier: (pilih salah satu)
   - Stok: 10

**Expected:**
- ✅ Field SKU disabled (tidak bisa diinput)
- ✅ Label SKU menunjukkan "Auto-generate"
- ✅ Produk tersimpan dengan SKU otomatis (format: PRD-{timestamp})
- ✅ Alert: "Produk berhasil ditambahkan"

### Test Case 10: Edit Produk
**Steps:**
1. Di table produk, klik icon edit
2. Ubah nama: "Updated Product"
3. Klik "Simpan"

**Expected:**
- ✅ Perubahan tersimpan
- ✅ Alert: "Produk berhasil diperbarui"
- ✅ List ter-update

---

## 🏷️ Kategori Management Testing

### Test Case 11: View Kategori
**Steps:**
1. Login sebagai admin
2. Klik menu "Kategori"

**Expected:**
- ✅ Table kategori muncul
- ✅ Akses penuh (Edit, Delete)

### Test Case 12: Staff Kategori Access
**Steps:**
1. Login sebagai staff
2. Coba akses /frontend/categories.html

**Expected:**
- ✅ Tidak ada menu Kategori di navbar
- ✅ Jika diakses langsung, akan ter-redirect ke dashboard

---

## 🚚 Supplier Management Testing

### Test Case 13: View Supplier
**Steps:**
1. Login sebagai admin/staff
2. Klik menu "Supplier"

**Expected:**
- ✅ Table supplier muncul
- ✅ Kolom: ID, Nama, No. Telepon, Email, Alamat, Deskripsi
- ✅ **Nama supplier muncul dengan benar** (tidak kosong)
- ✅ **No. Telepon muncul dengan benar** (bukan "-")

### Test Case 14: Tambah Supplier
**Steps:**
1. Klik "Tambah Supplier"
2. Isi:
   - Nama: "PT Test Supplier"
   - Alamat: "Jalan Test No. 123"
   - No. Telepon: "081234567890"
   - Email: "test@supplier.com"
   - Deskripsi: "Test supplier"

**Expected:**
- ✅ Supplier tersimpan
- ✅ Alert: "Supplier berhasil ditambahkan"
- ✅ Saat view supplier list, nama dan telepon muncul dengan benar

---

## 📊 Stok Management Testing

### Test Case 15: View Stok (Bug Fix)
**Steps:**
1. Login sebagai admin/staff
2. Klik menu "Stok"

**Expected:**
- ✅ Table stok muncul
- ✅ **Kolom Kategori muncul dengan data** (bukan "-" atau kosong)
- ✅ Kolom: ID, Produk, Stok, Min, Tgl Update, Kategori, Supplier

### Test Case 16: Stok Rendah
**Steps:**
1. Buka tab "Stok Rendah"
2. Update stok produk menjadi <= minimum

**Expected:**
- ✅ Produk muncul di tab "Stok Rendah"
- ✅ **Kategori dan Supplier name muncul dengan benar**

---

## 🎯 Dashboard Testing

### Test Case 17: Admin Dashboard
**Steps:**
1. Login sebagai admin
2. Lihat admin-dashboard.html

**Expected:**
- ✅ 4 Stat Card: Total Produk, Kategori, Supplier, Stok Rendah
- ✅ 3 Tabel: Stok Rendah, Produk Habis, Produk Terbaru
- ✅ Semua data dengan kategori & supplier name yang benar

### Test Case 18: Staff Dashboard
**Steps:**
1. Login sebagai staff
2. Lihat staff-dashboard.html

**Expected:**
- ✅ 3 Stat Card: Total Produk, Stok Rendah, Produk Habis
- ✅ 3 Tabel sama seperti admin tapi role-appropriate
- ✅ Navbar berwarna hijau (berbeda dari admin)

---

## 🔐 Security Testing

### Test Case 19: Auth Check
**Steps:**
1. Clear localStorage
2. Akses langsung http://localhost:8080/frontend/products.html

**Expected:**
- ✅ Redirect otomatis ke login.html
- ❌ Tidak bisa akses tanpa login

### Test Case 20: Session Persistence
**Steps:**
1. Login sebagai admin
2. Refresh halaman
3. Navigate antar halaman

**Expected:**
- ✅ Session tetap aktif
- ✅ User tetap logged in
- ✅ Nama user tetap terlihat di navbar

---

## 🐛 Bug Verification

### Bug Fix #1: SKU Input
- [x] SKU field disabled di form
- [x] Tidak bisa menginput manual SKU
- [x] SKU auto-generate saat create
- [x] Format SKU: PRD-{timestamp}

### Bug Fix #2: Supplier Info
- [x] Nama supplier muncul di list
- [x] No. Telepon muncul di list
- [x] Tidak ada field kosong/"-" untuk supplier yang ada data

### Bug Fix #3: Kategori di Stok
- [x] Kategori muncul di halaman stok
- [x] Kategori muncul dengan benar (bukan "-")
- [x] Supplier name juga muncul dengan benar

---

## 📈 Performance Testing

### Test Case 21: Large Data Set
**Steps:**
1. Create 100+ produk
2. Load halaman produk

**Expected:**
- ✅ Halaman load dalam < 2 detik
- ✅ Table responsive dan smooth

---

## 🎓 Notes

- Default users otomatis dibuat saat startup
- Jika ingin tambah user baru, gunakan endpoint `/api/auth/register`
- Password saat ini plain text (untuk demo). Dalam production, gunakan BCrypt!
- Database schema auto-created oleh Hibernate DDL-auto=update

---

**Last Updated**: 30 Januari 2026
**Tested by**: Development Team
**Status**: ✅ Ready for Testing
