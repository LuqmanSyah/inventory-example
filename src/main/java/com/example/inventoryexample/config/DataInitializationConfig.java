package com.example.inventoryexample.config;

import com.example.inventoryexample.entity.User;
import com.example.inventoryexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializationConfig {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            // Create default admin user jika belum ada
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("Administrator");
                admin.setEmail("admin@inventori.com");
                admin.setRole(User.UserRole.ADMIN);
                admin.setIsActive(true);
                userRepository.save(admin);
                System.out.println("✓ Admin user created: admin / admin123");
            } else {
                // Update existing admin password to hashed version if it's plain text
                userRepository.findByUsername("admin").ifPresent(admin -> {
                    if (!admin.getPassword().startsWith("$2a$") && !admin.getPassword().startsWith("$2b$")) {
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        userRepository.save(admin);
                        System.out.println("✓ Admin password updated to hashed version");
                    }
                });
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
        };
    }
}
