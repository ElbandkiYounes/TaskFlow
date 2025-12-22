package com.taskflowapi.util;

import com.taskflowapi.entity.User;
import com.taskflowapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Seeding test users...");

            User john = new User();
            john.setEmail("john@example.com");
            john.setPasswordHash(passwordEncoder.encode("password123"));
            john.setName("John Doe");
            userRepository.save(john);

            User jane = new User();
            jane.setEmail("jane@example.com");
            jane.setPasswordHash(passwordEncoder.encode("password123"));
            jane.setName("Jane Smith");
            userRepository.save(jane);

            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPasswordHash(passwordEncoder.encode("password123"));
            admin.setName("Admin User");
            userRepository.save(admin);

            log.info("Test users seeded successfully!");
            log.info("You can login with:");
            log.info("  - john@example.com / password123");
            log.info("  - jane@example.com / password123");
            log.info("  - admin@example.com / password123");
        } else {
            log.info("Database already contains users. Skipping seeding.");
        }
    }
}
