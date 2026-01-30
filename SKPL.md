# SPESIFIKASI KEBUTUHAN PERANGKAT LUNAK (SKPL)

## Sistem Manajemen Inventori Barang

**Versi Dokumen:** 1.0  
**Tanggal:** 30 Januari 2026  
**Nama Aplikasi:** Inventory Management System

---

## 1. PENDAHULUAN

### 1.1 Tujuan Dokumen

Dokumen SKPL ini dibuat untuk mendefinisikan kebutuhan perangkat lunak Sistem Manajemen Inventori Barang secara lengkap dan terstruktur. Dokumen ini menjadi acuan bagi pengembang dalam membangun sistem dan sebagai dasar pengujian aplikasi.

### 1.2 Lingkup Produk

Sistem Manajemen Inventori Barang adalah aplikasi berbasis web yang digunakan untuk mengelola data inventori produk, kategori, supplier, dan stok barang. Sistem ini dirancang untuk membantu pengguna dalam:

- Mengelola data produk beserta informasi harga dan deskripsinya
- Mengkategorikan produk ke dalam berbagai kategori
- Mengelola data supplier/pemasok
- Memonitor stok barang secara real-time
- Mendeteksi produk dengan stok rendah atau habis

### 1.3 Definisi dan Istilah

| Istilah | Definisi |
|---------|----------|
| SKU | Stock Keeping Unit - kode unik untuk identifikasi produk |
| CRUD | Create, Read, Update, Delete - operasi dasar pada data |
| REST API | Representational State Transfer API - antarmuka pemrograman berbasis HTTP |
| Low Stock | Kondisi ketika jumlah stok sama dengan atau di bawah minimum stok |
| Out of Stock | Kondisi ketika jumlah stok bernilai 0 |
| Dashboard | Halaman utama yang menampilkan ringkasan informasi sistem |

### 1.4 Referensi

- Spring Boot 4.0.2 Documentation
- PostgreSQL Documentation
- Java 25 Documentation

---

## 2. DESKRIPSI UMUM SISTEM

### 2.1 Perspektif Produk

Sistem Manajemen Inventori Barang merupakan aplikasi standalone yang terdiri dari:

- **Backend**: REST API menggunakan Spring Boot dengan database PostgreSQL
- **Frontend**: Aplikasi web berbasis HTML dan JavaScript (Vanilla JS)
- **Arsitektur**: Model-View-Controller (MVC)

### 2.2 Fungsi Produk

Sistem memiliki fungsi-fungsi utama sebagai berikut:

1. **Manajemen Kategori**: Mengelola kategori produk
2. **Manajemen Supplier**: Mengelola data pemasok/supplier
3. **Manajemen Produk**: Mengelola data produk dengan relasi ke kategori dan supplier
4. **Manajemen Stok**: Mengelola dan memonitor stok barang
5. **Dashboard**: Menampilkan ringkasan dan alert stok

### 2.3 Karakteristik Pengguna

| Pengguna | Deskripsi | Kemampuan |
|----------|-----------|-----------|
| Admin/Staff Inventori | Pengguna utama sistem yang bertanggung jawab mengelola inventori | Mengelola semua modul (kategori, supplier, produk, stok) |

### 2.4 Batasan Sistem

- Sistem membutuhkan koneksi database PostgreSQL
- Sistem berjalan pada port 8080 (default)
- Frontend membutuhkan browser modern dengan dukungan JavaScript

### 2.5 Asumsi dan Ketergantungan

- PostgreSQL sudah terinstall dan dapat diakses
- Java Runtime Environment tersedia pada server
- Pengguna memiliki akses jaringan ke server aplikasi

---

## 3. KEBUTUHAN FUNGSIONAL

### 3.1 Modul Kategori (Category)

#### KF-CAT-01: Melihat Daftar Kategori
- **Deskripsi**: Sistem harus dapat menampilkan semua kategori yang tersimpan dalam database
- **Input**: Tidak ada
- **Output**: Daftar kategori beserta informasi ID, nama, deskripsi, dan jumlah produk

#### KF-CAT-02: Melihat Detail Kategori
- **Deskripsi**: Sistem harus dapat menampilkan detail satu kategori berdasarkan ID
- **Input**: ID kategori
- **Output**: Detail kategori meliputi ID, nama, deskripsi, tanggal dibuat, dan tanggal diupdate

