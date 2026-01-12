package com.bookpass.bookpass.controller;

import com.bookpass.bookpass.dto.request.AddBookRequest;
import com.bookpass.bookpass.dto.request.ReviewBookRequest;
import com.bookpass.bookpass.dto.response.BookResponse;
import com.bookpass.bookpass.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final com.bookpass.bookpass.service.TransactionService transactionService;

    @PostMapping
    public ResponseEntity<BookResponse> addBook(
            @Valid @RequestBody AddBookRequest request,
            Principal principal) {
        BookResponse book = bookService.addBook(principal.getName(), request);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/available")
    public ResponseEntity<List<BookResponse>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/my-books")
    public ResponseEntity<List<BookResponse>> getMyBooks(Principal principal) {
        return ResponseEntity.ok(bookService.getBooksBySellerEmail(principal.getName()));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BookResponse>> getPendingBooks() {
        return ResponseEntity.ok(bookService.getPendingBooks());
    }

    @GetMapping("/store/pending")
    public ResponseEntity<List<BookResponse>> getStorePendingBooks(Principal principal) {
        return ResponseEntity.ok(bookService.getStorePendingBooks(principal.getName()));
    }

    @GetMapping("/store/sold")
    public ResponseEntity<List<BookResponse>> getStoreSoldBooks(Principal principal) {
        return ResponseEntity.ok(bookService.getStoreSoldBooks(principal.getName()));
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<BookResponse> reviewBook(
            @PathVariable java.util.UUID id,
            @Valid @RequestBody ReviewBookRequest request,
            Principal principal) {
        return ResponseEntity.ok(bookService.reviewBook(id, principal.getName(), request));
    }

    @PutMapping("/{id}/picked")
    public ResponseEntity<BookResponse> markAsPicked(
            @PathVariable java.util.UUID id,
            Principal principal) {
        return ResponseEntity.ok(bookService.markAsPicked(id, principal.getName()));
    }

    @PutMapping("/{id}/purchase")
    public ResponseEntity<BookResponse> purchaseBook(
            @PathVariable java.util.UUID id,
            @Valid @RequestBody com.bookpass.bookpass.dto.request.PurchaseRequest request,
            Principal principal) {
        // Delegate to TransactionService to handle logic and Moyasar check
        com.bookpass.bookpass.entity.Transaction transaction = transactionService.purchaseBook(id, principal.getName(), request.getPaymentId());
        
        // Map transaction back to BookResponse using helper
        return ResponseEntity.ok(bookService.mapTransactionToBookResponse(transaction));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "title") String type,
            @RequestParam(required = false) String university) {
        return ResponseEntity.ok(bookService.searchBooks(query, type, university));
    }
}