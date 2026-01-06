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

    @Override
    public void run(String... args) {
        // Only seed if database is empty
        if (bookRepository.count() > 0) {
            log.info("Database already has books, skipping seeding");
            return;
        }

        log.info("Seeding database with mock books...");

        // Create a system seller user for mock books
        User systemSeller = userRepository.findByEmail("system@bookpass.com")
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail("system@bookpass.com");
                    user.setPassword(passwordEncoder.encode("system123"));
                    user.setFirstName("BookPass");
                    user.setLastName("System");
                    user.setRole("CUSTOMER");
                    user.setPhoneNumber("0500000000");
                    return userRepository.save(user);
                });

        // Mock books data
        List<BookData> mockBooks = Arrays.asList(
                new BookData("Headway Academic Skills", "Staffs of Oxford", "جامعة الملك سعود", 25, "ممتازة", "9780194742160"),
                new BookData("Introduction to Algorithms", "Thomas H. Cormen", "جامعة الملك فهد", 120, "جيد جداً", "9781285741550"),
                new BookData("Physics for Scientists", "Raymond A. Serway", "جامعة الأميرة نورة", 85, "ممتازة", "9780134414232"),
                new BookData("Calculus: Early Transcendentals", "James Stewart", "جامعة الملك سعود", 95, "جيد", "9781133947271"),
                new BookData("Organic Chemistry", "Paula Yurkanis Bruice", "جامعة القصيم", 110, "مقبول", "9780134093413"),
                new BookData("Database System Concepts", "Abraham Silberschatz", "جامعة الملك خالد", 70, "ممتازة", "9780134743356"),
                new BookData("Computer Networks", "Andrew S. Tanenbaum", "جامعة جدة", 55, "جيد جداً", "9781305585126"),
                new BookData("Engineering Mechanics", "Russell C. Hibbeler", "جامعة الملك فيصل", 90, "ممتازة", "9780133918922"),
                new BookData("مبادئ الإدارة", "د. أحمد الشميمري", "جامعة الملك سعود", 45, "جديد", "9786030123456"),
                new BookData("أساسيات المحاسبة", "د. محمد العمري", "جامعة الملك فهد", 60, "ممتازة", "9786030234567"),
                new BookData("مقدمة في علم النفس", "د. سارة الحربي", "جامعة الأميرة نورة", 35, "جيد جداً", "9786030345678"),
                new BookData("التسويق الرقمي", "د. فهد السعيد", "جامعة القصيم", 75, "جديد", "9786030456789"),
                new BookData("البرمجة بلغة جافا", "د. عبدالله الغامدي", "جامعة الملك خالد", 80, "ممتازة", "9786030567890"),
                new BookData("الرياضيات التطبيقية", "د. نورة القحطاني", "جامعة جدة", 50, "جيد", "9786030678901"),
                new BookData("الفيزياء العامة", "د. خالد المالكي", "جامعة الملك فيصل", 65, "جيد جداً", "9786030789012"),
                new BookData("الكيمياء العضوية", "د. ريم الدوسري", "جامعة الملك سعود", 55, "ممتازة", "9786030890123")
        );

        // Create books
        for (int i = 0; i < mockBooks.size(); i++) {
            BookData data = mockBooks.get(i);
            Book book = new Book();
            book.setSeller(systemSeller);
            book.setTitle(data.title);
            book.setAuthor(data.author);
            book.setUniversity(data.university);
            book.setPrice(BigDecimal.valueOf(data.price));
            book.setBookCondition(data.condition);
            book.setIsbn(data.isbn);
            book.setStatus("AVAILABLE");
            book.setDescription("كتاب جامعي مستعمل بحالة " + data.condition);
            bookRepository.save(book);
        }

        log.info("Successfully seeded {} books", mockBooks.size());
    }

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
}