#### KF-CAT-03: Menambah Kategori Baru
- **Deskripsi**: Sistem harus dapat menyimpan kategori baru ke database
- **Input**: Nama kategori (wajib), deskripsi (opsional)
- **Output**: Data kategori yang berhasil disimpan
- **Validasi**: Nama kategori tidak boleh kosong dan harus unik

#### KF-CAT-04: Mengubah Data Kategori
- **Deskripsi**: Sistem harus dapat mengubah data kategori yang sudah ada
- **Input**: ID kategori, nama baru, deskripsi baru
- **Output**: Data kategori yang sudah diupdate
- **Validasi**: Nama kategori tidak boleh kosong

#### KF-CAT-05: Menghapus Kategori
- **Deskripsi**: Sistem harus dapat menghapus kategori dari database
- **Input**: ID kategori
- **Output**: Konfirmasi penghapusan
- **Catatan**: Penghapusan kategori akan menghapus semua produk terkait (cascade delete)

---

### 3.2 Modul Supplier

#### KF-SUP-01: Melihat Daftar Supplier
- **Deskripsi**: Sistem harus dapat menampilkan semua supplier yang tersimpan dalam database
- **Input**: Tidak ada
- **Output**: Daftar supplier beserta informasi ID, nama, alamat, telepon, email, dan deskripsi

#### KF-SUP-02: Melihat Detail Supplier
- **Deskripsi**: Sistem harus dapat menampilkan detail satu supplier berdasarkan ID
- **Input**: ID supplier
- **Output**: Detail supplier lengkap

#### KF-SUP-03: Mencari Supplier
- **Deskripsi**: Sistem harus dapat mencari supplier berdasarkan nama
- **Input**: Kata kunci pencarian nama
- **Output**: Daftar supplier yang sesuai dengan kata kunci

#### KF-SUP-04: Menambah Supplier Baru
- **Deskripsi**: Sistem harus dapat menyimpan supplier baru ke database
- **Input**: Nama (wajib), alamat (wajib), nomor telepon (opsional), email (opsional), deskripsi (opsional)
- **Output**: Data supplier yang berhasil disimpan
- **Validasi**: 
  - Nama supplier tidak boleh kosong
  - Alamat tidak boleh kosong
  - Format email harus valid (jika diisi)

#### KF-SUP-05: Mengubah Data Supplier
- **Deskripsi**: Sistem harus dapat mengubah data supplier yang sudah ada
- **Input**: ID supplier, data baru
- **Output**: Data supplier yang sudah diupdate

#### KF-SUP-06: Menghapus Supplier
- **Deskripsi**: Sistem harus dapat menghapus supplier dari database
- **Input**: ID supplier
- **Output**: Konfirmasi penghapusan
- **Catatan**: Penghapusan supplier akan menghapus semua produk terkait (cascade delete)

---

### 3.3 Modul Produk (Product)

#### KF-PRD-01: Melihat Daftar Produk
- **Deskripsi**: Sistem harus dapat menampilkan semua produk yang tersimpan dalam database
- **Input**: Tidak ada
- **Output**: Daftar produk beserta informasi ID, nama, SKU, deskripsi, kategori, supplier, harga, dan status stok

#### KF-PRD-02: Melihat Detail Produk
- **Deskripsi**: Sistem harus dapat menampilkan detail satu produk berdasarkan ID
- **Input**: ID produk
- **Output**: Detail produk lengkap termasuk informasi stok

#### KF-PRD-03: Mencari Produk Berdasarkan Nama
- **Deskripsi**: Sistem harus dapat mencari produk berdasarkan nama
- **Input**: Kata kunci pencarian nama
- **Output**: Daftar produk yang sesuai dengan kata kunci

#### KF-PRD-04: Melihat Produk Berdasarkan Kategori
- **Deskripsi**: Sistem harus dapat memfilter produk berdasarkan kategori
- **Input**: ID kategori
- **Output**: Daftar produk dalam kategori tersebut

#### KF-PRD-05: Melihat Produk Berdasarkan Supplier
- **Deskripsi**: Sistem harus dapat memfilter produk berdasarkan supplier
- **Input**: ID supplier
- **Output**: Daftar produk dari supplier tersebut

#### KF-PRD-06: Menambah Produk Baru
- **Deskripsi**: Sistem harus dapat menyimpan produk baru ke database
- **Input**: 
  - Nama produk (wajib)
  - SKU (opsional, auto-generate jika kosong)
  - Deskripsi (opsional)
  - Kategori (wajib)
  - Supplier (wajib)
  - Harga (wajib)
