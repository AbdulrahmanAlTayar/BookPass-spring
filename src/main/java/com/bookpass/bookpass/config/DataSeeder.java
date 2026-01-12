package com.bookpass.bookpass.config;

import com.bookpass.bookpass.entity.Book;
import com.bookpass.bookpass.entity.User;
import com.bookpass.bookpass.repository.BookRepository;
import com.bookpass.bookpass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Helper class for book data
    private static class BookData {
        String title;
        String author;
        String university;
        int price;
        String condition;
        String isbn;

        BookData(String title, String author, String university, int price, String condition, String isbn) {
            this.title = title;
            this.author = author;
            this.university = university;
            this.price = price;
            this.condition = condition;
            this.isbn = isbn;
        }
    }

    @Override
    public void run(String... args) throws Exception {
        // Create university reviewers
        for (com.bookpass.bookpass.constants.SaudiUniversities uni : com.bookpass.bookpass.constants.SaudiUniversities.values()) {
            String email = "reviewer@" + uni.getEmailDomain();
            userRepository.findByEmail(email).ifPresentOrElse(
                    user -> log.info("Reviewer for {} already exists", uni.getAcronym()),
                    () -> {
                        User user = new User();
                        user.setEmail(email);
                        user.setPassword(passwordEncoder.encode("Test12341234"));
                        user.setFirstName(uni.getAcronym());
                        user.setLastName("Reviewer");
                        user.setRole("BOOKSTORE");
                        user.setPhoneNumber("0500000000"); // Default phone
                        
                        // Store specific info
                        user.setStoreName(uni.getName());
                        user.setStoreAddress(uni.getAcronym() + " Campus");
                        
                        userRepository.save(user);
                        log.info("Created reviewer for {}", uni.getAcronym());
                    }
            );
        }
    }
}
