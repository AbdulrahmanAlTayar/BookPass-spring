package com.bookpass.bookpass.controller;

import com.bookpass.bookpass.dto.response.BookResponse;
import com.bookpass.bookpass.entity.Transaction;
import com.bookpass.bookpass.entity.User;
import com.bookpass.bookpass.repository.TransactionRepository;
import com.bookpass.bookpass.repository.UserRepository;
import com.bookpass.bookpass.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    /**
     * Get books the current user has purchased
     */
    @GetMapping("/my-purchases")
    public ResponseEntity<List<BookResponse>> getMyPurchases(Principal principal) {
        User buyer = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = transactionRepository.findByBuyer_UserId(buyer.getUserId());

        List<BookResponse> purchasedBooks = transactions.stream()
                .map(bookService::mapTransactionToBookResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(purchasedBooks);
    }

    /**
     * Get books the current user has sold
     */
    @GetMapping("/my-sales")
    public ResponseEntity<List<BookResponse>> getMySales(Principal principal) {
        User seller = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = transactionRepository.findBySeller_UserId(seller.getUserId());

        List<BookResponse> soldBooks = transactions.stream()
                .map(bookService::mapTransactionToBookResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(soldBooks);
    }
}