- **Output**: Data produk yang berhasil disimpan beserta stok awal
- **Validasi**:
  - Nama produk tidak boleh kosong
  - Harga harus lebih dari 0
  - Kategori harus valid
  - Supplier harus valid
  - SKU harus unik

#### KF-PRD-07: Mengubah Data Produk
- **Deskripsi**: Sistem harus dapat mengubah data produk yang sudah ada
- **Input**: ID produk, data baru
- **Output**: Data produk yang sudah diupdate

#### KF-PRD-08: Menghapus Produk
- **Deskripsi**: Sistem harus dapat menghapus produk dari database
- **Input**: ID produk
- **Output**: Konfirmasi penghapusan
- **Catatan**: Penghapusan produk akan menghapus data stok terkait (cascade delete)

---

### 3.4 Modul Stok (Stock)

#### KF-STK-01: Melihat Daftar Stok
- **Deskripsi**: Sistem harus dapat menampilkan semua data stok yang tersimpan dalam database
- **Input**: Tidak ada
- **Output**: Daftar stok beserta informasi ID, nama produk, jumlah stok, minimum stok, dan tanggal update

#### KF-STK-02: Melihat Detail Stok
- **Deskripsi**: Sistem harus dapat menampilkan detail stok berdasarkan ID
- **Input**: ID stok
- **Output**: Detail stok lengkap

#### KF-STK-03: Melihat Stok Berdasarkan Produk
- **Deskripsi**: Sistem harus dapat menampilkan stok berdasarkan ID produk
- **Input**: ID produk
- **Output**: Data stok produk tersebut

#### KF-STK-04: Melihat Produk dengan Stok Rendah
- **Deskripsi**: Sistem harus dapat menampilkan daftar produk yang memiliki stok rendah (quantity ≤ minimumStock)
- **Input**: Tidak ada
- **Output**: Daftar produk dengan stok rendah beserta informasi stok

#### KF-STK-05: Melihat Produk yang Habis
- **Deskripsi**: Sistem harus dapat menampilkan daftar produk yang stoknya habis (quantity = 0)
- **Input**: Tidak ada
- **Output**: Daftar produk yang habis stok

#### KF-STK-06: Mengubah Data Stok
- **Deskripsi**: Sistem harus dapat mengubah data stok secara langsung
- **Input**: ID stok, jumlah stok baru, minimum stok baru
- **Output**: Data stok yang sudah diupdate
- **Validasi**: Jumlah stok tidak boleh kurang dari 0

#### KF-STK-07: Menambah Stok
- **Deskripsi**: Sistem harus dapat menambah jumlah stok produk (restock)
- **Input**: ID produk, jumlah yang ditambahkan
- **Output**: Data stok yang sudah diupdate dengan tanggal restock
- **Validasi**: Jumlah yang ditambahkan harus lebih dari 0

#### KF-STK-08: Mengurangi Stok
- **Deskripsi**: Sistem harus dapat mengurangi jumlah stok produk
- **Input**: ID produk, jumlah yang dikurangi
- **Output**: Data stok yang sudah diupdate
- **Validasi**: 
  - Jumlah yang dikurangi harus lebih dari 0
  - Stok hasil pengurangan tidak boleh kurang dari 0

---

### 3.5 Modul Dashboard

#### KF-DSH-01: Menampilkan Statistik Ringkasan
- **Deskripsi**: Sistem harus dapat menampilkan statistik ringkasan pada halaman dashboard
- **Output**: 
  - Total jumlah produk
  - Total jumlah kategori
  - Total jumlah supplier
  - Jumlah item dengan stok rendah

#### KF-DSH-02: Menampilkan Alert Stok Rendah
- **Deskripsi**: Sistem harus dapat menampilkan tabel produk dengan stok rendah pada dashboard
- **Output**: Tabel berisi nama produk, stok saat ini, dan minimum stok

#### KF-DSH-03: Menampilkan Alert Stok Habis
- **Deskripsi**: Sistem harus dapat menampilkan tabel produk yang stoknya habis pada dashboard
- **Output**: Tabel berisi nama produk, SKU, dan kategori

#### KF-DSH-04: Menampilkan Produk Terbaru
- **Deskripsi**: Sistem harus dapat menampilkan 10 produk terakhir yang ditambahkan
- **Output**: Tabel produk terbaru dengan informasi lengkap

