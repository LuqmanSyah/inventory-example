package com.example.inventoryexample.config;

import com.example.inventoryexample.entity.*;
import com.example.inventoryexample.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializationConfig {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    
    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            // === INITIALIZE USERS ===
            initializeUsers();
            
            // === INITIALIZE CATEGORIES ===
            initializeCategories();
            
            // === INITIALIZE SUPPLIERS ===
            initializeSuppliers();
            
            // === INITIALIZE PRODUCTS ===
            initializeProducts();
            
            System.out.println("\n✓✓✓ All dummy data initialized successfully! ✓✓✓\n");
        };
    }
    
    private void initializeUsers() {
        // Create default SUPER ADMIN user jika belum ada (hanya boleh 1)
        if (!userRepository.existsByRole(User.UserRole.SUPER_ADMIN)) {
            if (!userRepository.existsByUsername("superadmin")) {
                User superAdmin = new User();
                superAdmin.setUsername("superadmin");
                superAdmin.setPassword(passwordEncoder.encode("superadmin123"));
                superAdmin.setFullName("Super Administrator");
                superAdmin.setEmail("superadmin@inventori.com");
                superAdmin.setRole(User.UserRole.SUPER_ADMIN);
                superAdmin.setIsActive(true);
                userRepository.save(superAdmin);
                System.out.println("✓ Super Admin user created: superadmin / superadmin123");
            }
        }
        
        // Update existing admin user to regular ADMIN (bukan SUPER_ADMIN)
        if (userRepository.existsByUsername("admin")) {
            userRepository.findByUsername("admin").ifPresent(admin -> {
                if (admin.getRole() == User.UserRole.SUPER_ADMIN) {
                    // Jika sudah ada super admin lain, ubah jadi admin biasa
                    if (userRepository.countByRole(User.UserRole.SUPER_ADMIN) > 1) {
                        admin.setRole(User.UserRole.ADMIN);
                    }
                }
                if (!admin.getPassword().startsWith("$2a$") && !admin.getPassword().startsWith("$2b$")) {
                    admin.setPassword(passwordEncoder.encode("admin123"));
                }
                userRepository.save(admin);
                System.out.println("✓ Admin user updated: admin / admin123");
            });
        } else {
            // Create default admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrator");
            admin.setEmail("admin@inventori.com");
            admin.setRole(User.UserRole.ADMIN);
            admin.setIsActive(true);
            userRepository.save(admin);
            System.out.println("✓ Admin user created: admin / admin123");
        }
        
        // Create default staff user jika belum ada
        if (!userRepository.existsByUsername("staff")) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setPassword(passwordEncoder.encode("staff123"));
            staff.setFullName("Staff Member");
            staff.setEmail("staff@inventori.com");
            staff.setRole(User.UserRole.STAFF);
            staff.setIsActive(true);
            userRepository.save(staff);
            System.out.println("✓ Staff user created: staff / staff123");
        } else {
            // Update existing staff password to hashed version if it's plain text
            userRepository.findByUsername("staff").ifPresent(staff -> {
                if (!staff.getPassword().startsWith("$2a$") && !staff.getPassword().startsWith("$2b$")) {
                    staff.setPassword(passwordEncoder.encode("staff123"));
                    userRepository.save(staff);
                    System.out.println("✓ Staff password updated to hashed version");
                }
            });
        }
        
        // Create additional users (john_admin is now regular ADMIN, not SUPER_ADMIN)
        createUserIfNotExists("john_admin", "John Doe", "john@inventori.com", User.UserRole.ADMIN);
        createUserIfNotExists("sarah_staff", "Sarah Wilson", "sarah@inventori.com", User.UserRole.STAFF);
        createUserIfNotExists("mike_staff", "Mike Johnson", "mike@inventori.com", User.UserRole.STAFF);
    }
    
    private void createUserIfNotExists(String username, String fullName, String email, User.UserRole role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode("password123"));
            user.setFullName(fullName);
            user.setEmail(email);
            user.setRole(role);
            user.setIsActive(true);
            userRepository.save(user);
            System.out.println("✓ User created: " + username + " / password123");
        }
    }
    
    private void initializeCategories() {
        createCategoryIfNotExists("Elektronik", "Peralatan dan komponen elektronik");
        createCategoryIfNotExists("Komputer & Laptop", "Komputer, laptop, dan aksesori");
        createCategoryIfNotExists("Smartphone & Tablet", "Smartphone, tablet, dan aksesori");
        createCategoryIfNotExists("Peralatan Kantor", "ATK dan peralatan kantor");
        createCategoryIfNotExists("Furniture", "Meja, kursi, dan furniture kantor");
        createCategoryIfNotExists("Networking", "Router, switch, dan peralatan jaringan");
        createCategoryIfNotExists("Audio & Video", "Speaker, headphone, kamera, proyektor");
        createCategoryIfNotExists("Gaming", "Console, controller, dan aksesori gaming");
        createCategoryIfNotExists("Aksesoris", "Berbagai aksesoris teknologi");
        createCategoryIfNotExists("Software", "Software dan lisensi");
        System.out.println("✓ Categories initialized");
    }
    
    private void createCategoryIfNotExists(String name, String description) {
        if (!categoryRepository.existsByName(name)) {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            categoryRepository.save(category);
        }
    }
    
    private void initializeSuppliers() {
        createSupplierIfNotExists(
            "PT Teknologi Maju",
            "Jl. Sudirman No. 123, Jakarta Pusat",
            "02155512340",
            "info@tekmaju.com",
            "Supplier komputer dan laptop terpercaya"
        );
        
        createSupplierIfNotExists(
            "CV Elektronik Jaya",
            "Jl. Gatot Subroto No. 456, Bandung",
            "02277712340",
            "sales@elektronikjaya.com",
            "Supplier elektronik dan komponen"
        );
        
        createSupplierIfNotExists(
            "UD Mitra Sejahtera",
            "Jl. Ahmad Yani No. 789, Surabaya",
            "03188812340",
            "contact@mitrasejahtera.com",
            "Supplier peralatan kantor dan furniture"
        );
        
        createSupplierIfNotExists(
            "PT Global Tech Solution",
            "Jl. Rasuna Said No. 321, Jakarta Selatan",
            "02199912340",
            "info@globaltech.com",
            "Supplier networking dan server equipment"
        );
        
        createSupplierIfNotExists(
            "CV Digital Media",
            "Jl. Diponegoro No. 654, Yogyakarta",
            "027466612340",
            "sales@digitalmedia.com",
            "Supplier audio, video, dan multimedia"
        );
        
        createSupplierIfNotExists(
            "UD Smartphone Center",
            "Jl. Mangga Dua, Jakarta Utara",
            "02144412340",
            "info@smartphonecenter.com",
            "Supplier smartphone dan tablet"
        );
        
        createSupplierIfNotExists(
            "PT Gaming Pro",
            "Jl. Sudirman No. 999, Surabaya",
            "03155512340",
            "sales@gamingpro.com",
            "Supplier peralatan gaming"
        );
        
        System.out.println("✓ Suppliers initialized");
    }
    
    private void createSupplierIfNotExists(String name, String address, String phone, String email, String description) {
        if (!supplierRepository.existsByEmail(email)) {
            Supplier supplier = new Supplier();
            supplier.setName(name);
            supplier.setAddress(address);
            supplier.setPhoneNumber(phone);
            supplier.setEmail(email);
            supplier.setDescription(description);
            supplierRepository.save(supplier);
        }
    }
    
    private void initializeProducts() {
        // Elektronik products
        createProductWithStock("Laptop Dell XPS 13", 
            "Laptop premium dengan Intel Core i7, 16GB RAM, 512GB SSD", 
            new BigDecimal("18500000"), "Komputer & Laptop", "PT Teknologi Maju", 15, 5);
            
        createProductWithStock("Laptop ASUS ROG", 
            "Gaming laptop dengan RTX 3060, Intel Core i7, 16GB RAM", 
            new BigDecimal("22000000"), "Komputer & Laptop", "PT Teknologi Maju", 8, 3);
            
        createProductWithStock("MacBook Pro 14\"", 
            "MacBook Pro M2 Pro, 16GB RAM, 512GB SSD", 
            new BigDecimal("32000000"), "Komputer & Laptop", "PT Teknologi Maju", 5, 2);
        
        // Smartphones
        createProductWithStock("iPhone 14 Pro", 
            "iPhone 14 Pro 256GB, Deep Purple", 
            new BigDecimal("18999000"), "Smartphone & Tablet", "UD Smartphone Center", 20, 5);
            
        createProductWithStock("Samsung Galaxy S23", 
            "Samsung Galaxy S23 256GB, Phantom Black", 
            new BigDecimal("13999000"), "Smartphone & Tablet", "UD Smartphone Center", 25, 8);
            
        createProductWithStock("iPad Pro 11\"", 
            "iPad Pro 11\" M2, 256GB, WiFi", 
            new BigDecimal("15999000"), "Smartphone & Tablet", "UD Smartphone Center", 12, 3);
        
        // Networking
        createProductWithStock("Router TP-Link AX5400", 
            "WiFi 6 Router dengan kecepatan hingga 5400Mbps", 
            new BigDecimal("1850000"), "Networking", "PT Global Tech Solution", 30, 10);
            
        createProductWithStock("Switch Cisco 24 Port", 
            "Managed Switch 24 Port Gigabit", 
            new BigDecimal("8500000"), "Networking", "PT Global Tech Solution", 10, 3);
            
        createProductWithStock("Access Point Ubiquiti", 
            "UniFi AP AC Pro dengan PoE", 
            new BigDecimal("2100000"), "Networking", "PT Global Tech Solution", 18, 5);
        
        // Audio & Video
        createProductWithStock("Sony WH-1000XM5", 
            "Noise Cancelling Headphone Premium", 
            new BigDecimal("5499000"), "Audio & Video", "CV Digital Media", 22, 8);
            
        createProductWithStock("Logitech Brio 4K", 
            "Webcam 4K Ultra HD dengan HDR", 
            new BigDecimal("3200000"), "Audio & Video", "CV Digital Media", 15, 5);
            
        createProductWithStock("JBL Flip 6", 
            "Portable Bluetooth Speaker Waterproof", 
            new BigDecimal("1899000"), "Audio & Video", "CV Digital Media", 35, 10);
        
        // Office Equipment
        createProductWithStock("Monitor LG 27\"", 
            "Monitor IPS 27\" 4K UHD", 
            new BigDecimal("4500000"), "Peralatan Kantor", "CV Elektronik Jaya", 20, 8);
            
        createProductWithStock("Keyboard Mechanical", 
            "Logitech MX Mechanical Wireless", 
            new BigDecimal("2100000"), "Peralatan Kantor", "CV Elektronik Jaya", 25, 10);
            
        createProductWithStock("Mouse Logitech MX Master 3S", 
            "Wireless Mouse untuk profesional", 
            new BigDecimal("1450000"), "Peralatan Kantor", "CV Elektronik Jaya", 30, 10);
        
        // Gaming
        createProductWithStock("PlayStation 5", 
            "Console gaming Sony PS5 dengan Blu-ray drive", 
            new BigDecimal("7999000"), "Gaming", "PT Gaming Pro", 12, 4);
            
        createProductWithStock("Xbox Series X", 
            "Console gaming Microsoft Xbox Series X", 
            new BigDecimal("7499000"), "Gaming", "PT Gaming Pro", 10, 3);
            
        createProductWithStock("Steam Deck", 
            "Portable Gaming PC 512GB", 
            new BigDecimal("8999000"), "Gaming", "PT Gaming Pro", 6, 2);
        
        // Furniture
        createProductWithStock("Kursi Gaming DXRacer", 
            "Kursi gaming ergonomis dengan lumbar support", 
            new BigDecimal("4500000"), "Furniture", "UD Mitra Sejahtera", 15, 5);
            
        createProductWithStock("Meja Standing Desk", 
            "Meja kerja adjustable elektrik", 
            new BigDecimal("6500000"), "Furniture", "UD Mitra Sejahtera", 8, 3);
        
        // Accessories
        createProductWithStock("USB-C Hub 7-in-1", 
            "Anker USB-C Hub dengan HDMI, USB 3.0, SD Card", 
            new BigDecimal("650000"), "Aksesoris", "CV Elektronik Jaya", 50, 15);
            
        createProductWithStock("Power Bank 20000mAh", 
            "Anker Power Bank dengan fast charging", 
            new BigDecimal("550000"), "Aksesoris", "CV Elektronik Jaya", 60, 20);
            
        createProductWithStock("Charging Cable USB-C", 
            "Kabel charging USB-C 2 meter", 
            new BigDecimal("150000"), "Aksesoris", "CV Elektronik Jaya", 100, 30);
        
        // Electronics Components
        createProductWithStock("SSD Samsung 1TB", 
            "Samsung 980 Pro NVMe SSD 1TB", 
            new BigDecimal("2100000"), "Elektronik", "CV Elektronik Jaya", 25, 10);
            
        createProductWithStock("RAM DDR4 16GB", 
            "Corsair Vengeance 16GB DDR4 3200MHz", 
            new BigDecimal("950000"), "Elektronik", "CV Elektronik Jaya", 40, 15);
        
        System.out.println("✓ Products and Stocks initialized (25 products)");
    }
    
    private void createProductWithStock(String name, String description, 
                                       BigDecimal price, String categoryName, 
                                       String supplierName, int quantity, int minStock) {
        // Check if product with same name already exists
        if (productRepository.findByNameContainingIgnoreCase(name).isEmpty()) {
            Category category = categoryRepository.findByName(categoryName).orElse(null);
            Supplier supplier = supplierRepository.findByEmail(
                supplierName.toLowerCase().replaceAll(" ", "") + ".com"
            ).orElse(supplierRepository.findAll().get(0));
            
            if (category != null) {
                Product product = new Product();
                product.setName(name);
                // SKU will be auto-generated by @PrePersist in Product entity
                product.setDescription(description);
                product.setPrice(price);
                product.setCategory(category);
                product.setSupplier(supplier);
                
                Product savedProduct = productRepository.save(product);
                
                // Create stock for the product
                Stock stock = new Stock();
                stock.setProduct(savedProduct);
                stock.setQuantity(quantity);
                stock.setMinimumStock(minStock);
                stock.setLastRestockDate(LocalDateTime.now().minusDays((long)(Math.random() * 30)));
                stockRepository.save(stock);
            }
        }
    }
}
