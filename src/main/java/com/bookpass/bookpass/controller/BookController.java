package com.bookpass.bookpass.controller;

import com.bookpass.bookpass.dto.request.AddBookRequest;
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

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "title") String type,
            @RequestParam(required = false) String university) {
        return ResponseEntity.ok(bookService.searchBooks(query, type, university));
    }
}