---

## 4. USE CASE

### 4.1 Skenario Penggunaan Sistem Manajemen Inventori

**Deskripsi**: Alur penggunaan sistem inventori dari awal hingga pengelolaan stok barang secara lengkap.

**Aktor**: Admin/Staff Inventori

**Alur Utama**:

1. **Persiapan Data Master**
   - Pengguna membuka aplikasi Sistem Manajemen Inventori
   - Sistem menampilkan halaman Dashboard dengan statistik awal (masih kosong)
   - Pengguna membuka halaman Kategori untuk mempersiapkan kategori produk
   - Pengguna menekan tombol "Tambah Kategori" dan mengisi form (nama: "Elektronik", deskripsi: "Perangkat elektronik")
   - Sistem menyimpan kategori baru dan menampilkan notifikasi sukses
   - Pengguna menambahkan kategori lain seperti "Furniture", "Alat Tulis", dll sesuai kebutuhan
   - Pengguna membuka halaman Supplier untuk mendaftarkan pemasok
   - Pengguna menekan tombol "Tambah Supplier" dan mengisi data (nama: "PT Elektronik Jaya", alamat, telepon, email)
   - Sistem menyimpan supplier baru dan menampilkan notifikasi sukses
   - Pengguna menambahkan beberapa supplier lainnya

2. **Pengelolaan Produk**
   - Pengguna membuka halaman Produk untuk menambahkan produk baru
   - Pengguna menekan tombol "Tambah Produk"
   - Sistem menampilkan form modal dengan dropdown kategori dan supplier yang sudah tersedia
   - Pengguna mengisi data produk (nama: "Laptop ASUS", kategori: "Elektronik", supplier: "PT Elektronik Jaya", harga: 7500000)
   - Sistem melakukan validasi data dan membuat SKU otomatis (misalnya: "PROD-001-2026")
   - Sistem menyimpan produk baru beserta data stok awal (quantity: 0, minimum stock: 10)
   - Sistem menampilkan notifikasi sukses dan menampilkan produk dalam daftar
   - Pengguna menambahkan beberapa produk lain dengan cara yang sama
   - Jika perlu mencari produk tertentu, pengguna dapat menggunakan fitur pencarian atau filter berdasarkan kategori/supplier

3. **Pengelolaan Stok Barang**
   - Pengguna membuka halaman Stok untuk memonitor dan mengelola stok
   - Sistem menampilkan daftar semua produk dengan informasi stok (masih 0 untuk produk baru)
   - Produk ditampilkan dengan badge merah (Out of Stock) karena stok masih 0
   - Pengguna menekan tombol "Tambah Stok" pada produk "Laptop ASUS" untuk melakukan restock
   - Sistem menampilkan form input jumlah stok yang akan ditambahkan
   - Pengguna mengisi jumlah: 50 unit
   - Sistem menambahkan stok menjadi 50 dan mencatat tanggal restock
   - Status badge berubah menjadi hijau (In Stock) karena stok di atas minimum
   - Pengguna melakukan restock untuk produk-produk lainnya

4. **Monitoring Dashboard**
   - Pengguna kembali ke halaman Dashboard
   - Sistem menampilkan statistik terkini:
     - Total Produk: 15
     - Total Kategori: 4
     - Total Supplier: 5
     - Low Stock Items: 0
   - Sistem menampilkan 10 produk terbaru yang ditambahkan
   - Sistem menampilkan tabel produk dengan stok rendah (kosong jika semua stok mencukupi)
   - Sistem menampilkan tabel produk yang habis (kosong jika sudah di-restock)

5. **Operasi Harian**
   - Ketika terjadi penjualan, pengguna membuka halaman Stok
   - Pengguna menekan tombol "Kurangi Stok" pada produk yang terjual
   - Sistem menampilkan form input jumlah yang akan dikurangi
   - Pengguna mengisi jumlah: 5 unit
   - Sistem memvalidasi bahwa pengurangan tidak melebihi stok tersedia
   - Sistem mengurangi stok dan menampilkan notifikasi sukses
   - Jika stok setelah pengurangan mencapai atau di bawah minimum stock, badge berubah menjadi kuning (Low Stock)
   - Dashboard secara otomatis menampilkan alert produk tersebut di tabel "Low Stock Alert"

