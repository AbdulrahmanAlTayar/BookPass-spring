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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Checkout multiple books (cart) with a single payment
     */
    @Transactional
    public List<Transaction> checkoutCart(List<UUID> bookIds, String buyerEmail, String paymentId) {
        if (bookIds == null || bookIds.isEmpty()) {
            throw new RuntimeException("No books provided for checkout");
        }

        // 1. Fetch all books and validate availability
        List<Book> books = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (UUID bookId : bookIds) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));

            if (!"AVAILABLE".equals(book.getStatus())) {
                throw new RuntimeException("Book is not available for purchase: " + book.getTitle());
            }

            books.add(book);
            totalAmount = totalAmount.add(book.getPrice());
        }

        // 2. Verify Payment with total amount (Server-to-Server)
        moyasarService.verifyPayment(paymentId, totalAmount);

        // 3. Get buyer
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // 4. Create transactions for each book and update status
        List<Transaction> transactions = new ArrayList<>();

        for (Book book : books) {
            Transaction transaction = new Transaction();
            transaction.setBook(book);
            transaction.setBuyer(buyer);
            transaction.setSeller(book.getSeller());
            transaction.setAmount(book.getPrice());
            transaction.setPaymentId(paymentId);
            transaction.setStatus("COMPLETED");

            transactionRepository.save(transaction);
            transactions.add(transaction);

            // Mark book as SOLD
            book.setStatus("SOLD");
            bookRepository.save(book);
        }

        return transactions;
    }
}

