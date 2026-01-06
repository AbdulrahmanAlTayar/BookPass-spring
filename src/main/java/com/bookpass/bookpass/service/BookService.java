package com.bookpass.bookpass.service;

import com.bookpass.bookpass.dto.request.AddBookRequest;
import com.bookpass.bookpass.dto.response.BookResponse;
import com.bookpass.bookpass.entity.Book;
import com.bookpass.bookpass.entity.User;
import com.bookpass.bookpass.repository.BookRepository;
import com.bookpass.bookpass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * Student adds a book for sale
     */
    public BookResponse addBook(String userEmail, AddBookRequest request) {
        User seller = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = new Book();
        book.setSeller(seller);
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setUniversity(request.getUniversity());
        book.setBookCondition(request.getCondition());
        book.setBookImages(request.getBookImages());
        book.setStatus("AVAILABLE"); // Make it available immediately for now

        bookRepository.save(book);

        return mapToBookResponse(book);
    }

    /**
     * Get all available books for home page
     * Only books that are AVAILABLE and reviewed by bookstore
     */
    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findAllAvailableBooks()
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all active books for marketplace
     */
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAllActiveBooks()
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get books by seller email
     */
    public List<BookResponse> getBooksBySellerEmail(String email) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookRepository.findBySeller_UserId(seller.getUserId())
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get books by seller
     */
    public List<BookResponse> getBooksBySeller(UUID sellerId) {
        return bookRepository.findBySeller_UserId(sellerId)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get pending books (for bookstore review)
     */
    public List<BookResponse> getPendingBooks() {
        return bookRepository.findByStatus("PENDING")
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search books by title
     */
    public List<BookResponse> searchByTitle(String query) {
        return bookRepository.searchByTitle(query)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search books by author
     */
    public List<BookResponse> searchByAuthor(String query) {
        return bookRepository.searchByAuthor(query)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search books by ISBN
     */
    public List<BookResponse> searchByIsbn(String query) {
        return bookRepository.searchByIsbn(query)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search books with filters
     */
    public List<BookResponse> searchBooks(String query, String searchType, String university) {
        List<Book> books;

        if (query != null && !query.isEmpty()) {
            switch (searchType != null ? searchType : "title") {
                case "author":
                    books = bookRepository.searchByAuthor(query);
                    break;
                case "isbn":
                    books = bookRepository.searchByIsbn(query);
                    break;
                default:
                    books = bookRepository.searchByTitle(query);
            }
        } else if (university != null && !university.isEmpty()) {
            books = bookRepository.findByUniversity(university);
        } else {
            books = bookRepository.findAllAvailableBooks();
        }

        // Filter by university if provided and query was used
        if (university != null && !university.isEmpty() && query != null && !query.isEmpty()) {
            books = books.stream()
                    .filter(b -> university.equals(b.getUniversity()))
                    .collect(Collectors.toList());
        }

        return books.stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map Book entity to BookResponse DTO
     */
    private BookResponse mapToBookResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setBookId(book.getBookId());
        response.setTitle(book.getTitle());
        response.setDescription(book.getDescription());
        response.setPrice(book.getPrice());
        response.setStatus(book.getStatus());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setUniversity(book.getUniversity());
        response.setBookImages(book.getBookImages());
        response.setBookCondition(book.getBookCondition());
        response.setReviewNotes(book.getReviewNotes());
        response.setSold("SOLD".equals(book.getStatus()));
        response.setSellerId(book.getSeller().getUserId());
        response.setSellerName(book.getSeller().getFirstName() + " " + book.getSeller().getLastName());
        response.setSellerPhone(book.getSeller().getPhoneNumber());
        response.setCreatedAt(book.getCreatedAt());
        return response;
    }
}