6. **Manajemen Alert Stok**
   - Pengguna memeriksa Dashboard dan melihat ada 3 produk di tabel "Low Stock Alert"
   - Pengguna dapat langsung mengklik produk tersebut untuk melakukan restock
   - Sistem mengarahkan ke halaman Stok dengan produk terpilih
   - Pengguna melakukan restock sesuai kebutuhan
   - Alert akan hilang dari dashboard setelah stok kembali normal

7. **Update Data**
   - Jika ada perubahan harga produk, pengguna membuka halaman Produk
   - Pengguna menekan tombol "Edit" pada produk yang ingin diubah
   - Sistem menampilkan form dengan data saat ini
   - Pengguna mengubah harga atau data lainnya
   - Sistem memvalidasi dan menyimpan perubahan
   - Jika ada kategori yang perlu diubah, pengguna membuka halaman Kategori dan melakukan edit
   - Jika supplier tidak aktif lagi, pengguna dapat menghapusnya dari halaman Supplier
   - Sistem menampilkan konfirmasi sebelum menghapus karena akan mempengaruhi produk terkait

8. **Pencarian dan Filter**
   - Ketika mencari produk tertentu, pengguna menggunakan kolom pencarian di halaman Produk
   - Pengguna mengetik nama atau SKU produk
   - Sistem memfilter dan menampilkan hasil pencarian secara real-time
   - Pengguna dapat menggunakan filter kategori atau supplier untuk mempersempit hasil
   - Pengguna dapat menekan tombol "Reset" untuk menghapus semua filter

**Alur Alternatif**:

- **Jika validasi gagal saat input data**: Sistem menampilkan pesan error yang spesifik (misalnya: "Nama produk tidak boleh kosong", "Format email tidak valid", "Harga harus lebih dari 0")
- **Jika pengurangan stok melebihi jumlah tersedia**: Sistem menampilkan pesan error "Stok tidak mencukupi" dan membatalkan operasi
- **Jika nama kategori atau SKU sudah digunakan**: Sistem menampilkan pesan "Nama kategori sudah ada" atau "SKU sudah digunakan"
- **Jika menghapus kategori/supplier yang memiliki produk**: Sistem menampilkan dialog konfirmasi dengan peringatan bahwa semua produk terkait akan ikut terhapus
- **Jika tidak ada data**: Sistem menampilkan pesan informatif "Tidak ada data" untuk setiap modul yang kosong

**Hasil Akhir**:
- Database terisi dengan data master (kategori, supplier) yang terorganisir
- Semua produk tercatat dengan informasi lengkap dan relasi yang benar
- Stok barang termonitor dengan baik melalui dashboard
- Sistem memberikan alert otomatis untuk stok rendah dan habis
- Pengguna dapat mengelola inventori secara efisien dan real-time

---

## 5. DATA FLOW DIAGRAM (DFD)

### 5.1 DFD Level 0 (Context Diagram)

DFD Level 0 menggambarkan sistem secara keseluruhan sebagai satu proses tunggal dengan entitas eksternal yang berinteraksi dengan sistem.

```
                    +------------------------------------------+
                    |                                          |
                    |   Data Kategori                          |
                    |   Data Supplier                          |
                    |   Data Produk                            |
                    |   Data Stok                              |
                    |   Request Pencarian/Filter               |
                    |                                          |
                    v                                          |
        +---------------------------+                          |
        |                           |                          |
        |   Admin/Staff Inventori   |                          |
        |                           |                          |
        +---------------------------+                          |
                    |                                          |
                    |   Daftar Kategori                        |
                    |   Daftar Supplier                        |
                    |   Daftar Produk                          |
                    |   Informasi Stok                         |
                    |   Alert Stok Rendah/Habis                |
                    |   Statistik Dashboard                    |
                    |   Notifikasi Sukses/Error                |
                    |                                          |
                    +----------------------------------------->+
                              
                    [SISTEM MANAJEMEN INVENTORI BARANG]
```

**Komponen:**

**Entitas Eksternal:**
- **Admin/Staff Inventori**: Pengguna yang berinteraksi dengan sistem untuk mengelola inventori

**Proses:**
- **Sistem Manajemen Inventori Barang**: Sistem utama yang mengelola seluruh proses inventori

**Aliran Data Masuk (dari Admin ke Sistem):**
- Data Kategori (tambah, ubah, hapus)
- Data Supplier (tambah, ubah, hapus)
- Data Produk (tambah, ubah, hapus)
- Data Stok (tambah, kurangi, update)
- Request Pencarian/Filter

