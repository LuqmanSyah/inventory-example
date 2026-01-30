package com.example.inventoryexample.config;

import com.example.inventoryexample.entity.User;
import com.example.inventoryexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializationConfig {
    
    private final UserRepository userRepository;
    
    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            // Create default admin user jika belum ada
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
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
                staff.setPassword("staff123");
                staff.setFullName("Staff Member");
                staff.setEmail("staff@inventori.com");
                staff.setRole(User.UserRole.STAFF);
                staff.setIsActive(true);
                userRepository.save(staff);
                System.out.println("✓ Staff user created: staff / staff123");
            }
        };
    }
}
