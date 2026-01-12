package com.bookpass.bookpass.service;

import com.bookpass.bookpass.entity.Book;
import com.bookpass.bookpass.entity.Transaction;
import com.bookpass.bookpass.entity.User;
import com.bookpass.bookpass.repository.BookRepository;
import com.bookpass.bookpass.repository.TransactionRepository;
import com.bookpass.bookpass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final MoyasarService moyasarService;

    @Transactional
    public Transaction purchaseBook(UUID bookId, String buyerEmail, String paymentId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!"AVAILABLE".equals(book.getStatus())) {
            throw new RuntimeException("Book is not available for purchase");
        }

        // Verify Payment (Server-to-Server)
        moyasarService.verifyPayment(paymentId, book.getPrice());

        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // Create Transaction
        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setBuyer(buyer);
        transaction.setSeller(book.getSeller());
        transaction.setAmount(book.getPrice());
        transaction.setPaymentId(paymentId);
        transaction.setStatus("COMPLETED");

        transactionRepository.save(transaction);

        // Update Book Status
        book.setStatus("SOLD");
        bookRepository.save(book);

        return transaction;
    }
}