**Aliran Data Keluar (dari Sistem ke Admin):**
- Daftar Kategori
- Daftar Supplier
- Daftar Produk
- Informasi Stok
- Alert Stok Rendah/Habis
- Statistik Dashboard
- Notifikasi Sukses/Error

---

### 5.2 DFD Level 1

DFD Level 1 menggambarkan pemecahan sistem menjadi proses-proses utama beserta data store yang digunakan.

```
                            Admin/Staff Inventori
                                     |
        +----------------------------+----------------------------+
        |                            |                            |
        | Data Kategori       Data Supplier                Data Produk
        |                            |                            |
        v                            v                            |
   +---------+                  +---------+                       |
   |   1.0   |                  |   2.0   |                       |
   | Kelola  |                  | Kelola  |                       |
   |Kategori |                  |Supplier |                       |
   +---------+                  +---------+                       |
        |                            |                            |
        | Simpan/Update/Hapus        | Simpan/Update/Hapus        |
        v                            v                            v
   [D1: Category]              [D2: Supplier]              +---------+
        |                            |                      |   3.0   |
        | Data Kategori              | Data Supplier        | Kelola  |
        |                            |                      | Produk  |
        |                            +--------------------> +---------+
        +-----------------------------------------------------> |
                                                              |
                                          Simpan/Update/Hapus |
                                                              v
                                                        [D3: Product]
                                                              |
                                                              | Data Produk
                                                              |
        +-----------------------------------------------------+
        |
        | Data Produk                          Data Stok
        |                                           |
        v                                           v
   +---------+                                 +---------+
   |   4.0   |                                 |   5.0   |
   | Kelola  |<--------------------------------| Generate|
   |  Stok   |    Request Stok per Produk      |Dashboard|
   +---------+                                 +---------+
        |                                           ^
        | Simpan/Update Stok                        |
        v                                           |
   [D4: Stock]                                      |
        |                                           |
        | Info Stok Low/Out                         |
        +-------------------------------------------+
        |
        | Daftar/Info/Alert/Statistik
        |
        v
   Admin/Staff Inventori
```

**Komponen:**

**Entitas Eksternal:**
- **Admin/Staff Inventori**: Pengguna sistem

**Proses Utama:**

**1.0 Kelola Kategori**
- Input: Data kategori (nama, deskripsi) dari Admin
- Proses: Validasi, simpan, update, atau hapus data kategori
- Output: Daftar kategori ke Admin, data kategori ke D1
- Data Store: D1 (Category)

**2.0 Kelola Supplier**
- Input: Data supplier (nama, alamat, telepon, email, deskripsi) dari Admin
- Proses: Validasi, simpan, update, atau hapus data supplier
- Output: Daftar supplier ke Admin, data supplier ke D2
- Data Store: D2 (Supplier)

**3.0 Kelola Produk**
- Input: Data produk (nama, SKU, deskripsi, harga, kategori, supplier) dari Admin
- Proses: Validasi, generate SKU, simpan, update, atau hapus data produk
- Output: Daftar produk ke Admin, data produk ke D3
- Data Store: D3 (Product)
- Referensi: Membaca data dari D1 (Category) dan D2 (Supplier)

**4.0 Kelola Stok**
- Input: Data stok (quantity, minimum stock, operasi tambah/kurangi) dari Admin
- Proses: Validasi, update stok, catat tanggal restock
- Output: Informasi stok ke Admin, data stok ke D4
- Data Store: D4 (Stock)
- Referensi: Membaca data dari D3 (Product)

**5.0 Generate Dashboard**
- Input: Request dari Admin untuk melihat dashboard
- Proses: 
  - Hitung statistik (total produk, kategori, supplier)
  - Deteksi low stock (quantity ≤ minimum stock)
  - Deteksi out of stock (quantity = 0)
  - Ambil produk terbaru
- Output: Statistik dan alert ke Admin
- Data Store: Membaca dari D1 (Category), D2 (Supplier), D3 (Product), D4 (Stock)

**Data Store:**

| ID | Nama | Deskripsi |
|----|------|-----------|
| D1 | Category | Menyimpan data kategori produk |
| D2 | Supplier | Menyimpan data supplier/pemasok |
| D3 | Product | Menyimpan data produk dengan relasi ke kategori dan supplier |
| D4 | Stock | Menyimpan data stok untuk setiap produk |

**Aliran Data Utama:**

