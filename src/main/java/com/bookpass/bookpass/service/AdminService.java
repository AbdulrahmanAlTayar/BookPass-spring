package com.bookpass.bookpass.service;

import com.bookpass.bookpass.constants.SaudiUniversities;
import com.bookpass.bookpass.dto.response.dashboard.*;
import com.bookpass.bookpass.entity.Book;
import com.bookpass.bookpass.entity.User;
import com.bookpass.bookpass.repository.BookRepository;
import com.bookpass.bookpass.repository.TransactionRepository;
import com.bookpass.bookpass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DashboardStatsResponse getDashboardStats() {
        // Metrics
        long totalBooks = bookRepository.count();
        BigDecimal totalRevenue = transactionRepository.sumTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        long availableBooks = bookRepository.countByStatus("AVAILABLE");
        long soldBooks = bookRepository.countByStatus("SOLD");
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByUpdatedAtAfter(LocalDateTime.now().minusDays(30)); 
        long universities = userRepository.countByRole("BOOKSTORE");

        MetricsDTO metrics = MetricsDTO.builder()
                .totalBooks(totalBooks)
                .totalRevenue(totalRevenue)
                .availableBooks(availableBooks)
                .soldBooks(soldBooks)
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .universities(universities)
                .build();

        // University Stats
        List<Object[]> uniCounts = bookRepository.countBooksByAssignedBookstore();
        List<UniversityStatDTO> universityStats = new ArrayList<>();

        for (Object[] row : uniCounts) {
            User bookstore = (User) row[0];
            if (bookstore == null) continue;
            long count = (long) row[1];

            String uniName = bookstore.getStoreName();
            String code = bookstore.getFirstName(); // In DataSeeder, FirstName is used for Acronym

            universityStats.add(new UniversityStatDTO(uniName, code, count));
        }

        // Recent Users
        List<User> recentUserEntities = userRepository.findTop5ByOrderByCreatedAtDesc();
        List<RecentUserDTO> recentUsers = recentUserEntities.stream().map(user -> 
            RecentUserDTO.builder()
                .id(user.getUserId())
                .name(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole())
                .university(user.getStoreAddress()) // As per user instruction
                .joinDate(user.getCreatedAt().toLocalDate())
                .build()
        ).collect(Collectors.toList());

        // Recent Books
        List<Book> recentBookEntities = bookRepository.findTop5ByOrderByCreatedAtDesc();
        List<RecentBookDTO> recentBooks = recentBookEntities.stream().map(book -> 
            RecentBookDTO.builder()
                .id(book.getBookId())
                .title(book.getTitle())
                .price(book.getPrice())
                .status(book.getStatus())
                .build()
        ).collect(Collectors.toList());

        return DashboardStatsResponse.builder()
                .metrics(metrics)
                .universityStats(universityStats)
                .recentUsers(recentUsers)
                .recentBooks(recentBooks)
                .build();
    }
}