1. **Admin → Proses 1.0**: Input data kategori
2. **Proses 1.0 → D1**: Simpan/update/hapus kategori
3. **D1 → Proses 1.0**: Baca data kategori
4. **Proses 1.0 → Admin**: Tampilkan daftar kategori

5. **Admin → Proses 2.0**: Input data supplier
6. **Proses 2.0 → D2**: Simpan/update/hapus supplier
7. **D2 → Proses 2.0**: Baca data supplier
8. **Proses 2.0 → Admin**: Tampilkan daftar supplier

9. **Admin → Proses 3.0**: Input data produk
10. **D1 → Proses 3.0**: Referensi kategori
11. **D2 → Proses 3.0**: Referensi supplier
12. **Proses 3.0 → D3**: Simpan/update/hapus produk
13. **D3 → Proses 3.0**: Baca data produk
14. **Proses 3.0 → Admin**: Tampilkan daftar produk

15. **Admin → Proses 4.0**: Input operasi stok
16. **D3 → Proses 4.0**: Referensi produk
17. **Proses 4.0 → D4**: Simpan/update stok
18. **D4 → Proses 4.0**: Baca data stok
19. **Proses 4.0 → Admin**: Tampilkan informasi stok

20. **Admin → Proses 5.0**: Request dashboard
21. **D1, D2, D3, D4 → Proses 5.0**: Baca semua data untuk statistik
22. **Proses 5.0 → Admin**: Tampilkan dashboard dengan statistik dan alert

---

## 6. KEBUTUHAN NON-FUNGSIONAL

### 6.1 Kebutuhan Performa

| ID | Kebutuhan | Keterangan |
|----|-----------|------------|
| KNF-01 | Response time API maksimal 2 detik | Untuk operasi CRUD standar |
| KNF-02 | Sistem dapat menangani minimal 100 request per menit | Kapasitas beban normal |

### 6.2 Kebutuhan Keamanan

| ID | Kebutuhan | Keterangan |
|----|-----------|------------|
| KNF-03 | CORS dikonfigurasi untuk keamanan | Hanya domain tertentu yang dapat mengakses API |
| KNF-04 | Validasi input pada server | Mencegah data tidak valid masuk ke database |

### 6.3 Kebutuhan Keandalan

| ID | Kebutuhan | Keterangan |
|----|-----------|------------|
| KNF-05 | Transaksi database bersifat atomik | Menggunakan @Transactional |
| KNF-06 | Validasi integritas data | Foreign key dan constraint di database |

### 6.4 Kebutuhan Usability

| ID | Kebutuhan | Keterangan |
|----|-----------|------------|
| KNF-07 | Interface responsif | Dapat diakses dari berbagai ukuran layar |
| KNF-08 | Notifikasi yang jelas | Pengguna mendapat feedback untuk setiap aksi |
| KNF-09 | Status stok visual | Menggunakan badge berwarna untuk memudahkan identifikasi |

### 6.5 Kebutuhan Portabilitas

| ID | Kebutuhan | Keterangan |
|----|-----------|------------|
| KNF-10 | Cross-browser support | Mendukung Chrome, Firefox, Edge, Safari terbaru |
| KNF-11 | Dapat di-deploy di berbagai OS | Windows, Linux, macOS |

---

## 7. MODEL DATA

### 7.1 Entity Relationship

#### Tabel Category
| Field | Tipe Data | Keterangan |
|-------|-----------|------------|
| id | BIGINT | Primary Key, Auto Increment |
| name | VARCHAR(255) | Nama kategori, NOT NULL, UNIQUE |
| description | VARCHAR(500) | Deskripsi kategori |
| created_at | TIMESTAMP | Tanggal dibuat |
| updated_at | TIMESTAMP | Tanggal diupdate |

#### Tabel Supplier
| Field | Tipe Data | Keterangan |
|-------|-----------|------------|
| id | BIGINT | Primary Key, Auto Increment |
| name | VARCHAR(255) | Nama supplier, NOT NULL |
| address | VARCHAR(255) | Alamat, NOT NULL |
| phone_number | VARCHAR(50) | Nomor telepon |
| email | VARCHAR(255) | Email (format valid) |
| description | VARCHAR(500) | Deskripsi |
| created_at | TIMESTAMP | Tanggal dibuat |
| updated_at | TIMESTAMP | Tanggal diupdate |

#### Tabel Product
| Field | Tipe Data | Keterangan |
|-------|-----------|------------|
| id | BIGINT | Primary Key, Auto Increment |
| name | VARCHAR(255) | Nama produk, NOT NULL |
| sku | VARCHAR(50) | Stock Keeping Unit, UNIQUE, NOT NULL |
| description | VARCHAR(1000) | Deskripsi produk |
| price | DECIMAL(12,2) | Harga, NOT NULL, > 0 |
| category_id | BIGINT | Foreign Key ke Category |
| supplier_id | BIGINT | Foreign Key ke Supplier |
| created_at | TIMESTAMP | Tanggal dibuat |
| updated_at | TIMESTAMP | Tanggal diupdate |

#### Tabel Stock
| Field | Tipe Data | Keterangan |
|-------|-----------|------------|
| id | BIGINT | Primary Key, Auto Increment |
| quantity | INTEGER | Jumlah stok, NOT NULL, >= 0, default 0 |
| minimum_stock | INTEGER | Minimum stok, >= 0, default 10 |
| product_id | BIGINT | Foreign Key ke Product, UNIQUE |
| last_restock_date | TIMESTAMP | Tanggal restock terakhir |
| updated_at | TIMESTAMP | Tanggal diupdate |

### 7.2 Relasi Antar Tabel

- **Category (1) → (N) Product**: Satu kategori memiliki banyak produk
- **Supplier (1) → (N) Product**: Satu supplier memasok banyak produk
- **Product (1) → (1) Stock**: Satu produk memiliki satu record stok

---

## 8. ANTARMUKA SISTEM

### 8.1 API Endpoints

#### Kategori
| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | /api/categories | Mengambil semua kategori |
| GET | /api/categories/{id} | Mengambil kategori berdasarkan ID |
| POST | /api/categories | Membuat kategori baru |
| PUT | /api/categories/{id} | Mengupdate kategori |
| DELETE | /api/categories/{id} | Menghapus kategori |

#### Supplier
| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | /api/suppliers | Mengambil semua supplier |
| GET | /api/suppliers/{id} | Mengambil supplier berdasarkan ID |
| GET | /api/suppliers/search?name={name} | Mencari supplier berdasarkan nama |
| POST | /api/suppliers | Membuat supplier baru |
| PUT | /api/suppliers/{id} | Mengupdate supplier |
| DELETE | /api/suppliers/{id} | Menghapus supplier |

#### Produk
| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | /api/products | Mengambil semua produk |
| GET | /api/products/{id} | Mengambil produk berdasarkan ID |
| GET | /api/products/search?name={name} | Mencari produk berdasarkan nama |
| GET | /api/products/category/{categoryId} | Mengambil produk berdasarkan kategori |
| GET | /api/products/supplier/{supplierId} | Mengambil produk berdasarkan supplier |
| POST | /api/products | Membuat produk baru |
| PUT | /api/products/{id} | Mengupdate produk |
| DELETE | /api/products/{id} | Menghapus produk |

#### Stok
| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | /api/stocks | Mengambil semua stok |
| GET | /api/stocks/{id} | Mengambil stok berdasarkan ID |
| GET | /api/stocks/product/{productId} | Mengambil stok berdasarkan produk |
| GET | /api/stocks/low-stock | Mengambil produk dengan stok rendah |
| GET | /api/stocks/out-of-stock | Mengambil produk yang habis |
| PUT | /api/stocks/{id} | Mengupdate stok |
| POST | /api/stocks/product/{productId}/add | Menambah stok |
| POST | /api/stocks/product/{productId}/reduce | Mengurangi stok |

### 8.2 Format Response

Semua response API menggunakan format JSON dengan struktur yang konsisten.

---

## 9. LAMPIRAN

### 9.1 Teknologi yang Digunakan

| Komponen | Teknologi | Versi |
|----------|-----------|-------|
| Backend Framework | Spring Boot | 4.0.2 |
| Bahasa Pemrograman | Java | 25 |
| Database | PostgreSQL | - |
| ORM | Spring Data JPA | - |
| Build Tool | Maven | - |
| Frontend | HTML, CSS, JavaScript | - |
| HTTP Client | Axios | - |
| CSS Framework | Bootstrap | 5.x |

### 9.2 Konfigurasi Sistem

- **Port Default**: 8080
- **Database Name**: inventory_db
- **Connection Pool**: HikariCP (default Spring Boot)

---

**Dokumen ini dibuat sebagai panduan pengembangan dan dapat diperbarui sesuai kebutuhan project.**
